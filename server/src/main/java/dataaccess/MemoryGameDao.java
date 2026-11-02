package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import com.google.gson.Gson;
public class MemoryGameDao implements GameDao {
    private HashMap<Integer, GameData> games;
    private final Gson gson = new Gson();
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
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, game.whiteUsername());
            stmt.setString(2, game.blackUsername());
            stmt.setString(3, game.gameName());
            stmt.setString(4, gson.toJson(game.game()));
            stmt.executeUpdate();

        } catch (SQLException e){
            throw new DataAccessException("Unable to create game");
        }

    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {

        String sql = """
        SELECT *
        FROM games
        WHERE gameID = ?
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameID);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return new GameData(
                            rs.getInt("gameID"),
                            rs.getString("whiteUsername"),
                            rs.getString("blackUsername"),
                            rs.getString("gameName"),
                            gson.fromJson(
                                    rs.getString("game"),
                                    ChessGame.class)
                    );
                }
            }

            throw new DataAccessException("No Such Game");

        } catch (SQLException e) {
            throw new DataAccessException("Unable to get game", e);
        }
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        String sql = """
                
                UPDATE games
                SET whiteUsername = ?,
                    blackUsername = ?,
                    gameName = ?,
                    game = ?
               WHERE gameID = ?
               
                """;
        




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
