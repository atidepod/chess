package service;

import dataaccess.SQLAuthDao;
import dataaccess.SQLGameDao;
import dataaccess.SQLUserDao;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DataBaseTests {
    private SQLUserDao users;
    private SQLAuthDao auths;
    private SQLGameDao games;
    private UserService service;


    @BeforeEach
    void setUp() {
        users = new SQLUserDao();
        auths = new SQLAuthDao();
        games = new SQLGameDao();
        service = new UserService(users, auths, games);
    }


    @Test
    public void createUserPositive() throws Exception{
        UserData user = new UserData("bob", "password", "bob@test.com");
        users.createUser(user);
        UserData found = users.getUser("bob");
        assertNotNull(found);
        assertEquals("bob", found.username());
        assertEquals("password", found.password());
        assertEquals("bob@test.com", found.email());

    }

}
