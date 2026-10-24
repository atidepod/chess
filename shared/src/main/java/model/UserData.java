package model;
import java.util.Objects;


public class UserData {
    private final String username;
    private final String password;
    private final String email;

    UserData(String username, String password, String email){
        this.username =username;
        this.password = password;
        this.email = email;

    }
    public String getUsername(){
        return username;
    }
    public String getPassword(){
        return password;
    }
    public String getEmail(){
        return email;
    }
    public String toString(){
        return "UserData Class[ Username=" + username + "Password=" + password + "Email=" + email + "]";
    }
}
