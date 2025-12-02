import java.sql.*;
import java.util.*;

public class Member extends User {

    public Member(String email, String password) {
        email_ = email;
        password_ = password;
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

        String update = """
        UPDATE Members
        SET email = ?, first_name = ?, last_name = ?, phone_number = ?
        WHERE email = ?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(update)) {
            stmt.setString(1, email);
            stmt.setString(2, firstName);
            stmt.setString(3, lastName);
            stmt.setString(4, phoneNumber);
            stmt.setString(5, email_);

            System.out.println(stmt);
            if (stmt.executeUpdate() > 0) {
                email_ = email;
                return true;
            }
            return false;
        }
        catch (SQLException e) {
            System.out.println(e);
            return false;
        }
    }

    public boolean setFitnessGoal(Connection connection, Scanner scanner) throws SQLException {
        // Prompt for fitness goal details
        String type;
        int value;
        try {
            System.out.println("Please enter your goal parameters: ");
            System.out.print("Goal Type (weight/bodyfat): ");
            type = scanner.nextLine();
            System.out.print("Target Value (kg/percent): ");
            value = scanner.nextInt();
            scanner.nextLine(); // Clear leftover symbols from buffer
        }
        catch (Exception e) {
            System.out.println(e);
            System.out.println("\nPlease input proper values.");
            return false;
        }

        boolean result = DatabaseHandler.addTuple(connection, "fitnessgoals", new Object[]{email_, type, value});

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
            System.out.println(stmt);
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
            System.out.println(e);
            return false;
        }
    }

    public boolean logHealthMetric(Connection connection, Scanner scanner) throws SQLException {

        // Prompt for fitness goal details
        int weight, height, heartRate, bodyFatPct;
        try {
            System.out.println("Please enter your health parameters: ");
            System.out.print("Weight (kg): ");
            weight = scanner.nextInt();
            scanner.nextLine(); // Clear leftover symbols from buffer
            System.out.print("Height (cm): ");
            height = scanner.nextInt();
            scanner.nextLine(); // Clear leftover symbols from buffer
            System.out.print("Heart Rate (bpm): ");
            heartRate = scanner.nextInt();
            scanner.nextLine(); // Clear leftover symbols from buffer
            System.out.print("Body Fat (percentage): ");
            bodyFatPct = scanner.nextInt();
            scanner.nextLine(); // Clear leftover symbols from buffer
        }
        catch (Exception e) {
            System.out.println(e);
            System.out.println("\nPlease input proper values.");
            return false;
        }

        // Add health metric
        Timestamp now = new Timestamp(System.currentTimeMillis());
        boolean result = DatabaseHandler.addTuple(connection, "healthmetrics", new Object[]{email_, weight, height, heartRate, bodyFatPct, now});

        // Return early if no results found
        if (!result) {
            return result;
        }

        // Show updated health metrics of current member
        System.out.println("\nHere's your updated health metrics: ");
        String sql = "SELECT * FROM HealthMetrics WHERE email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email_);

            // Grab query result
            System.out.println(stmt);
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
            System.out.println(e);
            return false;
        }
    }

    public boolean getHealthHistory(Connection connection) {
        // Show updated health metrics of current member
        System.out.println("\nDisplaying Health History: ");
        String sql = "SELECT * FROM HealthMetrics WHERE email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email_);

            // Grab query result
            System.out.println(stmt);
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

    public boolean registerForClass(Connection connection, Scanner scanner) throws SQLException {
        // Display classes already registered for
        System.out.println("\nHere's a summary of the classes you've registered for: ");
        displayClassSchedule(connection);

        // Display Current Class Options
        System.out.println("And here's a list of the currently available classes: ");
        System.out.println(DatabaseHandler.getAll(connection, "groupfitnessclasses", new String[]{""}));

        // Prompt user for desired class Id to join group
        System.out.print("Please enter the ID of the class you'd like to join (-1 to cancel): ");
        int classId = scanner.nextInt();
        scanner.nextLine(); // Clear leftover symbols from buffer

        if (classId == -1) {
            System.out.println("Class registration cancelled.");
            return true;
        }

        // Check Capacity
        String capacityCheck = """
        SELECT capacity,
               (SELECT COUNT(*) FROM Participates WHERE class_id = ?) AS enrolled
        FROM GroupFitnessClasses
        WHERE class_id = ?
        """;

        // Add Student into proper relationship
        String insert = """
        INSERT INTO Participates (email, class_id)
        VALUES (?, ?)
        """;

        try (PreparedStatement checkStmt = connection.prepareStatement(capacityCheck)) {

            checkStmt.setInt(1, classId);
            checkStmt.setInt(2, classId);
            System.out.println(checkStmt);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Class does not exist.");
                return false;
            }

            int capacity = rs.getInt("capacity");
            int enrolled = rs.getInt("enrolled");

            if (enrolled >= capacity) {
                System.out.println("Class is full.");
                return false;
            }

            try (PreparedStatement insertStmt = connection.prepareStatement(insert)) {
                insertStmt.setString(1, email_);
                insertStmt.setInt(2, classId);

                System.out.println(insertStmt);
                insertStmt.executeUpdate();
            }

            System.out.println("Class registration complete.");

            // Display classes already registered for
            System.out.println("\nHere's your updated schedule: ");
            displayClassSchedule(connection);

            return true;
        }
        catch (SQLException e) {
            System.out.println(e);
            return false;
        }
    }

    public void displayClassSchedule(Connection connection) {
        String sql = """
                SELECT p.email, c.* FROM Participates p
                JOIN GroupFitnessClasses c ON p.class_id = c.class_id
                WHERE p.email = ?;
                """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email_);

            // Grab query result
            System.out.println(stmt);
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
        }
        catch (SQLException e) {
            System.out.println("Something went wrong when accessing table.");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
