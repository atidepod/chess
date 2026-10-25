package service;

public record createGameRequest(
        String authToken,
        String gameName
) {
}
