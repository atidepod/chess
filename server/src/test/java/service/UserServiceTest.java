package service;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDao;
import dataaccess.MemoryGameDao;
import dataaccess.MemoryUserDao;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.naming.AuthenticationException;

import io.javalin.http.BadRequestResponse;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private MemoryUserDao users;
    private MemoryAuthDao auths;
    private MemoryGameDao games;
    private UserService service;
    private UserHandler handler;
    private MemoryUserDao users;
    private MemoryAuthDao auths;
    private MemoryGameDao games;
    private final Gson gson = new Gson();

    @BeforeEach
    void setUp() {
        users = new MemoryUserDao();
        auths = new MemoryAuthDao();
        games = new MemoryGameDao();
        service = new UserService(users, auths, games);
        handler = new UserHandler(new UserService(users, auths, games));
    }

    @Test
    void registerCreatesUserAndAuthToken() throws Exception {
        RegisterResult result = service.register(
                new RegisterRequest("alice", "password", "alice@example.com"));

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
                () -> service.register(new RegisterRequest(null, "pw", "a@b.com")));
        assertThrows(AuthenticationException.class,
                () -> service.register(new RegisterRequest("alice", null, "a@b.com")));
        assertThrows(AuthenticationException.class,
                () -> service.register(new RegisterRequest("alice", "pw", null)));
        assertThrows(AuthenticationException.class,
                () -> service.register(new RegisterRequest(" ", "pw", "a@b.com")));
        assertThrows(AuthenticationException.class,
                () -> service.register(new RegisterRequest("alice", " ", "a@b.com")));
        assertThrows(AuthenticationException.class,
                () -> service.register(new RegisterRequest("alice", "pw", " ")));
    }

    @Test
    void registerRejectsDuplicateUsername() throws Exception {
        service.register(new RegisterRequest("alice", "password", "alice@example.com"));

        AuthenticationException error = assertThrows(AuthenticationException.class,
                () -> service.register(new RegisterRequest("alice", "different", "other@example.com")));

        assertEquals("Username already exists", error.getMessage());
    }

    @Test
    void loginReturnsNewValidToken() throws Exception {
        service.register(new RegisterRequest("alice", "password", "alice@example.com"));

        LoginResult result = service.login(new LoginRequest("alice", "password"));

        assertEquals("alice", result.username());
        assertNotNull(result.authToken());
        assertNotNull(auths.getAuth(result.authToken()));
        assertEquals("alice", auths.getAuth(result.authToken()).username());
    }

    @Test
    void loginRejectsNullOrIncompleteRequest() {
        assertThrows(AuthenticationException.class, () -> service.login(null));
        assertThrows(AuthenticationException.class, () -> service.login(new LoginRequest(null, "pw")));
        assertThrows(AuthenticationException.class, () -> service.login(new LoginRequest("alice", null)));
        assertThrows(AuthenticationException.class, () -> service.login(new LoginRequest("", "pw")));
        assertThrows(AuthenticationException.class, () -> service.login(new LoginRequest("alice", " ")));
    }

    @Test
    void loginRejectsUnknownUserAndWrongPassword() throws Exception {
        AuthenticationException unknownUser = assertThrows(AuthenticationException.class,
                () -> service.login(new LoginRequest("missing", "password")));
        assertEquals("Unauthorized", unknownUser.getMessage());

        service.register(new RegisterRequest("alice", "password", "alice@example.com"));
        AuthenticationException wrongPassword = assertThrows(AuthenticationException.class,
                () -> service.login(new LoginRequest("alice", "wrong")));
        assertEquals("Unauthorized", wrongPassword.getMessage());
    }

    @Test
    void loginCreatesDistinctTokensForSeparateLogins() throws Exception {
        service.register(new RegisterRequest("alice", "password", "alice@example.com"));

        LoginResult first = service.login(new LoginRequest("alice", "password"));
        LoginResult second = service.login(new LoginRequest("alice", "password"));

        assertNotEquals(first.authToken(), second.authToken());
        assertNotNull(auths.getAuth(first.authToken()));
        assertNotNull(auths.getAuth(second.authToken()));
    }

    @Test
    void logoutRemovesExistingToken() throws Exception {
        RegisterResult registered = service.register(
                new RegisterRequest("alice", "password", "alice@example.com"));
        assertNotNull(auths.getAuth(registered.authToken()));

        service.logout(new LogoutRequest(registered.authToken()));

        assertNull(auths.getAuth(registered.authToken()));
    }

    @Test
    void logoutRejectsNullRequestMissingTokenAndUnknownToken() throws Exception {
        assertThrows(BadRequestResponse.class, () -> service.logout(null));
        AuthenticationException missing = assertThrows(AuthenticationException.class,
                () -> service.logout(new LogoutRequest(null)));
        assertEquals("Unauthorized", missing.getMessage());

        AuthenticationException blank = assertThrows(AuthenticationException.class,
                () -> service.logout(new LogoutRequest(" ")));
        assertEquals("Unauthorized", blank.getMessage());

        AuthenticationException unknown = assertThrows(AuthenticationException.class,
                () -> service.logout(new LogoutRequest("not-a-token")));
        assertEquals("Unauthorized", unknown.getMessage());
    }

    @Test
    void listGamesReturnsAllCreatedGamesForAuthenticatedUser() throws Exception {
        RegisterResult registered = service.register(
                new RegisterRequest("alice", "password", "alice@example.com"));
        int firstId = service.createGame(new CreateGameRequest(registered.authToken(), "First")).gameID();
        int secondId = service.createGame(new CreateGameRequest(registered.authToken(), "Second")).gameID();

        ListGameResult result = service.listGame(new ListGameRequest(registered.authToken()));

        assertNotNull(result.games());
        assertEquals(2, result.games().size());
        assertTrue(result.games().stream().anyMatch(g -> g.gameID() == firstId && g.gameName().equals("First")));
        assertTrue(result.games().stream().anyMatch(g -> g.gameID() == secondId && g.gameName().equals("Second")));
    }

    @Test
    void listGamesReturnsEmptyCollectionWhenNoGamesExist() throws Exception {
        RegisterResult registered = service.register(
                new RegisterRequest("alice", "password", "alice@example.com"));

        ListGameResult result = service.listGame(new ListGameRequest(registered.authToken()));

        assertNotNull(result.games());
        assertTrue(result.games().isEmpty());
    }

    @Test
    void listGamesRejectsNullRequestAndInvalidToken() throws Exception {
        assertThrows(BadRequestResponse.class, () -> service.listGame(null));
        assertThrows(AuthenticationException.class, () -> service.listGame(new ListGameRequest(null)));
        assertThrows(AuthenticationException.class, () -> service.listGame(new ListGameRequest("")));
        assertThrows(AuthenticationException.class, () -> service.listGame(new ListGameRequest("invalid")));
    }

    @Test
    void createGameStoresGameWithExpectedDetails() throws Exception {
        RegisterResult registered = service.register(
                new RegisterRequest("alice", "password", "alice@example.com"));

        CreateGameResult result = service.createGame(
                new CreateGameRequest(registered.authToken(), "My game"));

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
                () -> service.createGame(new CreateGameRequest(null, "game")));
        assertThrows(AuthenticationException.class,
                () -> service.createGame(new CreateGameRequest("invalid", "game")));

        RegisterResult registered = service.register(
                new RegisterRequest("alice", "password", "alice@example.com"));
        assertThrows(BadRequestResponse.class,
                () -> service.createGame(new CreateGameRequest(registered.authToken(), null)));
        assertThrows(BadRequestResponse.class,
                () -> service.createGame(new CreateGameRequest(registered.authToken(), " ")));
    }

    @Test
    void joinGameClaimsWhiteAndBlackSlots() throws Exception {
        RegisterResult alice = service.register(
                new RegisterRequest("alice", "password", "alice@example.com"));
        RegisterResult bob = service.register(
                new RegisterRequest("bob", "password", "bob@example.com"));
        int gameId = service.createGame(new CreateGameRequest(alice.authToken(), "Game")).gameID();

        service.joinGame(new JoinGameRequest(alice.authToken(), "WHITE", gameId));
        service.joinGame(new JoinGameRequest(bob.authToken(), "BLACK", gameId));

        GameData stored = games.getGame(gameId);
        assertEquals("alice", stored.whiteUsername());
        assertEquals("bob", stored.blackUsername());
        assertEquals("Game", stored.gameName());
    }

    @Test
    void joinGameRejectsNullRequestInvalidTokenBadColorAndNonpositiveId() throws Exception {
        assertThrows(BadRequestResponse.class, () -> service.joinGame(null));
        assertThrows(AuthenticationException.class,
                () -> service.joinGame(new JoinGameRequest(null, "WHITE", 1)));
        assertThrows(AuthenticationException.class,
                () -> service.joinGame(new JoinGameRequest("invalid", "WHITE", 1)));
        assertThrows(BadRequestResponse.class,
                () -> service.joinGame(new JoinGameRequest("token", null, 1)));
        assertThrows(BadRequestResponse.class,
                () -> service.joinGame(new JoinGameRequest("token", "", 1)));
        assertThrows(BadRequestResponse.class,
                () -> service.joinGame(new JoinGameRequest("token", "GREEN", 1)));
        assertThrows(BadRequestResponse.class,
                () -> service.joinGame(new JoinGameRequest("token", "WHITE", 0)));
        assertThrows(BadRequestResponse.class,
                () -> service.joinGame(new JoinGameRequest("token", "WHITE", -1)));
    }

    @Test
    void joinGameRejectsNonexistentGame() throws Exception {
        RegisterResult registered = service.register(
                new RegisterRequest("alice", "password", "alice@example.com"));

        assertThrows(DataAccessException.class,
                () -> service.joinGame(new JoinGameRequest(registered.authToken(), "WHITE", 999)));
    }

    @Test
    void joinGameRejectsAlreadyClaimedColor() throws Exception {
        RegisterResult alice = service.register(
                new RegisterRequest("alice", "password", "alice@example.com"));
        RegisterResult bob = service.register(
                new RegisterRequest("bob", "password", "bob@example.com"));
        int gameId = service.createGame(new CreateGameRequest(alice.authToken(), "Game")).gameID();

        service.joinGame(new JoinGameRequest(alice.authToken(), "WHITE", gameId));

        DataAccessException error = assertThrows(DataAccessException.class,
                () -> service.joinGame(new JoinGameRequest(bob.authToken(), "WHITE", gameId)));
        assertEquals("already taken", error.getMessage());

        GameData stored = games.getGame(gameId);
        assertEquals("alice", stored.whiteUsername());
        assertNull(stored.blackUsername());
    }

    @Test
    void clearRemovesUsersAuthTokensAndGames() throws Exception {
        RegisterResult registered = service.register(
                new RegisterRequest("alice", "password", "alice@example.com"));
        service.createGame(new CreateGameRequest(registered.authToken(), "Game"));

        service.clear();

        assertNull(users.getUser("alice"));
        assertNull(auths.getAuth(registered.authToken()));
        assertTrue(games.listGames().isEmpty());
        assertThrows(AuthenticationException.class,
                () -> service.login(new LoginRequest("alice", "password")));
    }

    @Test
    void clearCanBeCalledMoreThanOnce() throws Exception {
        service.clear();
        service.clear();

        assertTrue(games.listGames().isEmpty());
    }
}
