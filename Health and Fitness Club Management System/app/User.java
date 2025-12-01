import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class User {

    protected String email_;
    protected String password_;

    // Getters
    public String getEmail() {
        return email_;
    }

    // Setters
    public void setEmail(String email) {
        email_ = email;
    }

    // Method to authenticate user login
    public boolean findUser(Connection connection, String email, String password, String table) throws SQLException {
        String[] emailVal = {email};
        String result = DatabaseHandler.getAll(connection, table, emailVal);
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
    public boolean registerMember(Connection connection, Scanner scanner) throws SQLException {
        // Prompt user for new account details
        System.out.println("Please enter your credentials: ");
        System.out.print("Email (e.g. fname.lname@email.com): ");
        String email = scanner.nextLine();
        System.out.print("First Name: ");
        String firstName = scanner.nextLine();
        System.out.print("Last Name: ");
        String lastName = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.print("Birth Date (e.g. YYYY-MM-DD): ");
        String date = scanner.nextLine();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate birthDate = LocalDate.parse(date, format);
        System.out.print("Gender (e.g. M): ");
        String gender = scanner.nextLine();
        System.out.print("Phone Number (e.g. XXX-XXX-XXXX): ");
        String phoneNumber = scanner.nextLine();

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
