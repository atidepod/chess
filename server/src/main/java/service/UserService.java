package service;

import dataaccess.DataAccessException;
import dataaccess.memoryUserDao;
import model.UserData;

public class UserService {
    private final String username;
    private final String password;
    private final String email;
    public UserService(){
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public registerResult register(registerRequest request) throws Exception {
        if (username == null || password == null || email == null) {

            throw new Exception("Bad request");
        }
        UserData existing = memoryUserDao.getUser(request.username());
        if (existing != null){
            throw new DataAccessException("Username already exists");
        }
        UserData user = new UserData(request.username(), request.password(), request.email());
        memoryUserDao.createUser(user);

    }
    public loginResult login(loginRequest request){

    }
    void logout(){

    }

}
