package dataaccess;

import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDao implements GameDao {
    private HashMap<Integer, GameData> games;
    private int nextGameID;
    public MemoryGameDao(){
        games = new HashMap<>();
        nextGameID = 1;
    }
    @Override
    public int getNextGameID(){
        return nextGameID++;
    }


    @Override
    public void createGame(GameData game) throws DataAccessException {
        String sql = """
                INSERT INTO games
                (whiteUsername, blackUsername, gameName, game)
                VALUES (?, ?, ?, ?)
                """;
        try (Connection conn = DatabaseManager.getConnection())

    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        if (!games.containsKey(gameID)){
            throw new DataAccessException("No Game ID");
        }
        return games.get(gameID);
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        if (!games.containsKey(game.gameID())){
            throw new DataAccessException("No Such Game");
        }
        games.put(game.gameID(), game);



    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        if (games.values() == null){
            throw new DataAccessException("No Such Game");
        }
        return games.values();
    }

    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM games";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Unable to clear games", e);
        }
    }
}
