param([ValidateSet('Start', 'Stop')][string]$Action = 'Start')

$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$mysqlRoot = Join-Path $projectRoot '.local/mysql'
$distribution = Join-Path $mysqlRoot 'mysql-8.4.9-winx64'
$mysqlServer = Join-Path $distribution 'bin/mysqld.exe'
$mysqlClient = Join-Path $distribution 'bin/mysql.exe'
$serverConfig = Join-Path $mysqlRoot 'my.ini'
$clientConfig = Join-Path $mysqlRoot 'client.cnf'
$bootstrapConfig = Join-Path $mysqlRoot 'bootstrap.cnf'
$pidFile = Join-Path $mysqlRoot 'mysql.pid'
$dataDirectory = Join-Path $mysqlRoot 'data'
$utf8 = [System.Text.UTF8Encoding]::new($false)

function Get-ManagedMySql {
    if (!(Test-Path -LiteralPath $pidFile)) { return $null }
    $mysqlProcessId = (Get-Content -LiteralPath $pidFile -Raw).Trim()
    if ($mysqlProcessId -notmatch '^\d+$') { return $null }
    $mysqlProcess = Get-Process -Id $mysqlProcessId -ErrorAction SilentlyContinue
    if (!$mysqlProcess -or $mysqlProcess.Path -ne $mysqlServer) { return $null }
    $details = Get-CimInstance Win32_Process -Filter "ProcessId = $mysqlProcessId"
    if (!$details.CommandLine -or $details.CommandLine.IndexOf($serverConfig,
            [System.StringComparison]::OrdinalIgnoreCase) -lt 0) { return $null }
    return $mysqlProcess
}

