package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class MemoryAuthDao implements AuthDao {
    private HashMap<String, AuthData> data;
    public MemoryAuthDao(){
        data = new HashMap<>();
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        String sql = """
               INSERT INTO auth
               (authtoken, username)
               VALUES (?, ?)
               """;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, auth.authToken());
            stmt.setString(2, auth.username());
            stmt.executeUpdate();

        } catch (SQLException e){
            throw new DataAccessException("Unable to create auth", e);
        }
    }

    @Override
    public AuthData getAuth(String authToken) {
        String sql = """
                SELECT authTokem, username
                FROM auth
                WHERE authToken = ?
                """;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken);
            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()){
                    return rs.getString("authToken");
                }
            }
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if (!data.containsKey(authToken)){
            throw new DataAccessException("No Auth Token");
        }
        data.remove(authToken);
    }

    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM auth";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Unable to clear auth", e);
        }
    }
}
