package service;

import chess.ChessGame;




import dataaccess.SQLAuthDao;
import dataaccess.SQLGameDao;
import dataaccess.SQLUserDao;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.*;

public class DataBaseTests {
    private SQLUserDao users;
    private SQLAuthDao auths;
    private SQLGameDao games;
    private UserService service;


    @BeforeEach
    void setUp() {
        users = new SQLUserDao();
        auths = new SQLAuthDao();
        games = new SQLGameDao();
        service = new UserService(users, auths, games);
    }


    @Test
    public void createUserPositive() throws Exception{
        UserData user = new UserData("bob", "password", "bob@test.com");
        users.createUser(user);
        UserData found = users.getUser("bob");
        assertNotNull(found);
        assertEquals("bob", found.username());
        assertEquals("password", found.password());
        assertEquals("bob@test.com", found.email());

    }

    @Test
    public void getUserNegative() throws Exception{
        UserData result = users.getUser("missing");
        assertNull(result);
    }

    @Test
    public void createAuthPositive() throws Exception{
        AuthData auth = new AuthData("token123", "bob");
        auths.createAuth(auth);
        AuthData result = auths.getAuth("token123");
        assertNotNull("bob", result.username());

    }

    @Test
    public void deleteAuthPositive() throws Exception{
        AuthData auth = new AuthData("token123", "bob");
        auths.createAuth(auth);
        auths.deleteAuth("token123");
        assertNull(auths.getAuth("token123"));


    }

    @Test
    public void getAuthNegative() throws Exception{
        AuthData auth = auths.getAuth("badtoken");
        assertNull(auth);


    }
    @Test
    public void createGamePositive() throws Exception{
        ChessGame game = new ChessGame();
        int gameID = games.createGame(new GameData(0, null, null, "test", game));
        assertTrue(gameID>0);

    }

    @Test
    public void getGamePositive() throws Exception{
        ChessGame game = new ChessGame();
        int id = games.createGame(new GameData(0,null, null, "test", game));
        GameData loaded = games.getGame(id);
        assertNotNull(loaded);
        assertEquals(id, loaded.gameID());

    }
    

}
