package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDao implements UserDao {
    private HashMap<String, UserData> users;
    public MemoryUserDao() {
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
        if (users == null){
            throw new DataAccessException("no users");
        }
        users.clear();

    }
}

