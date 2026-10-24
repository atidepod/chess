package model;

public class AuthData {
    private final String authToken;
    private final String username;

    AuthData(String authToken, String username){
        this.authToken = authToken;
        this.username = username;
    }
    private String getAuthToken(){
        return authToken;
    }
    private String getUsername(){
        return username
    }
    public String toString(){
        return "Auth Data Class [ auth token =" + authToken + "username=" + username + "]";
    }
}
