package client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.Gson;
import service.*;

public class ServerFacade {
    private final String serverUrl;
    private final HttpClient client;
    private final Gson gson;

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
        this.client = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    public LoginResult login(String username, String password) throws Exception{
        LoginRequest user = new LoginRequest(username, password);
        String json = gson.toJson(user);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/session"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), LoginResult.class);
        } else {
            throw new Exception("Login failed");
        }

    }

    public void logout(String authToken) throws Exception {
        LogoutRequest user = new LogoutRequest(authToken);
        String json = gson.toJson(user);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/session"))
                .header("Authorization", authToken)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200){
            throw new Exception("Logout failed");
        }

    }

    public ListGameResult listGame(String authToken) throws Exception {
        ListGameRequest user = new ListGameRequest(authToken);
        String json = gson.toJson(user);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/game"))
                .header("Authorization", authToken)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200){
            return gson.fromJson(response.body(), ListGameResult.class);

        } else {
            throw new Exception("Unable to list games");
        }
    }

    public int createGame(String authToken, String gameName) throws Exception {
        CreateGameRequest user = new CreateGameRequest(authToken, gameName);
        String json = gson.toJson(user);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/game"))
                .header("Content-Type", "application/json")
                .header("Authorization", authToken)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200){
            CreateGameResult result = gson.fromJson(response.body(), CreateGameResult.class);
            return result.gameID();

        } else {
            throw new Exception("Unable to create game");
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



    public RegisterResult register(String username, String password, String email) throws Exception {
        RegisterRequest RegRequest = new RegisterRequest(username, password, email);
        String json = gson.toJson(RegRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/user"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200){
            return gson.fromJson(response.body(), RegisterResult.class);

        }
        else {
            throw new Exception("Could not register");
        }

    }
}

