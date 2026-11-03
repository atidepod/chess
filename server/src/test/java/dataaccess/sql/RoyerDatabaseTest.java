package dataaccess.sql;
import chess.ChessGame;


import chess.ChessMove;
import chess.ChessPosition;
import dataaccess.SQLAuthDao;
import dataaccess.SQLGameDao;
import dataaccess.SQLUserDao;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.UserService;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class RoyerDatabaseTest {
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
        UserData user = new UserData("bib", "password", "bib@test.com");
        users.createUser(user);
        UserData found = users.getUser("bib");
        assertNotNull(found);
        assertEquals("bib", found.username());
        assertEquals("password", found.password());
        assertEquals("bib@test.com", found.email());

    }

    @Test
    public void getUserNegative() throws Exception{
        UserData result = users.getUser("missing");
        assertNull(result);
    }

    @Test
    public void createAuthPositive() throws Exception{
        users.createUser(new UserData("bb", "password", "bb@test.com"));

        AuthData auth = new AuthData("token123", "bob");
        auths.createAuth(auth);
        AuthData result = auths.getAuth("token123");
        assertNotNull("bob", result.username());

    }

    @Test
    public void deleteAuthPositive() throws Exception{
        users.createUser(new UserData("bob", "password", "bob@test.com"));

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

    @Test
    public void getGameNegative() throws Exception{
        GameData game = games.getGame(999999);

        assertNull(game);


    }
    @Test
    public void updateGamePlayersPositive() throws Exception{
        users.createUser(
                new UserData(
                        "whitePlayer",
                        "pw",
                        "white@test.com"
                )
        );

        users.createUser(
                new UserData(
                        "blackPlayer",
                        "pw",
                        "black@test.com"
                )
        );
        int id = games.createGame(new GameData(0, null, null, "game", new ChessGame()));
        GameData updated = new GameData(id, "whitePlayer", "blackPlayer", "game2", new ChessGame());
        games.updateGame(updated);
        GameData result = games.getGame(id);
        assertEquals("whitePlayer", result.whiteUsername());
        assertEquals("blackPlayer", result.blackUsername());

    }
    @Test
    public void gameStatePersistencePositive() throws Exception{
        ChessGame game = new ChessGame();
        game.makeMove(new ChessMove(new ChessPosition(2,1), new ChessPosition(4,1), null));
        int id = games.createGame(new GameData(0, null, null, "game", game));
        GameData loaded = games.getGame(id);
        assertEquals(game, loaded.game());


    }
    @Test
    public void listGamesPositive() throws Exception{
        games.createGame(new GameData(0, null, null, "game1", new ChessGame()));
        games.createGame(new GameData(0, null, null, "game2", new ChessGame()));
        Collection<GameData> gameList = games.listGames();
        assertNotNull(gameList);


    }
    @Test
    public void clearGamesPositive() throws Exception{
        games.createGame(new GameData(0, null, null, "game", new ChessGame()));
        games.clear();
        assertTrue(games.listGames().isEmpty());
    }


}
