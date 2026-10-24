package service;

import com.google.gson.Gson;
import io.javalin.http.Context;

public class UserHandler {

    public final UserService service;
    public UserHandler(UserService service){
        this.service = service;
    }
    public register(Context ctx) throws Exception {
        Gson gson = new Gson();
        try {


            registerRequest request = gson.fromJson(ctx.body(), registerRequest.class);
            registerResult result = service.register(request);
            ctx.result(gson.toJson(result));
        }
        catch {
            return "Error"
        }

    }
    public void login(Context ctx) throws Exception{

        Gson gson = new Gson();
        loginRequest request = gson.fromJson(ctx.body(), loginRequest.class);
        loginResult result = service.login(request);
        ctx.result(gson.toJson(result));

    }
    public void logout(Context ctx) throws Exception{

        String token = ctx.header("Authorization");
        logoutRequest request = new logoutRequest(token);
        service.logout(request);
    }


}
