import java.sql.*;

public class AdminStaff extends User {

    public AdminStaff(String email, String password, String role) {
        email_ = email;
        password_ = password;
        role_ = role;
    }
}
