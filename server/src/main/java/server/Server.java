package server;

import dataaccess.DataAccessException;
import dataaccess.authdao;
import dataaccess.userdao;
import io.javalin.*;
import model.UserData;
import service.UserHandler;
import service.UserService;

public class Server {

    private final Javalin javalin;
    private final userdao userDao;
    private final authdao authDao;


    public Server(userdao userDao, authdao authDao) {
        this.userDao = userDao;
        this.authDao = authDao;
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
