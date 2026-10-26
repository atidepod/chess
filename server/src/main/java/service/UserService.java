package service;

import chess.ChessGame;
import dataaccess.DataAccessException;

import dataaccess.authdao;
import dataaccess.gamedao;
import dataaccess.userdao;
import io.javalin.http.BadRequestResponse;
import model.AuthData;
import model.GameData;
import model.UserData;

import javax.naming.AuthenticationException;
import java.util.Collection;
import java.util.UUID;

public class UserService {
    private final userdao userDAO;
    private final authdao authDAO;
    private final gamedao gameDAO;

    public UserService(userdao userDAO, authdao authDAO, gamedao gameDAO){

        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }


    public registerResult register(registerRequest request) throws AuthenticationException, DataAccessException {
        if (request.username() == null || request.password() == null || request.email() == null || request.username().isBlank() || request.password().isBlank() || request.email().isBlank()) {

            throw new AuthenticationException("Bad request");
        }
        UserData existing = userDAO.getUser(request.username());
        if (existing != null){
            throw new AuthenticationException("Username already exists");
        }
        UserData user = new UserData(request.username(), request.password(), request.email());
        userDAO.createUser(user);
        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, request.username());
        authDAO.createAuth(auth);
        registerResult result = new registerResult(request.username(), token);
        return result;

    }
    public loginResult login(loginRequest request) throws AuthenticationException, DataAccessException {
        if (request == null || request.username() == null || request.password() == null || request.username().isBlank() || request.password().isBlank()){
            throw new AuthenticationException("empty field");
        }

        UserData user = userDAO.getUser(request.username());
        if (user == null){
            throw new AuthenticationException("Unauthorized");
        }
        if (!user.password().equals(request.password())){
            throw new AuthenticationException("Unauthorized");
        }
        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, request.username());
        authDAO.createAuth(auth);
        loginResult result = new loginResult(request.username(), token);
        return result;




    }
    public void logout(logoutRequest request) throws AuthenticationException, DataAccessException {
        if (request == null) {
            throw new BadRequestResponse("bad request");
        }
        if (request.authToken() == null || request.authToken().isBlank()) {

            throw new AuthenticationException("Unauthorized");
        }
        if (authDAO.getAuth(request.authToken()) == null){
            throw new AuthenticationException("Unauthorized");
        }
        authDAO.deleteAuth(request.authToken());

    }

    public listGameResult listGame(listGameRequest request) throws AuthenticationException, DataAccessException, BadRequestResponse{

        if (request == null){
            throw new BadRequestResponse("bad request");
        }
        if (request.authToken() == null || request.authToken().isBlank()){
            throw new AuthenticationException("Unauthorized");
        }
        AuthData auth = authDAO.getAuth(request.authToken());

        if (auth == null){
            throw new AuthenticationException(("Unauthorized"));
        }
        Collection<GameData> games = gameDAO.listGames();
        return new listGameResult(games);
    }

    public createGameResult createGame(createGameRequest request) throws AuthenticationException, DataAccessException, BadRequestResponse{
        if (request.authToken() == null || request.authToken().isBlank()){
            throw new AuthenticationException("Unauthorized");
        }
        AuthData auth = authDAO.getAuth(request.authToken());

        if (auth == null){
            throw new AuthenticationException(("Unauthorized"));
        }
        if (request.gameName() == null || request.gameName().isBlank()){
            throw new BadRequestResponse("bad request");
        }
        ChessGame game = new ChessGame();
        int newGameID = gameDAO.getNextGameID();
        GameData data = new GameData(newGameID, null, null, request.gameName(), game);

        gameDAO.createGame(data);
        return new createGameResult(newGameID);

    }

    public void joinGame(joinGameRequest request) throws AuthenticationException, DataAccessException, BadRequestResponse{
        if (request == null) {
            throw new BadRequestResponse("bad request");
        }
        if (request.authToken() == null || request.authToken().isBlank()){
            throw new AuthenticationException("Unauthorized");
        }

        if (request.playerColor() == null || (!request.playerColor().equals("WHITE") && !request.playerColor().equals("BLACK"))) {
            throw new BadRequestResponse("bad request");
        }

        if (request.gameID() <= 0) {
            throw new BadRequestResponse("bad request");
        }
        if(!request.playerColor().equals("WHITE") && !request.playerColor().equals("BLACK")){
            throw new BadRequestResponse("bad request");
        }
        AuthData auth = authDAO.getAuth(request.authToken());

        if (auth == null){
            throw new AuthenticationException(("Unauthorized"));
        }
        GameData gameToJoin = gameDAO.getGame(request.gameID());
        if (gameToJoin == null) {
            throw new BadRequestResponse("bad request");
        }

        ChessGame game = gameToJoin.game();
        String username = auth.username();


        if (request.playerColor().equals("WHITE") && gameToJoin.whiteUsername() == null){
            GameData data = new GameData(request.gameID(), username, gameToJoin.blackUsername(), gameToJoin.gameName(), game);
            gameDAO.updateGame(data);
        }
        else if (request.playerColor().equals("BLACK") && gameToJoin.blackUsername() == null){
            GameData data = new GameData(request.gameID(), gameToJoin.whiteUsername(), username, gameToJoin.gameName(), game);
            gameDAO.updateGame(data);
        }
        else{
            throw new DataAccessException("already taken");
        }

    }
    public void clear() throws DataAccessException {
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();

    }


}
