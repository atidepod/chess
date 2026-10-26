package server;

import dataaccess.*;
import io.javalin.*;
import model.UserData;
import service.UserHandler;
import service.UserService;

public class Server {

    private final Javalin javalin;



    public Server() {
        userdao userDao = new memoryUserDao();
        authdao authDao = new memoryAuthDao();
        gamedao gameDAO = new memoryGameDao();
        javalin = Javalin.create(config -> config.staticFiles.add("web"));


        UserService userService = new UserService(userDao, authDao, gameDAO);
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
