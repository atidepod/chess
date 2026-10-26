package service;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.BadRequestResponse;
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
                ctx.status(403).result(gson.toJson(Map.of("message", "Error: already taken")));
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

        catch(AuthenticationException e){
            if ((e.getMessage().equals("Unauthorized"))){
                ctx.status(401).result(gson.toJson(Map.of("message", "Error: unauthorized")));
            }
            else {
                ctx.status(500).result(gson.toJson(Map.of("message", "Error: something broke")));
            }
        }
        catch(DataAccessException e){
            ctx.status(500).result(gson.toJson(Map.of("message", "Error: something broke")));

        }
        catch(BadRequestResponse e){
            if (e.getMessage().equals("bad request")){
                ctx.status(400).result(gson.toJson(Map.of("message", "Error: bad request")));
            }
        }

    }

    public void createGame(Context ctx){
        Gson gson = new Gson();
        try {
            String token = ctx.header("Authorization");
            createGameRequest body = gson.fromJson(ctx.body(), createGameRequest.class);
            if (body == null) {
                throw new BadRequestResponse("bad request");
            }

            createGameRequest request = new createGameRequest(token, body.gameName());
            createGameResult result = service.createGame(request);
            ctx.status(200).result(gson.toJson(result));


        }
        catch(DataAccessException e){
            if(e.getMessage().equals("Game Already Made")){
                ctx.status(400).result(gson.toJson(Map.of("message", "Error: game already exists")));
            }
            else {
                ctx.status(500).result(gson.toJson(Map.of("message", "Error: something broke")));
            }
        }
        catch(AuthenticationException e){
            if ((e.getMessage().equals("Unauthorized"))){
                ctx.status(401).result(gson.toJson(Map.of("message", "Error: unauthorized")));
            }
            else {
                ctx.status(500).result(gson.toJson(Map.of("message", "Error: something broke")));
            }
        }
        catch(BadRequestResponse e){
            if (e.getMessage().equals("bad request")){
                ctx.status(400).result(gson.toJson(Map.of("message", "Error: bad request")));
            }
            else {
                ctx.status(500).result(gson.toJson(Map.of("message", "Error: something broke")));
            }
        }

    }
    public void joinGame(Context ctx){
        Gson gson = new Gson();
        try{
            String token = ctx.header("Authorization");
            joinGameRequest body = gson.fromJson(ctx.body(), joinGameRequest.class);
            joinGameRequest request = new joinGameRequest(token, body.playerColor(), body.gameID());
            service.joinGame(request);
            ctx.status(200).result("{}");
        }
        catch(BadRequestResponse e){
            if (e.getMessage().equals("bad request")){
                ctx.status(400).result(gson.toJson(Map.of("message", "Error: bad request")));
            }

            else {
                ctx.status(500).result(gson.toJson(Map.of("message", "Error: something broke")));
            }
        }
        catch(AuthenticationException e){
            if ((e.getMessage().equals("Unauthorized"))){
                ctx.status(401).result(gson.toJson(Map.of("message", "Error: unauthorized")));
            }
            else {
                ctx.status(500).result(gson.toJson(Map.of("message", "Error: something broke")));
            }
        }
        catch(DataAccessException e){
            if (e.getMessage().equals("already taken")){
                ctx.status(403).result(gson.toJson(Map.of("message", "Error: game already taken")));
            }
            else {
                ctx.status(500).result(gson.toJson(Map.of("message", "Error: something broke")));
            }

        }


    }
    public void clear(Context ctx) throws DataAccessException {
        Gson gson = new Gson();
        try{
            service.clear();
            ctx.status(200).result("{}");
        }
        catch(DataAccessException e){
            ctx.status(500).result(gson.toJson(Map.of("message", "Error: something broke")));

        }

    }



}
