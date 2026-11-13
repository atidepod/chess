package server;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.AuthData;
import model.UserData;

public class ServerFacade {
    private final String serverUrl;
    private final HttpClient client;
    private final ObjectMapper mapper;

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
        this.client = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
    }

    public AuthData login(String username, String password) throws Exception{
        UserData user = new UserData(username, password);
        String json = mapper.writeValueAsString(user);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/session"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return mapper.readValue(response.body(), AuthData.class);
        } else {
            throw new Exception("Login failed");
        }

    }
}
