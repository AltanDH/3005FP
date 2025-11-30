import java.sql.*;

public class Trainer extends User {

    public Trainer(String email, String password, String role) {
        email_ = email;
        password_ = password;
        role_ = role;
    }
}
