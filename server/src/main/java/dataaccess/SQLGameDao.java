package dataaccess;

import chess.ChessGame;
import model.GameData;
import chess.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import com.google.gson.Gson;
public class SQLGameDao implements GameDao {
    private HashMap<Integer, GameData> games;
    private final Gson gson = new Gson();
    private int nextGameID;
    public SQLGameDao(){
        games = new HashMap<>();
        nextGameID = 1;
    }


    @Override
    public int createGame(GameData game) throws DataAccessException {
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

            try (ResultSet rs = stmt.getGeneratedKeys()){
                if (rs.next()){
                    return rs.getInt(1);

                }
            }
            throw new DataAccessException("No game ID generated");

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

            return null;

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
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, game.whiteUsername());
            stmt.setString(2, game.blackUsername());
            stmt.setString(3, game.gameName());
            stmt.setString(4, gson.toJson(game.game()));
            stmt.setInt(5, game.gameID());
            int rows = stmt.executeUpdate();

            if (rows == 0){
                throw new DataAccessException("No such Game");
            }

        } catch (SQLException e){
            throw new DataAccessException("Unable to update game", e);
        }




    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        ArrayList<GameData> games = new ArrayList<>();
        String sql = "SELECT * FROM games";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                games.add(new GameData(rs.getInt("gameID"),
                        rs.getString("whiteUsername"),
                        rs.getString("blackUsername"),
                        rs.getString("gameName"),
                        gson.fromJson(rs.getString("game"), ChessGame.class)));

            }
            return games;
        }


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
