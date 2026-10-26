package service;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import model.GameData;

import javax.naming.AuthenticationException;
import java.util.Collection;
import java.util.Map;

public class UserHandler {

    public final UserService service;
    public UserHandler(UserService service){
        this.service = service;
    }
    public void register(Context ctx) throws Exception {
        Gson gson = new Gson();
        try {
            registerRequest request = gson.fromJson(ctx.body(), registerRequest.class);
            registerResult result = service.register(request);
            ctx.status(200).result(gson.toJson(result));
        }
        catch(com.google.gson.JsonParseException e){
            ctx.status(400).result(gson.toJson(Map.of("message", "Error: bad request")));
        }
        catch(AuthenticationException e){
            if (e.getMessage().equals("Bad request")){
                ctx.status(400).result(gson.toJson(Map.of("message", "Error: bad request")));
            }
            else if (e.getMessage().equals("Username already exists")){
                ctx.status(403).result(gson.toJson(Map.of("message", "Error: please don't")));
            }
            else {
                ctx.status(500).result(gson.toJson(Map.of("message", "Error: something broke")));
            }
        }
        catch (DataAccessException e){
            ctx.status(500).result(gson.toJson(Map.of("message", "Error: something broke")));
        }

    }
    public void login(Context ctx) throws Exception{

        Gson gson = new Gson();
        try {
            loginRequest request = gson.fromJson(ctx.body(), loginRequest.class);
            loginResult result = service.login(request);
            ctx.status(200).result(gson.toJson(result));
        }
        catch(com.google.gson.JsonParseException e){
            ctx.status(400).result(gson.toJson(Map.of("message", "Error: bad request")));
        }
        catch (AuthenticationException e){
            if (e.getMessage().equals("empty field")){
                ctx.status(400).result(gson.toJson(Map.of("message", "Error: bad request")));
            }
            else if ((e.getMessage().equals("Unauthorized"))){
                ctx.status(401).result(gson.toJson(Map.of("message", "Error: unauthorized")));
            }
            else {
                ctx.status(500).result(gson.toJson(Map.of("message", "Error: something broke")));
            }
        }
        catch (DataAccessException e){
            ctx.status(500).result(gson.toJson(Map.of("message", "Error: something broke")));
        }

    }
    public void logout(Context ctx) throws Exception{
        Gson gson = new Gson();
        try {

            String token = ctx.header("Authorization");
            logoutRequest request = new logoutRequest(token);
            service.logout(request);
            ctx.status(200).result("{}");

        }
        catch(com.google.gson.JsonParseException e){
            ctx.status(400).result(gson.toJson(Map.of("message", "Error: bad request")));
        }
        catch(AuthenticationException e){
            if ((e.getMessage().equals("Unauthorized"))){
                ctx.status(401).result(gson.toJson(Map.of("message", "Error: unauthorized")));
            }
            else {
                ctx.status(500).result(gson.toJson(Map.of("message", "Error: something broke")));
            }
        }
        catch (DataAccessException e){
            ctx.status(500).result(gson.toJson(Map.of("message", "Error: something broke")));
        }
    }

    public void listGame(Context ctx){
        Gson gson = new Gson();
        try{
            String token = ctx.header("Authorization");
            listGameRequest request = new listGameRequest(token);
            listGameResult result = service.listGame(request);
            ctx.status(200).result(gson.toJson(result));
        }
        catch()

    }

    public void createGame(Context ctx){

    }
    public void joinGame(Context ctx){

    }



}
