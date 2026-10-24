package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class memoryAuthDao implements authdao{
    private static HashMap<String, AuthData> data;
    public memoryAuthDao(){
        data = new HashMap<>();
    }

    @Override
    public static void createAuth(AuthData auth) throws DataAccessException {
        data.put(auth.authToken(), auth);
    }

    @Override
    public static AuthData getAuth(String authToken) throws DataAccessException {
        if (!data.containsKey(authToken)){
            throw new DataAccessException("No Auth Token");
        }

        return data.get(authToken);
    }

    @Override
    public static void deleteAuth(String authToken) throws DataAccessException {
        if (!data.containsKey(authToken)){
            throw new DataAccessException("No Auth Token");
        }
        data.remove(authToken);
    }

    @Override
    public void clear() throws DataAccessException {
        data.clear();
    }
}
