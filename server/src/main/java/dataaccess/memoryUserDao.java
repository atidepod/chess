package dataaccess;

import model.UserData;

import java.util.HashMap;

public class memoryUserDao implements userdao{
    private HashMap<String, UserData> users;
    public memoryUserDao() {
        users = new HashMap<>();


        @Override
        public void insertUser (UserData user) throws DataAccessException {

        }

        @Override
        public UserData getUser (String username) throws DataAccessException {
            return null;
        }

        @Override
        public void clear () throws DataAccessException {

        }
    }
}
