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
    public AuthData getAuth(String authToken) throws DataAccessException {
        String sql = """
            SELECT authToken, username
            FROM auth
            WHERE authToken = ?
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, authToken);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return new AuthData(
                            rs.getString("authToken"),
                            rs.getString("username")
                    );
                }
            }
            return null;

        } catch (SQLException e) {
            throw new DataAccessException("Unable to get auth", e);
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

        String sql = """
            DELETE FROM auth
            WHERE authToken = ?
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, authToken);

            stmt.executeUpdate();
            int rows = stmt.executeUpdate();

            if (rows == 0) {
                throw new DataAccessException("Auth token not found");
            }

        } catch (SQLException e) {
            throw new DataAccessException("Unable to delete auth", e);
        }
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
