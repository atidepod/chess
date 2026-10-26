package service;

import dataaccess.DataAccessException;
import dataaccess.memoryAuthDao;
import dataaccess.memoryGameDao;
import dataaccess.memoryUserDao;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.naming.AuthenticationException;

import io.javalin.http.BadRequestResponse;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private memoryUserDao users;
    private memoryAuthDao auths;
    private memoryGameDao games;
    private UserService service;

    @BeforeEach
    void setUp() {
        users = new memoryUserDao();
        auths = new memoryAuthDao();
        games = new memoryGameDao();
        service = new UserService(users, auths, games);
    }

    @Test
    void registerCreatesUserAndAuthToken() throws Exception {
        registerResult result = service.register(
                new registerRequest("alice", "password", "alice@example.com"));

        assertEquals("alice", result.username());
        assertNotNull(result.authToken());
        assertFalse(result.authToken().isBlank());

        UserData storedUser = users.getUser("alice");
        assertNotNull(storedUser);
        assertEquals("alice", storedUser.username());
        assertEquals("password", storedUser.password());
        assertEquals("alice@example.com", storedUser.email());

        AuthData storedAuth = auths.getAuth(result.authToken());
        assertNotNull(storedAuth);
        assertEquals("alice", storedAuth.username());
    }

    @Test
    void registerRejectsMissingOrBlankFields() {
        assertThrows(AuthenticationException.class,
                () -> service.register(new registerRequest(null, "pw", "a@b.com")));
        assertThrows(AuthenticationException.class,
                () -> service.register(new registerRequest("alice", null, "a@b.com")));
        assertThrows(AuthenticationException.class,
                () -> service.register(new registerRequest("alice", "pw", null)));
        assertThrows(AuthenticationException.class,
                () -> service.register(new registerRequest(" ", "pw", "a@b.com")));
        assertThrows(AuthenticationException.class,
                () -> service.register(new registerRequest("alice", " ", "a@b.com")));
        assertThrows(AuthenticationException.class,
                () -> service.register(new registerRequest("alice", "pw", " ")));
    }

    @Test
    void registerRejectsDuplicateUsername() throws Exception {
        service.register(new registerRequest("alice", "password", "alice@example.com"));

        AuthenticationException error = assertThrows(AuthenticationException.class,
                () -> service.register(new registerRequest("alice", "different", "other@example.com")));

        assertEquals("Username already exists", error.getMessage());
    }

    @Test
    void loginReturnsNewValidToken() throws Exception {
        service.register(new registerRequest("alice", "password", "alice@example.com"));

        loginResult result = service.login(new loginRequest("alice", "password"));

        assertEquals("alice", result.username());
        assertNotNull(result.authToken());
        assertNotNull(auths.getAuth(result.authToken()));
        assertEquals("alice", auths.getAuth(result.authToken()).username());
    }

    @Test
    void loginRejectsNullOrIncompleteRequest() {
        assertThrows(AuthenticationException.class, () -> service.login(null));
        assertThrows(AuthenticationException.class, () -> service.login(new loginRequest(null, "pw")));
        assertThrows(AuthenticationException.class, () -> service.login(new loginRequest("alice", null)));
        assertThrows(AuthenticationException.class, () -> service.login(new loginRequest("", "pw")));
        assertThrows(AuthenticationException.class, () -> service.login(new loginRequest("alice", " ")));
    }

    @Test
    void loginRejectsUnknownUserAndWrongPassword() throws Exception {
        AuthenticationException unknownUser = assertThrows(AuthenticationException.class,
                () -> service.login(new loginRequest("missing", "password")));
        assertEquals("Unauthorized", unknownUser.getMessage());

        service.register(new registerRequest("alice", "password", "alice@example.com"));
        AuthenticationException wrongPassword = assertThrows(AuthenticationException.class,
                () -> service.login(new loginRequest("alice", "wrong")));
        assertEquals("Unauthorized", wrongPassword.getMessage());
    }

    @Test
    void loginCreatesDistinctTokensForSeparateLogins() throws Exception {
        service.register(new registerRequest("alice", "password", "alice@example.com"));

        loginResult first = service.login(new loginRequest("alice", "password"));
        loginResult second = service.login(new loginRequest("alice", "password"));

        assertNotEquals(first.authToken(), second.authToken());
        assertNotNull(auths.getAuth(first.authToken()));
        assertNotNull(auths.getAuth(second.authToken()));
    }

    @Test
    void logoutRemovesExistingToken() throws Exception {
        registerResult registered = service.register(
                new registerRequest("alice", "password", "alice@example.com"));
        assertNotNull(auths.getAuth(registered.authToken()));

        service.logout(new logoutRequest(registered.authToken()));

        assertNull(auths.getAuth(registered.authToken()));
    }

    @Test
    void logoutRejectsNullRequestMissingTokenAndUnknownToken() throws Exception {
        assertThrows(BadRequestResponse.class, () -> service.logout(null));
        AuthenticationException missing = assertThrows(AuthenticationException.class,
                () -> service.logout(new logoutRequest(null)));
        assertEquals("Unauthorized", missing.getMessage());

        AuthenticationException blank = assertThrows(AuthenticationException.class,
                () -> service.logout(new logoutRequest(" ")));
        assertEquals("Unauthorized", blank.getMessage());

        AuthenticationException unknown = assertThrows(AuthenticationException.class,
                () -> service.logout(new logoutRequest("not-a-token")));
        assertEquals("Unauthorized", unknown.getMessage());
    }

    @Test
    void listGamesReturnsAllCreatedGamesForAuthenticatedUser() throws Exception {
        registerResult registered = service.register(
                new registerRequest("alice", "password", "alice@example.com"));
        int firstId = service.createGame(new createGameRequest(registered.authToken(), "First")).gameID();
        int secondId = service.createGame(new createGameRequest(registered.authToken(), "Second")).gameID();

        listGameResult result = service.listGame(new listGameRequest(registered.authToken()));

        assertNotNull(result.games());
        assertEquals(2, result.games().size());
        assertTrue(result.games().stream().anyMatch(g -> g.gameID() == firstId && g.gameName().equals("First")));
        assertTrue(result.games().stream().anyMatch(g -> g.gameID() == secondId && g.gameName().equals("Second")));
    }

    @Test
    void listGamesReturnsEmptyCollectionWhenNoGamesExist() throws Exception {
        registerResult registered = service.register(
                new registerRequest("alice", "password", "alice@example.com"));

        listGameResult result = service.listGame(new listGameRequest(registered.authToken()));

        assertNotNull(result.games());
        assertTrue(result.games().isEmpty());
    }

    @Test
    void listGamesRejectsNullRequestAndInvalidToken() throws Exception {
        assertThrows(BadRequestResponse.class, () -> service.listGame(null));
        assertThrows(AuthenticationException.class, () -> service.listGame(new listGameRequest(null)));
        assertThrows(AuthenticationException.class, () -> service.listGame(new listGameRequest("")));
        assertThrows(AuthenticationException.class, () -> service.listGame(new listGameRequest("invalid")));
    }

    @Test
    void createGameStoresGameWithExpectedDetails() throws Exception {
        registerResult registered = service.register(
                new registerRequest("alice", "password", "alice@example.com"));

        createGameResult result = service.createGame(
                new createGameRequest(registered.authToken(), "My game"));

        assertTrue(result.gameID() > 0);
        GameData stored = games.getGame(result.gameID());
        assertEquals(result.gameID(), stored.gameID());
        assertEquals("My game", stored.gameName());
        assertNull(stored.whiteUsername());
        assertNull(stored.blackUsername());
        assertNotNull(stored.game());
    }

    @Test
    void createGameRejectsNullRequestInvalidTokenAndMissingName() throws Exception {
        assertThrows(BadRequestResponse.class, () -> service.createGame(null));
        assertThrows(AuthenticationException.class,
                () -> service.createGame(new createGameRequest(null, "game")));
        assertThrows(AuthenticationException.class,
                () -> service.createGame(new createGameRequest("invalid", "game")));

        registerResult registered = service.register(
                new registerRequest("alice", "password", "alice@example.com"));
        assertThrows(BadRequestResponse.class,
                () -> service.createGame(new createGameRequest(registered.authToken(), null)));
        assertThrows(BadRequestResponse.class,
                () -> service.createGame(new createGameRequest(registered.authToken(), " ")));
    }

    @Test
    void joinGameClaimsWhiteAndBlackSlots() throws Exception {
        registerResult alice = service.register(
                new registerRequest("alice", "password", "alice@example.com"));
        registerResult bob = service.register(
                new registerRequest("bob", "password", "bob@example.com"));
        int gameId = service.createGame(new createGameRequest(alice.authToken(), "Game")).gameID();

        service.joinGame(new joinGameRequest(alice.authToken(), "WHITE", gameId));
        service.joinGame(new joinGameRequest(bob.authToken(), "BLACK", gameId));

        GameData stored = games.getGame(gameId);
        assertEquals("alice", stored.whiteUsername());
        assertEquals("bob", stored.blackUsername());
        assertEquals("Game", stored.gameName());
    }

    @Test
    void joinGameRejectsNullRequestInvalidTokenBadColorAndNonpositiveId() throws Exception {
        assertThrows(BadRequestResponse.class, () -> service.joinGame(null));
        assertThrows(AuthenticationException.class,
                () -> service.joinGame(new joinGameRequest(null, "WHITE", 1)));
        assertThrows(AuthenticationException.class,
                () -> service.joinGame(new joinGameRequest("invalid", "WHITE", 1)));
        assertThrows(BadRequestResponse.class,
                () -> service.joinGame(new joinGameRequest("token", null, 1)));
        assertThrows(BadRequestResponse.class,
                () -> service.joinGame(new joinGameRequest("token", "", 1)));
        assertThrows(BadRequestResponse.class,
                () -> service.joinGame(new joinGameRequest("token", "GREEN", 1)));
        assertThrows(BadRequestResponse.class,
                () -> service.joinGame(new joinGameRequest("token", "WHITE", 0)));
        assertThrows(BadRequestResponse.class,
                () -> service.joinGame(new joinGameRequest("token", "WHITE", -1)));
    }

    @Test
    void joinGameRejectsNonexistentGame() throws Exception {
        registerResult registered = service.register(
                new registerRequest("alice", "password", "alice@example.com"));

        assertThrows(DataAccessException.class,
                () -> service.joinGame(new joinGameRequest(registered.authToken(), "WHITE", 999)));
    }

    @Test
    void joinGameRejectsAlreadyClaimedColor() throws Exception {
        registerResult alice = service.register(
                new registerRequest("alice", "password", "alice@example.com"));
        registerResult bob = service.register(
                new registerRequest("bob", "password", "bob@example.com"));
        int gameId = service.createGame(new createGameRequest(alice.authToken(), "Game")).gameID();

        service.joinGame(new joinGameRequest(alice.authToken(), "WHITE", gameId));

        DataAccessException error = assertThrows(DataAccessException.class,
                () -> service.joinGame(new joinGameRequest(bob.authToken(), "WHITE", gameId)));
        assertEquals("already taken", error.getMessage());

        GameData stored = games.getGame(gameId);
        assertEquals("alice", stored.whiteUsername());
        assertNull(stored.blackUsername());
    }

    @Test
    void clearRemovesUsersAuthTokensAndGames() throws Exception {
        registerResult registered = service.register(
                new registerRequest("alice", "password", "alice@example.com"));
        service.createGame(new createGameRequest(registered.authToken(), "Game"));

        service.clear();

        assertNull(users.getUser("alice"));
        assertNull(auths.getAuth(registered.authToken()));
        assertTrue(games.listGames().isEmpty());
        assertThrows(AuthenticationException.class,
                () -> service.login(new loginRequest("alice", "password")));
    }

    @Test
    void clearCanBeCalledMoreThanOnce() throws Exception {
        service.clear();
        service.clear();

        assertTrue(games.listGames().isEmpty());
    }
}
