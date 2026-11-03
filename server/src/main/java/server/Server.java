package server;

import dataaccess.*;
import io.javalin.*;
import service.UserHandler;
import service.UserService;

public class Server {

    private final Javalin javalin;



    public Server() {
        try {
            DatabaseManager.createDatabase();
            DataAccessInit.initDB();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        UserDao userDAO = new SQLUserDao();
        AuthDao authDAO = new SQLAuthDao();
        GameDao gameDAO = new SQLGameDao();
        javalin = Javalin.create(config -> config.staticFiles.add("web"));


        UserService userService = new UserService(userDAO, authDAO, gameDAO);
        UserHandler userHandler = new UserHandler(userService);


        // Register your endpoints and exception handlers here.
        javalin.post("/user", userHandler::register);
        javalin.post("/session", userHandler::login);
        javalin.delete("/session", userHandler::logout);
        javalin.delete("/db", userHandler::clear);
        javalin.get("/game", userHandler::listGame);
        javalin.post("/game", userHandler::createGame);
        javalin.put("/game", userHandler::joinGame);






    }

    public int run(int desiredPort) {

        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