function Write-PrivateOptions([string]$Path, [string]$Password, [int]$Port) {
    if (!(Test-Path -LiteralPath $Path)) { [System.IO.File]::WriteAllText($Path, '') }
    $permissions = [System.IO.File]::GetAccessControl($Path,
        [System.Security.AccessControl.AccessControlSections]::Access)
    $permissions.SetAccessRuleProtection($true, $false)
    $identity = [System.Security.Principal.WindowsIdentity]::GetCurrent().User
    $permissions.AddAccessRule([System.Security.AccessControl.FileSystemAccessRule]::new(
        $identity, 'FullControl', 'Allow'))
    [System.IO.File]::SetAccessControl($Path, $permissions)
    $escapedPassword = $Password.Replace('\', '\\').Replace('"', '\"').
        Replace("`r", '\r').Replace("`n", '\n')
    $options = "[client]`nhost=127.0.0.1`nport=$Port`nprotocol=tcp`nuser=root`npassword=`"$escapedPassword`"`n"
    [System.IO.File]::WriteAllText($Path, $options, $utf8)
}

function Invoke-MySql([string]$Options, [string]$Sql = 'SELECT 1;') {
    $clientProcess = [System.Diagnostics.Process]::new()
    $clientProcess.StartInfo.FileName = $mysqlClient
    $clientProcess.StartInfo.Arguments = '--defaults-extra-file="{0}" --connect-timeout=1 --default-character-set=utf8mb4 --batch --skip-column-names' -f $Options
    $clientProcess.StartInfo.UseShellExecute = $false
    $clientProcess.StartInfo.CreateNoWindow = $true
    $clientProcess.StartInfo.RedirectStandardInput = $true
    $clientProcess.StartInfo.RedirectStandardOutput = $true
    $clientProcess.StartInfo.RedirectStandardError = $true
    try {
        [void]$clientProcess.Start()
        $sqlBytes = $utf8.GetBytes($Sql + "`n")
        $clientProcess.StandardInput.BaseStream.Write($sqlBytes, 0, $sqlBytes.Length)
        $clientProcess.StandardInput.BaseStream.Flush()
        $clientProcess.StandardInput.Close()
        [void]$clientProcess.StandardOutput.ReadToEnd()
        [void]$clientProcess.StandardError.ReadToEnd()
        $clientProcess.WaitForExit()
        return $clientProcess.ExitCode -eq 0
    } finally { $clientProcess.Dispose() }
}

$managedMySql = Get-ManagedMySql
if ($Action -eq 'Stop') {
    if (!$managedMySql) { Write-Output 'Portable MySQL is not running.'; return }
    if (!(Test-Path -LiteralPath $clientConfig)) { throw 'Missing local MySQL client credentials; no process was stopped.' }
    & (Join-Path $distribution 'bin/mysqladmin.exe') "--defaults-extra-file=$clientConfig" --connect-timeout=2 shutdown 2>&1 | Out-Null
    if ($LASTEXITCODE -ne 0) { throw 'MySQL shutdown failed; check the local client credentials.' }
    if (!$managedMySql.WaitForExit(15000)) { throw 'MySQL has not finished shutting down.' }
    Write-Output 'Portable MySQL stopped. Its data has been preserved.'
    return
}

$propertiesPath = Join-Path $projectRoot 'server/src/main/resources/db.properties'
$properties = Get-Content -LiteralPath $propertiesPath -Raw | ConvertFrom-StringData
if ($properties['db.host'] -notin @('localhost', '127.0.0.1', '::1')) {
    throw 'This script starts a local database; db.host must refer to localhost.'
}
if ($properties['db.user'] -cne 'root') { throw 'This local setup expects db.user=root in db.properties.' }
$port = [int]$properties['db.port']
if ($port -lt 1 -or $port -gt 65535) { throw 'db.port must be between 1 and 65535.' }
$password = [string]$properties['db.password']

if (!$managedMySql) {
    $probe = [System.Net.Sockets.TcpClient]::new()
    try {
        $connection = $probe.ConnectAsync('127.0.0.1', $port)
        try { [void]$connection.Wait(500) } catch { }
        if ($probe.Connected) { throw "Port $port is already in use by another process; it was left running." }
    } finally { $probe.Dispose() }
}

New-Item -ItemType Directory -Path $mysqlRoot -Force | Out-Null
if (!(Test-Path -LiteralPath $mysqlServer)) {
    $downloads = Join-Path $mysqlRoot 'downloads'
    New-Item -ItemType Directory -Path $downloads -Force | Out-Null
    $archive = Join-Path $downloads 'mysql-8.4.9-winx64.zip'
    if (!(Test-Path -LiteralPath $archive)) {
        Write-Output 'Downloading MySQL Community Server 8.4.9...'
        Invoke-WebRequest -UseBasicParsing -Uri 'https://cdn.mysql.com/archives/mysql-8.4/mysql-8.4.9-winx64.zip' -OutFile $archive
    }
    $expectedHash = '5795BA250E89290F7507ED3BCC6A655BE373616ABB58B877ACDEA71E1B8F4E8C'
    if ((Get-FileHash -LiteralPath $archive -Algorithm SHA256).Hash -ne $expectedHash) {
        throw 'The downloaded MySQL archive failed SHA256 verification; it was not extracted.'
    }
    Expand-Archive -LiteralPath $archive -DestinationPath $mysqlRoot -Force
}

if (!(Test-Path -LiteralPath (Join-Path $dataDirectory 'mysql')) ) {
    if ((Test-Path -LiteralPath $dataDirectory) -and
            (Get-ChildItem -LiteralPath $dataDirectory -Force | Select-Object -First 1)) {
        throw 'The local data directory is not empty but is not initialized. Its contents were preserved.'
    }
    $initializeArguments = @('--no-defaults', '--initialize-insecure',
        "--basedir=`"$distribution`"", "--datadir=`"$dataDirectory`"",
        "--log-error=`"$(Join-Path $mysqlRoot 'initialize.log')`"")
    $initializeProcess = Start-Process -FilePath $mysqlServer -ArgumentList $initializeArguments -WindowStyle Hidden -Wait -PassThru
    if ($initializeProcess.ExitCode -ne 0) { throw 'MySQL initialization failed; see .local/mysql/initialize.log.' }
}

Write-PrivateOptions $clientConfig $password $port
Write-PrivateOptions $bootstrapConfig '' $port
if (!$managedMySql) {
    $serverOptions = @(
        '[mysqld]',
        ('basedir="{0}"' -f $distribution.Replace('\', '/')),
        ('datadir="{0}"' -f $dataDirectory.Replace('\', '/')),
        ('pid-file="{0}"' -f $pidFile.Replace('\', '/')),
        ('log-error="{0}"' -f (Join-Path $mysqlRoot 'mysql.log').Replace('\', '/')),
        'bind-address=127.0.0.1', "port=$port", 'mysqlx=0', 'innodb-buffer-pool-size=64M')
    [System.IO.File]::WriteAllText($serverConfig, ($serverOptions -join "`n") + "`n", $utf8)
    $managedMySql = Start-Process -FilePath $mysqlServer -ArgumentList "--defaults-file=`"$serverConfig`"" -WindowStyle Hidden -PassThru
}

$deadline = [DateTime]::UtcNow.AddSeconds(60)
do {
    if ($managedMySql.HasExited) { throw 'MySQL stopped during startup; see .local/mysql/mysql.log.' }
    if (Invoke-MySql $clientConfig) {
        Write-Output "Portable MySQL is ready on 127.0.0.1:$port."
        return
    }
    if (Invoke-MySql $bootstrapConfig) {
        $passwordLiteral = "'" + $password.Replace("'", "''") + "'"
        $configureSql = "SET SESSION sql_mode = 'NO_BACKSLASH_ESCAPES'; ALTER USER 'root'@'localhost' IDENTIFIED BY $passwordLiteral;"
        if (!(Invoke-MySql $bootstrapConfig $configureSql)) { throw 'Unable to configure the local MySQL account.' }
    }
    Start-Sleep -Milliseconds 250
} while ([DateTime]::UtcNow -lt $deadline)
throw 'MySQL did not become ready. Check .local/mysql/mysql.log and the credentials in db.properties.'
