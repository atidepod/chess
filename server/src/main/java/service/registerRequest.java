package service;

public record registerRequest(
        String username,
        String password,
        String email
) {
}
