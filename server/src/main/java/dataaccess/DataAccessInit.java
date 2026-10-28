package dataaccess;


import java.sql.SQLException;

public class DataAccessInit {


    public static void initDB () throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            var users = """
            
                    CREATE TABLE IF NOT EXISTS users (
                username VARCHAR(255) NOT NULL,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(255) NOT NULL,
                PRIMARY KEY (username)
            )
            """;

            var auth =
                    """
            CREATE TABLE IF NOT
                    EXISTS auth (
                                authToken VARCHAR(255) NOT NULL,
                                username VARCHAR(255) NOT NULL,
                                PRIMARY KEY (authToken)
            )
            """;

            var games = """
            CREATE
                    TABLE IF NOT EXISTS games (
                        gameID INT NOT NULL AUTO_INCREMENT,
                        whiteUsername VARCHAR(255),
                        blackUsername VARCHAR(255),
                        gameName VARCHAR(255) NOT NULL,
                        game TEXT NOT NULL,
                        PRIMARY KEY (gameID)
            )
             """;
            try (var statement = conn.createStatement()){
                statement.executeUpdate(users);
                statement.executeUpdate(
                        auth);
                statement.executeUpdate(games);
            }



        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        catch(DataAccessException e){
            throw new DataAccessException(e.getMessage());
        }

    }

}
