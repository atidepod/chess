package ui;
import chess.*;
import model.GameData;
import client.ServerFacade;
import service.ListGameResult;
import service.LoginResult;
import service.RegisterResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Repl {

    private final ServerFacade server;
    private final Scanner scanner;
    private List<GameData> cachedGames = new ArrayList<>();
    private boolean loggedIn;
    private String authToken;
    private String username;

    public Repl(String serverUrl) {
        server = new ServerFacade(serverUrl);
        scanner = new Scanner(System.in);
        loggedIn = false;
        authToken = null;
        username = null;
    }

    public void run() {

        System.out.println("Welcome to Chess");

        while (true) {

            try {

                if (!loggedIn) {
                    executePreLogin();
                }
                else {
                    executePostLogin();
                }

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private void executePreLogin() throws Exception {

        System.out.print("[LOGGED OUT] >>> ");

        String command = scanner.nextLine().trim().toLowerCase();

        switch (command) {

            case "help":
                PreLogin.help();
                break;

            case "login":
                login();
                break;

            case "register":
                register();
                break;

            case "quit":
                System.exit(0);
                break;

            default:
                System.out.println("Unknown command.");
        }
    }

    private void login() throws Exception {

        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        LoginResult result = server.login(username, password);

        this.username = result.username();
        this.authToken = result.authToken();

        loggedIn = true;

        System.out.println("Successfully logged in.");
    }
    private void logout() throws Exception {

        server.logout(authToken);

        authToken = null;
        username = null;
        loggedIn = false;

        System.out.println("Logged out.");
    }
    private void createGame() throws Exception {

        System.out.print("Game Name: ");
        String gameName = scanner.nextLine();

        server.createGame(authToken, gameName);

        System.out.println("Game created.");
    }

    private void register() throws Exception {

        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        RegisterResult result =server.register(username,password,email);

        this.username = result.username();
        this.authToken = result.authToken();

        loggedIn = true;

        System.out.println("Successfully registered.");
    }
    private void listGames() throws Exception {

        ListGameResult result =
                server.listGame(authToken);

        cachedGames = new ArrayList<>(result.games());

        if (cachedGames.isEmpty()) {
            System.out.println("No games found.");
            return;
        }

        for (int i = 0; i < cachedGames.size(); i++) {

            GameData game = cachedGames.get(i);

            String white =
                    game.whiteUsername() == null
                            ? "[EMPTY]"
                            : game.whiteUsername();

            String black =
                    game.blackUsername() == null
                            ? "[EMPTY]"
                            : game.blackUsername();

            System.out.printf("%d. %s (White: %s, Black: %s)%n", i + 1, game.gameName(), white, black);
        }
    }
    private void playGame() throws Exception {

        if (cachedGames.isEmpty()) {

            System.out.println("Use list first to view games.");

            return;
        }
        System.out.print("Game Number: ");

        int gameNumber = Integer.parseInt(scanner.nextLine());
        int index = gameNumber - 1;
        if (index < 0 || index >= cachedGames.size()) {

            System.out.println("Invalid game number.");

            return;
        }

        System.out.print("Color (WHITE/BLACK): ");

        String color =scanner.nextLine().trim().toUpperCase();
        if (!color.equals("WHITE") && !color.equals("BLACK")) {

            System.out.println("Invalid color");
            return;
        }

        GameData selected = cachedGames.get(gameNumber - 1);
        server.joinGame(authToken,selected.gameID(),color);

        System.out.println("Joined game.");
        ChessPainter.printBoard(selected.game(), color.equals("WHITE"));
    }
    private void observeGame() {

        if (cachedGames.isEmpty()) {

            System.out.println("Use list first to view games.");

            return;
        }

        System.out.print("Game Number: ");

        int gameNumber =Integer.parseInt(scanner.nextLine());
        int index = gameNumber - 1;

        if (index < 0 || index >= cachedGames.size()) {
            System.out.println("Invalid game number.");
            return;
        }

        GameData selected = cachedGames.get(index);
        System.out.println("Observing: "+ selected.gameName());
        ChessPainter.printBoard(selected.game(), true);

    }

    private void executePostLogin() throws Exception {

        System.out.print("[" + username + "] >>> ");
        String command =scanner.nextLine().trim().toLowerCase();
        switch (command) {

            case "help":
                PostLogin.help();
                break;

            case "logout":
                logout();
                break;

            case "create":
                createGame();
                break;

            case "list":
                listGames();
                break;

            case "play":
                playGame();
                break;

            case "observe":
                observeGame();
                break;

            default:
                System.out.println("Unknown command.");
        }
    }


}
