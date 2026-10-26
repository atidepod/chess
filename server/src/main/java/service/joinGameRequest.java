package service;

public record joinGameRequest(
        String authToken,
        String playerColor,
        int gameID
) {
}
