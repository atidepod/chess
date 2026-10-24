package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class memoryAuthDao implements authdao{
    private HashMap<String, AuthData> data;
    public memoryAuthDao(){
        data = new HashMap<>();
    }

    @Override
    public void insertAuth(AuthData auth) throws DataAccessException {
        data.put(auth.authToken(), auth);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {

        return data.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if (data.containsKey(authToken)){
            data.remove(authToken);
        }
        else{
            throw new DataAccessException("No Auth Token");
        }
    }

    @Override
    public void clear() throws DataAccessException {
        data.clear();
    }
}
