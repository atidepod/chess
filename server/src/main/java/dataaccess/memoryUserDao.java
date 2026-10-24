package dataaccess;

import model.UserData;

import java.util.HashMap;

public class memoryUserDao implements userdao{
    private HashMap<String, UserData> users;
    public memoryUserDao() {
        users = new HashMap<>();
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        if (users.containsKey(user.username())){
           throw new DataAccessException("Username taken");
        }
        users.put(user.username(), user);

    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return users.get(username);
    }

    @Override
    public void clear () throws DataAccessException {
        users.clear();

    }
}

