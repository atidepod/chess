package service;

import chess.ChessGame;
import dataaccess.DataAccessException;

import dataaccess.AuthDao;
import dataaccess.GameDao;
import dataaccess.UserDao;
import io.javalin.http.BadRequestResponse;
import model.AuthData;
import model.GameData;
import model.UserData;

import javax.naming.AuthenticationException;
import java.util.Collection;
import java.util.UUID;
import org.mindrot.jbcrypt.BCrypt;
public class UserService {
    private final UserDao userDAO;
    private final AuthDao authDAO;
    private final GameDao gameDAO;

    public UserService(UserDao userDAO, AuthDao authDAO, GameDao gameDAO){

        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }


    public RegisterResult register(RegisterRequest request) throws AuthenticationException, DataAccessException {
        if (request.username() == null || request.password() == null || request.email() == null || request.username().isBlank() || request.password().isBlank() || request.email().isBlank()) {

            throw new AuthenticationException("Bad request");
        }
        UserData existing = userDAO.getUser(request.username());
        if (existing != null){
            throw new AuthenticationException("Username already exists");
        }
        String hashedPassword = BCrypt.hashpw(request.password(), BCrypt.gensalt());
        UserData user = new UserData(request.username(), hashedPassword, request.email());
        userDAO.createUser(user);
        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, request.username());
        authDAO.createAuth(auth);
        RegisterResult result = new RegisterResult(request.username(), token);
        return result;

    }
    public LoginResult login(LoginRequest request) throws AuthenticationException, DataAccessException {
        if (request == null || request.username() == null || request.password() == null || request.username().isBlank() || request.password().isBlank()){
            throw new AuthenticationException("empty field");
        }

        UserData user = userDAO.getUser(request.username());
        if (user == null){
            throw new AuthenticationException("Unauthorized");
        }
        if (!BCrypt.checkpw(request.password(), user.password())) {
            throw new AuthenticationException("Unauthorized");
        }
        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, request.username());
        authDAO.createAuth(auth);
        LoginResult result = new LoginResult(request.username(), token);
        return result;




    }
    public void logout(LogoutRequest request) throws AuthenticationException, DataAccessException {
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

    public ListGameResult listGame(ListGameRequest request) throws AuthenticationException, DataAccessException, BadRequestResponse{

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
        return new ListGameResult(games);
    }

    public CreateGameResult createGame(CreateGameRequest request) throws AuthenticationException, DataAccessException, BadRequestResponse{
        if (request == null) {
            throw new BadRequestResponse("bad request");
        }
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
        GameData gamer = new GameData(0, null, null, request.gameName(), game);

        int gameID = gameDAO.createGame(gamer);
        return new CreateGameResult(gameID);

    }

    public void joinGame(JoinGameRequest request) throws AuthenticationException, DataAccessException, BadRequestResponse{
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
        authDAO.clear();
        gameDAO.clear();
        userDAO.clear();

    }


}
