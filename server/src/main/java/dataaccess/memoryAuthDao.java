package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class memoryAuthDao implements authdao{
    private HashMap<String, AuthData> data;
    public memoryAuthDao(){
        data = new HashMap<>();
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        data.put(auth.authToken(), auth);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        if (!data.containsKey(authToken)){
            throw new DataAccessException("No Auth Token");
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
        if (data == null){
            throw new DataAccessException("no data to clear");
        }
        data.clear();
    }
}
