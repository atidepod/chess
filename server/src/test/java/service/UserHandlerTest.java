package service;

import com.google.gson.Gson;
import dataaccess.MemoryAuthDao;
import dataaccess.MemoryGameDao;
import dataaccess.MemoryUserDao;
import io.javalin.http.Context;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Direct unit tests for UserHandler. These tests call handler methods with a lightweight
 * Context proxy; they do not start Javalin or make HTTP/network requests.
 */
class UserHandlerTest {
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
        handler = new UserHandler(new UserService(users, auths, games));
    }

    @Test
    void registerReturns200AndTokenForValidRequest() throws Exception {
        TestContext tc = new TestContext("{\"username\":\"alice\",\"password\":\"pw\",\"email\":\"a@b.com\"}", null);
        handler.register(tc.context());

        assertEquals(200, tc.status);
        RegisterResult result = gson.fromJson(tc.result, RegisterResult.class);
        assertEquals("alice", result.username());
        assertNotNull(result.authToken());
        assertNotNull(auths.getAuth(result.authToken()));
    }

    @Test
    void registerMapsDuplicateUsernameTo403() throws Exception {
        users.createUser(new model.UserData("alice", "pw", "a@b.com"));
        TestContext tc = new TestContext("{\"username\":\"alice\",\"password\":\"pw\",\"email\":\"a@b.com\"}", null);
        handler.register(tc.context());

        assertEquals(403, tc.status);
        assertTrue(tc.result.contains("already taken"));
    }

    @Test
    void loginReturns200ForValidCredentials() throws Exception {
        handler.register(new TestContext("{\"username\":\"alice\",\"password\":\"pw\",\"email\":\"a@b.com\"}", null).context());
        TestContext tc = new TestContext("{\"username\":\"alice\",\"password\":\"pw\"}", null);
        handler.login(tc.context());

        assertEquals(200, tc.status);
        LoginResult result = gson.fromJson(tc.result, LoginResult.class);
        assertEquals("alice", result.username());
        assertNotNull(auths.getAuth(result.authToken()));
    }

    @Test
    void loginMapsWrongPasswordTo401AndMissingFieldsTo400() throws Exception {
        handler.register(new TestContext("{\"username\":\"alice\",\"password\":\"pw\",\"email\":\"a@b.com\"}", null).context());
        TestContext badPassword = new TestContext("{\"username\":\"alice\",\"password\":\"wrong\"}", null);
        handler.login(badPassword.context());
        assertEquals(401, badPassword.status);

        TestContext missing = new TestContext("{\"username\":\"alice\"}", null);
        handler.login(missing.context());
        assertEquals(400, missing.status);
    }

    @Test
    void logoutReturns200AndRemovesToken() throws Exception {
        TestContext registered = new TestContext("{\"username\":\"alice\",\"password\":\"pw\",\"email\":\"a@b.com\"}", null);
        handler.register(registered.context());
        String token = gson.fromJson(registered.result, RegisterResult.class).authToken();

        TestContext tc = new TestContext(null, token);
        handler.logout(tc.context());
        assertEquals(200, tc.status);
        assertNull(auths.getAuth(token));
    }

    @Test
    void logoutMapsMissingTokenTo401() throws Exception {
        TestContext tc = new TestContext(null, null);
        handler.logout(tc.context());
        assertEquals(401, tc.status);
    }

    @Test
    void listGameReturns200AndGameListForValidToken() throws Exception {
        TestContext registered = new TestContext("{\"username\":\"alice\",\"password\":\"pw\",\"email\":\"a@b.com\"}", null);
        handler.register(registered.context());
        String token = gson.fromJson(registered.result, RegisterResult.class).authToken();
        TestContext created = new TestContext("{\"gameName\":\"Chess\"}", token);
        handler.createGame(created.context());
        assertEquals(200, created.status);

        TestContext tc = new TestContext(null, token);
        handler.listGame(tc.context());
        assertEquals(200, tc.status);
        ListGameResult result = gson.fromJson(tc.result, ListGameResult.class);
        assertEquals(1, result.games().size());
    }

    @Test
    void listGameMapsInvalidTokenTo401() {
        TestContext tc = new TestContext(null, "invalid-token");
        handler.listGame(tc.context());
        assertEquals(401, tc.status);
    }

    @Test
    void createGameMapsMissingNameTo400AndInvalidTokenTo401() throws Exception {
        TestContext registered = new TestContext("{\"username\":\"alice\",\"password\":\"pw\",\"email\":\"a@b.com\"}", null);
        handler.register(registered.context());
        String token = gson.fromJson(registered.result, RegisterResult.class).authToken();

        TestContext missingName = new TestContext("{}", token);
        handler.createGame(missingName.context());
        assertEquals(400, missingName.status);

        TestContext unauthorized = new TestContext("{\"gameName\":\"Chess\"}", "invalid-token");
        handler.createGame(unauthorized.context());
        assertEquals(401, unauthorized.status);
    }

    @Test
    void joinGameReturns200WhenJoiningAvailableSlot() throws Exception {
        TestContext registered = new TestContext("{\"username\":\"alice\",\"password\":\"pw\",\"email\":\"a@b.com\"}", null);
        handler.register(registered.context());
        String token = gson.fromJson(registered.result, RegisterResult.class).authToken();
        TestContext created = new TestContext("{\"gameName\":\"Chess\"}", token);
        handler.createGame(created.context());
        int gameId = gson.fromJson(created.result, CreateGameResult.class).gameID();

        TestContext tc = new TestContext("{\"playerColor\":\"WHITE\",\"gameID\":" + gameId + "}", token);
        handler.joinGame(tc.context());
        assertEquals(200, tc.status);
        assertEquals("alice", games.getGame(gameId).whiteUsername());
    }

    @Test
    void joinGameMapsInvalidColorTo400AndOccupiedColorTo403() throws Exception {
        TestContext alice = new TestContext("{\"username\":\"alice\",\"password\":\"pw\",\"email\":\"a@b.com\"}", null);
        handler.register(alice.context());
        String aliceToken = gson.fromJson(alice.result, RegisterResult.class).authToken();
        TestContext bob = new TestContext("{\"username\":\"bob\",\"password\":\"pw\",\"email\":\"b@b.com\"}", null);
        handler.register(bob.context());
        String bobToken = gson.fromJson(bob.result, RegisterResult.class).authToken();
        TestContext created = new TestContext("{\"gameName\":\"Chess\"}", aliceToken);
        handler.createGame(created.context());
        int gameId = gson.fromJson(created.result, CreateGameResult.class).gameID();

        TestContext invalidColor = new TestContext("{\"playerColor\":\"GREEN\",\"gameID\":" + gameId + "}", aliceToken);
        handler.joinGame(invalidColor.context());
        assertEquals(400, invalidColor.status);

        TestContext firstJoin = new TestContext("{\"playerColor\":\"WHITE\",\"gameID\":" + gameId + "}", aliceToken);
        handler.joinGame(firstJoin.context());
        assertEquals(200, firstJoin.status);
        TestContext occupied = new TestContext("{\"playerColor\":\"WHITE\",\"gameID\":" + gameId + "}", bobToken);
        handler.joinGame(occupied.context());
        assertEquals(403, occupied.status);
    }

    @Test
    void joinGameMapsInvalidTokenTo401() {
        TestContext tc = new TestContext("{\"playerColor\":\"WHITE\",\"gameID\":1}", "invalid-token");
        handler.joinGame(tc.context());
        assertEquals(401, tc.status);
    }

    @Test
    void clearReturns200AndClearsStoredData() throws Exception {
        handler.register(new TestContext("{\"username\":\"alice\",\"password\":\"pw\",\"email\":\"a@b.com\"}", null).context());
        TestContext tc = new TestContext(null, null);
        handler.clear(tc.context());

        assertEquals(200, tc.status);
        assertTrue(users.getUser("alice") == null);
        assertTrue(games.listGames().isEmpty());
    }

    /** Captures only the Context methods used by UserHandler. */
    private static final class TestContext implements InvocationHandler {
        private final String body;
        private final String authorization;
        private int status = -1;
        private String result;
        private Context proxy;

        private TestContext(String body, String authorization) {
            this.body = body;
            this.authorization = authorization;
        }

        private Context context() {
            proxy = (Context) Proxy.newProxyInstance(
                    Context.class.getClassLoader(),
                    new Class<?>[]{Context.class},
                    this);
            return proxy;
        }

        @Override
        public Object invoke(Object proxyObject, Method method, Object[] args) {
            return switch (method.getName()) {
                case "body" -> body;
                case "header" -> "Authorization".equals(args[0]) ? authorization : null;
                case "status" -> {
                    status = (Integer) args[0];
                    yield proxy;
                }
                case "result" -> {
                    result = (String) args[0];
                    yield proxy;
                }
                case "toString" -> "TestContext";
                case "hashCode" -> System.identityHashCode(proxyObject);
                case "equals" -> proxyObject == args[0];
                default -> {
                    Class<?> returnType = method.getReturnType();
                    if (returnType == boolean.class) yield false;
                    if (returnType == int.class) yield 0;
                    if (returnType == long.class) yield 0L;
                    if (returnType == double.class) yield 0.0;
                    if (returnType == float.class) yield 0.0f;
                    if (returnType == short.class) yield (short) 0;
                    if (returnType == byte.class) yield (byte) 0;
                    if (returnType == char.class) yield '\0';
                    yield null;
                }
            };
        }
    }
}
