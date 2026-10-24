package service;

import dataaccess.DataAccessException;

import dataaccess.authdao;
import dataaccess.userdao;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {
    private final userdao userDAO;
    private final authdao authDAO;

    public UserService(userdao userDAO, authdao authDAO){

        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public registerResult register(registerRequest request) throws Exception {
        if (request.username() == null || request.password() == null || request.email() == null) {

            throw new Exception("Bad request");
        }
        UserData existing = userDAO.getUser(request.username());
        if (existing != null){
            throw new DataAccessException("Username already exists");
        }
        UserData user = new UserData(request.username(), request.password(), request.email());
        userDAO.createUser(user);
        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, request.username());
        authDAO.createAuth(auth);
        registerResult result = new registerResult(request.username(), token);
        return result;

    }
    public loginResult login(loginRequest request) throws Exception {
        if (request.username() == null || request.password() == null){
            throw new Exception("empty field");
        }
        UserData user = userDAO.getUser(request.username());
        if (user == null){
            throw new DataAccessException("Unauthorized");
        }
        if (!user.password().equals(request.password())){
            throw new Exception("Unauthorized");
        }
        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, request.username());
        authDAO.createAuth(auth);
        loginResult result = new loginResult(request.username(), token);
        return result;




    }
    public void logout(logoutRequest request) throws DataAccessException {

        if (authDAO.getAuth(request.authToken()) == null){
            throw new DataAccessException("Unauthorized");
        }
        authDAO.deleteAuth(request.authToken());

    }

}
