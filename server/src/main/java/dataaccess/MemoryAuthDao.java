package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

public class MemoryAuthDao implements AuthDao {
    private HashMap<String, AuthData> data;
    public MemoryAuthDao(){
        data = new HashMap<>();
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        data.put(auth.authToken(), auth);
    }

    @Override
    public AuthData getAuth(String authToken) {
        if (!data.containsKey(authToken)){
            return null;
        }

        return data.get(authToken);
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
