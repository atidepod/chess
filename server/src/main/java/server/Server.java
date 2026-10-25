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
        javalin = Javalin.create(config -> config.staticFiles.add("web"));


        UserService userService = new UserService(userDao, authDao);
        UserHandler userHandler = new UserHandler(userService);


        // Register your endpoints and exception handlers here.
        javalin.post("/user", userHandler::register);
        javalin.post("/session", userHandler::login);
        javalin.delete("/session", userHandler::logout);




    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
