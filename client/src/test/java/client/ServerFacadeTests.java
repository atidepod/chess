package client;

import org.junit.jupiter.api.*;
import server.Server;
import service.*;
import java.util.UUID;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    private String newUsername(String prefix) {
        return prefix + UUID.randomUUID().toString().replace("-", "");
    }

    @Test
    public void loginSuccess() throws Exception {
        String username = newUsername("login");

        facade.register(username, "password", username + "@test.com");

        LoginResult result = facade.login(username, "password");

        Assertions.assertEquals(username, result.username());
        Assertions.assertNotNull(result.authToken());
    }

    @Test
    public void loginFailure() {
        Assertions.assertThrows(
                Exception.class,
                () -> facade.login("wrongUser", "wrongPassword")
        );
    }

    @Test
    public void logoutSuccess() throws Exception {
        String username = newUsername("logout");

        RegisterResult result = facade.register(
                username, "password", username + "@test.com"
        );

        Assertions.assertDoesNotThrow(
                () -> facade.logout(result.authToken())
        );
    }

    @Test
    public void logoutFailure() {
        Assertions.assertThrows(
                Exception.class,
                () -> facade.logout("invalidToken")
        );
    }

    @Test
    public void listGameSuccess() throws Exception {
        String username = newUsername("list");

        RegisterResult result = facade.register(
                username, "password", username + "@test.com"
        );

        ListGameResult games = facade.listGame(result.authToken());

        Assertions.assertNotNull(games);
        Assertions.assertNotNull(games.games());
    }

    @Test
    public void listGameFailure() {
        Assertions.assertThrows(
                Exception.class,
                () -> facade.listGame("invalidToken")
        );
    }

    @Test
    public void createGameSuccess() throws Exception {
        String username = newUsername("create");

        RegisterResult result = facade.register(
                username, "password", username + "@test.com"
        );

        int gameID = facade.createGame(result.authToken(), "Test Game");

        Assertions.assertTrue(gameID > 0);
    }

    @Test
    public void createGameFailure() {
        Assertions.assertThrows(
                Exception.class,
                () -> facade.createGame("invalidToken", "Test Game")
        );
    }

    @Test
    public void joinGameSuccess() throws Exception {
        String username = newUsername("join");

        RegisterResult result = facade.register(
                username, "password", username + "@test.com"
        );

        int gameID = facade.createGame(result.authToken(), "Join Test Game");

        Assertions.assertDoesNotThrow(
                () -> facade.joinGame(result.authToken(), gameID, "WHITE")
        );
    }

    @Test
    public void joinGameFailure() {
        Assertions.assertThrows(
                Exception.class,
                () -> facade.joinGame("invalidToken", 999, "WHITE")
        );
    }

    @Test
    public void registerSuccess() throws Exception {
        String username = newUsername("register");

        RegisterResult result = facade.register(
                username, "password", username + "@test.com"
        );

        Assertions.assertEquals(username, result.username());
        Assertions.assertNotNull(result.authToken());
    }

    @Test
    public void registerFailure() throws Exception {
        String username = newUsername("duplicate");

        facade.register(username, "password", username + "@test.com");

        Assertions.assertThrows(
                Exception.class,
                () -> facade.register(username, "password", username + "@test.com")
        );
    }
}