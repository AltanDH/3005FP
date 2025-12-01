import java.sql.*;
import java.util.*;

public class Member extends User {

    public Member(String email, String password, DatabaseHandler dbHand) {
        super(dbHand);
        email_ = email;
        password_ = password;
    }

    public String getEmail() {
        return email_;
    }

    public boolean updateMemberProfile(Connection connection, Scanner scanner) throws SQLException {
        // Prompt for new personal info
        System.out.println("Please enter your new personal details: ");
        System.out.print("New Email (e.g. fname.lname@email.com): ");
        String email = scanner.nextLine();
        System.out.print("New First Name: ");
        String firstName = scanner.nextLine();
        System.out.print("New Last Name: ");
        String lastName = scanner.nextLine();
        System.out.print("New Phone Number (e.g. XXX-XXX-XXXX): ");
        String phoneNumber = scanner.nextLine();

        String sql = """
        UPDATE Members
        SET first_name = ?, last_name = ?, phone_number = ?
        WHERE email = ?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, phoneNumber);
            stmt.setString(4, email);

            email_ = email;
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean setFitnessGoal(Connection connection, Scanner scanner) throws SQLException {
        // Prompt for fitness goal details
        System.out.println("Please enter your goal parameters: ");
        System.out.print("Goal Type (weight/bodyfat): ");
        String type = scanner.nextLine();
        System.out.print("Target Value (kg/percent): ");
        int value = scanner.nextInt();
        scanner.nextLine(); // Clear leftover symbols from buffer

        boolean result = dbHandler_.addTuple(connection, "fitnessgoals", new Object[]{email_, type, value});

        // Return early if no results found
        if (!result) {
            return result;
        }

        // Show updated fitness goals of current member
        System.out.println("\nHere's your updated fitness goals: ");
        String sql = "SELECT * FROM FitnessGoals WHERE email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email_);

            // Grab query result
            ResultSet rs = stmt.executeQuery();
            String out = "";

            // Get the metadata and column count of a passed in query
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // For each column, add the column name
            for (int i = 1; i <= columnCount; i++) {
                out += metaData.getColumnName(i);
                if (i < columnCount) { out += " | "; }
            }
            //System.out.println();
            out += "\n";

            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    out += rs.getString(i);
                    if (i < columnCount) { out += " | "; }
                }
                out += "\n";
            }
            rs.close();
            stmt.close();
            System.out.println(out);

            return true;
        }
    }

    public boolean logHealthMetric(Connection connection, Scanner scanner) throws SQLException {

        // Prompt for fitness goal details
        System.out.println("Please enter your health parameters: ");
        System.out.print("Weight (kg): ");
        int weight = scanner.nextInt();
        scanner.nextLine(); // Clear leftover symbols from buffer
        System.out.print("Height (cm): ");
        int height = scanner.nextInt();
        scanner.nextLine(); // Clear leftover symbols from buffer
        System.out.print("Heart Rate (bpm): ");
        int heartRate = scanner.nextInt();
        scanner.nextLine(); // Clear leftover symbols from buffer
        System.out.print("Body Fat (percentage): ");
        int bodyFatPct = scanner.nextInt();
        scanner.nextLine(); // Clear leftover symbols from buffer
        Timestamp now = new Timestamp(System.currentTimeMillis());

        // Add health metric
        boolean result = dbHandler_.addTuple(connection, "healthmetrics", new Object[]{email_, weight, height, heartRate, bodyFatPct, now});

        // Return early if no results found
        if (!result) {
            return result;
        }

        // Show updated health metrics of current member
        System.out.println("\nHere's your updated fitness goals: ");
        String sql = "SELECT * FROM HealthMetrics WHERE email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email_);

            // Grab query result
            ResultSet rs = stmt.executeQuery();
            String out = "";

            // Get the metadata and column count of a passed in query
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // For each column, add the column name
            for (int i = 1; i <= columnCount; i++) {
                out += metaData.getColumnName(i);
                if (i < columnCount) { out += " | "; }
            }
            //System.out.println();
            out += "\n";

            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    out += rs.getString(i);
                    if (i < columnCount) { out += " | "; }
                }
                out += "\n";
            }
            rs.close();
            stmt.close();
            System.out.println(out);

            return true;
        }
    }

    public boolean getHealthHistory(Connection connection) {
        // Show updated health metrics of current member
        System.out.println("\nDisplaying Health History: ");
        String sql = "SELECT * FROM HealthMetrics WHERE email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email_);

            // Grab query result
            ResultSet rs = stmt.executeQuery();
            String out = "";

            // Get the metadata and column count of a passed in query
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // For each column, add the column name
            for (int i = 1; i <= columnCount; i++) {
                out += metaData.getColumnName(i);
                if (i < columnCount) { out += " | "; }
            }
            //System.out.println();
            out += "\n";

            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    out += rs.getString(i);
                    if (i < columnCount) { out += " | "; }
                }
                out += "\n";
            }
            rs.close();
            stmt.close();
            System.out.println(out);

            return true;
        }
        catch (SQLException e) {
            System.out.println("Something went wrong when accessing table.");
            return false;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean registerForClass(Connection connection, String email, int classId) throws SQLException {

        String capacityCheck = """
        SELECT capacity,
               (SELECT COUNT(*) FROM Participates WHERE class_id = ?) AS enrolled
        FROM GroupFitnessClasses
        WHERE class_id = ?
        """;

        String insert = """
        INSERT INTO Participates (email, class_id)
        VALUES (?, ?)
        """;

        connection.setAutoCommit(false);

        try (PreparedStatement checkStmt = connection.prepareStatement(capacityCheck)) {

            checkStmt.setInt(1, classId);
            checkStmt.setInt(2, classId);

            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                connection.rollback();
                throw new SQLException("Class does not exist.");
            }

            int capacity = rs.getInt("capacity");
            int enrolled = rs.getInt("enrolled");

            if (enrolled >= capacity) {
                connection.rollback();
                System.out.println("Class is full.");
                return false;
            }

            try (PreparedStatement insertStmt = connection.prepareStatement(insert)) {
                insertStmt.setString(1, email);
                insertStmt.setInt(2, classId);

                insertStmt.executeUpdate();
            }

            connection.commit();
            return true;

        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }
}
