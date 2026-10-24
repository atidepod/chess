package service;

import dataaccess.DataAccessException;
import dataaccess.memoryAuthDao;
import dataaccess.memoryUserDao;
import model.AuthData;
import model.UserData;

import java.util.UUID;

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
        if (request.username() == null || request.password() == null || request.email() == null) {

            throw new Exception("Bad request");
        }
        UserData existing = memoryUserDao.getUser(request.username());
        if (existing != null){
            throw new DataAccessException("Username already exists");
        }
        UserData user = new UserData(request.username(), request.password(), request.email());
        memoryUserDao.createUser(user);
        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, request.username());
        memoryAuthDao.createAuth(auth);
        registerResult result = new registerResult(request.username(), token);
        return result;

    }
    public loginResult login(loginRequest request) throws Exception {
        if (request.username() == null || request.password() == null){
            throw new Exception("empty field");
        }
        UserData user = memoryUserDao.getUser(request.username());
        if (user == null){
            throw new DataAccessException("User does not exist");
        }
        if ()



    }
    void logout(){

    }

}
