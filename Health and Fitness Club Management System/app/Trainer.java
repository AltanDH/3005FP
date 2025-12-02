import java.sql.*;
import java.time.LocalTime;
import java.util.*;

public class Trainer extends User {

    public Trainer(String email, String password) {
        email_ = email;
        password_ = password;
    }

    public boolean setAvailabilityPeriod(Connection connection, Scanner scanner) throws SQLException {
        // Show current availability periods
        System.out.println("\nHere are your currently specified availability periods: ");
        displayAvailabilityPeriods(connection);

        // Prompt user for insert parameters
        System.out.println("Please enter period details: ");
        System.out.print("Day of the week: ");
        String day = scanner.nextLine();
        System.out.print("Start Time (e.g. HH:mm): ");
        LocalTime start = LocalTime.parse(scanner.nextLine());
        System.out.print("End Time (e.g. HH:mm): ");
        LocalTime end = LocalTime.parse(scanner.nextLine());
        System.out.print("Recurring Weekly (True/False): ");
        Boolean recurringWeekly = scanner.nextBoolean();

        String insert = """
        INSERT INTO AvailabilityPeriods (email, day, start_time, end_time, recurring_weekly)
        VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = connection.prepareStatement(insert)) {
            stmt.setString(1, email_);
            stmt.setString(2, day);
            stmt.setTime(3, Time.valueOf(start));
            stmt.setTime(4, Time.valueOf(end));
            stmt.setBoolean(5, recurringWeekly);

            System.out.println(stmt);
            stmt.executeUpdate();

            // Show updated availability periods
            System.out.println("\nHere are your updated availability periods: ");
            displayAvailabilityPeriods(connection);
            return true;
        }
        catch (SQLException e) {
            System.out.println(e);
            return false;
        }
    }

    public void displaySchedule(Connection connection) throws SQLException {

        // Get schedule info from Teaches relationship
        String sql = """
                SELECT t.email, c.* FROM Teaches t
                JOIN GroupFitnessClasses c ON t.class_id = c.class_id
                WHERE t.email = ?;
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

    public void memberLookup(Connection connection, Scanner scanner) throws SQLException {

        // Display all members the Trainer is in charge of to take their pick
        System.out.println("\nThe following is the list of all Members that you train: ");
        String select = "SELECT DISTINCT member_first_name, member_last_name FROM trainermemberslookup WHERE trainer_email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(select)) {
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
            System.out.println(e);
        }

        // Prompt for desired Member lookup details
        System.out.println("Please enter the name of the Member you'd like to lookup: ");
        System.out.print("First Name: ");
        String first_name = scanner.nextLine();
        System.out.print("Last Name: ");
        String last_name = scanner.nextLine();

        // Display lookup view details per Trainer parameters
        String sql = """
                SELECT * FROM TrainerMembersLookup 
                WHERE trainer_email = ? 
                AND LOWER(member_first_name) LIKE LOWER(?) 
                AND LOWER(member_last_name) LIKE LOWER(?);
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email_);
            stmt.setString(2, first_name);
            stmt.setString(3, last_name);

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
            System.out.println("Something went wrong when accessing view.");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void displayAvailabilityPeriods(Connection connection) {
        String sql = """
                SELECT * FROM AvailabilityPeriods p
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
