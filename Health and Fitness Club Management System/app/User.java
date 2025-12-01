import java.sql.*;
import java.time.LocalDate;

public class User {

    protected String email_;
    protected String password_;
    protected DatabaseHandler dbHandler_;

    // Getter for user role
    public User(DatabaseHandler dbHandler) {
        dbHandler_ = dbHandler;
    }

    public boolean findUser(Connection connection, String email, String password, String table) throws SQLException {
        String[] emailVal = {email};
        String result = dbHandler_.getAll(connection, table, emailVal);
        String [] records = result.split("\\n");

        if (records.length <= 1) {
            return false;
        }

        if (records[1].split(" \\| ")[3].equals(password)) {
            return true;
        }
        return false;
    }

    // Method to register a new member
    public boolean registerMember(Connection connection,
                                  String email,
                                  String firstName,
                                  String lastName,
                                  String password,
                                  LocalDate birthDate,
                                  String gender,
                                  String phoneNumber) throws SQLException {

        String sql = """
        INSERT INTO Members (email, first_name, last_name, password, birth_date, gender, phone_number)
        VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, firstName);
            stmt.setString(3, lastName);
            stmt.setString(4, password);
            stmt.setDate(5, Date.valueOf(birthDate));
            stmt.setString(6, gender);
            stmt.setString(7, phoneNumber);

            stmt.executeUpdate();
            System.out.println("Registration Successful!");
            return true;

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Registration failed: Email already exists.");
            return false;
        }
    }
}
