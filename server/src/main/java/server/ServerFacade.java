package server;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import service.JoinGameRequest;
import service.RegisterRequest;

public class ServerFacade {
    private final String serverUrl;
    private final HttpClient client;
    private final Gson gson;

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
        this.client = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    public AuthData login(String username, String password) throws Exception{
        UserData user = new UserData(username, password);
        String json = gson.toJson(user);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/session"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), AuthData.class);
        } else {
            throw new Exception("Login failed");
        }

    }

    public void joinGame(String authToken, int gameID, String color) throws Exception {
        JoinGameRequest joinRequest = new JoinGameRequest(authToken, color, gameID);
        String json = gson.toJson(joinRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/game"))
                .header("Content-Type", "application/json")
                .header("Authorization", authToken)
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200){
            throw new Exception("Unable to join game");
        }
    }

    public void quit(String quit) throws Exception{
        if (quit.equals("q")){
            beak all;
        }
    }

    public AuthData register(String email, String username, String password) throws Exception {
        RegisterRequest RegRequest = new RegisterRequest(email, username, password);
        String json = gson.toJson(RegRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/register"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200){
            return gson.fromJson(response.body(), AuthData.class);

        }
        else {
            throw new Exception("Could not register");
        }

    }
}
