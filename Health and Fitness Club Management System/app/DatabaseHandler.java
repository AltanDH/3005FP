import java.sql.*;
import java.util.Scanner;

public class DatabaseHandler {
    // Get the required data to connect to the database
    // localhost:5432 is the default port Postgres listens on
    private static final String DATABASE_NAME = "3005FP"; // Your DB name (edit here)
    private static final String USER = "postgres"; // Username (edit here)
    private static final String PASSWORD = "Teddy2005*"; // User's password (edit here)
    private static final String URL = "jdbc:postgresql://localhost:5432/" + DATABASE_NAME; // (Do not change)

    // Getters
    public static String getName() { return DATABASE_NAME; }
    public static String getUser() { return USER; }
    public static String getPassword() { return PASSWORD; }
    public static String getURL() { return URL; }


    // ---------- CRUD OPERATION FUNCTIONS ----------
    // Retrieves and displays all students
    // This is made with the JDBC documentation to grab data from the Postgres database
    // First create a statement with a connection, then execute the statement and store the result
    // Print the result, then close the result and statement
    public static void getAll(Connection connection, String table) {
        System.out.println("\nGetting all data from table " + table);

        // Basic query, gets all tuples in the database
        String query = "SELECT * FROM " + table;

        try {
            // Grab the result of a query
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(query);

            // Get the metadata and column count of a passed in query
            ResultSetMetaData metaData = result.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Print column names
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(metaData.getColumnName(i));
                if (i < columnCount) { System.out.print(" | "); }
            }
            System.out.println();

            // Print each tuple
            while (result.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(result.getString(i)); // Uses getString for any type
                    if (i < columnCount) System.out.print(" | ");
                }
                System.out.println();
            }
            result.close();
            statement.close();

        } catch (SQLException sqlException) {
            System.out.println("\nError! Invalid Query Entered");
        }
    }

    // Adds a new student to the database given all fields
    // This is made with the JDBC documentation to set data in the Postgres database
    // First creates a query and scanner to grab user data for a new student's fields
    // Then it creates a prepared statement which binds data in the query
    public static void addTuple(Connection connection, String table, String[] values) throws SQLException {
        // Joins the query with the table and fields of the tuple to create a syntactically valid query
        String[] colNames = getModifiableColumnNames(connection, table);
        String query = "INSERT INTO " + table + " (" + String.join(", ", colNames) + ") VALUES (";
        for (int i = 0; i < colNames.length - 1; i++) {
            query += "?, ";
        }
        query += "?)";

        // Update the data
        try  {
            // Create a PreparedStatement and store it to set values of "?"
            PreparedStatement statement = connection.prepareStatement(query);
            // Number specifies the question mark which gets updated from left to right
            for (int i = 0; i < colNames.length; i++) {
                statement.setString(i + 1, values[i]);
            }

            // Execute the query
            statement.executeUpdate();
            System.out.println("\nAdded successfully");

        } catch (SQLException sqlException) {
            System.out.println("\nError! Invalid Query Entered");
        }
        catch (Exception exception) {
            System.out.println(query);
            System.out.println("\nError! Invalid data entered");
        }
    }

    // Updates a student's email using their ID
    // Grabs the data from the user, then updates the specified student
    public static void updateStudentEmail(Connection connection) {
        // Scanner for getting the new email and the student id that will be updated
        Scanner scanner = new Scanner(System.in);

        // Get the data
        System.out.print("Enter student_id you would like to update: ");
        int student_id = scanner.nextInt();
        scanner.nextLine(); // Clear the newline symbol so newEmail reads properly
        System.out.print("Enter the new email for this student: ");
        String newEmail = scanner.nextLine();

        // Basic query, "?" gets replaced by the value of email and student_id
        String query = "UPDATE students SET email = ? WHERE student_id = ?";

        // Update the data
        // Create a PreparedStatement and store it to set values of "?"
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            // Number specifies the question mark which gets updated from left to right
            statement.setString(1, newEmail);
            statement.setInt(2, student_id);

            // Execute the query
            int rowsAltered = statement.executeUpdate();
            if (rowsAltered <= 0) { // If no rows were changed then no data was updated
                System.out.println("\nstudent_id does not exist, no data was altered");
            } else { // Otherwise data was updated
                System.out.println("\nStudent email updated successfully");
            }

        } catch (Exception exception) {
            System.out.println("\nError! Invalid data entered");
            exception.printStackTrace();
        }
    }

    // Deletes a student by ID
    // Asks the user for a student_id and deletes the student with that student_id
    public static void deleteStudent(Connection connection) {
        // Scanner for getting the ID of the student to delete
        Scanner scanner = new Scanner(System.in);

        // Get the data
        System.out.println("Enter the student_id of the student to delete: ");
        int goner = scanner.nextInt();

        // Basic query, "?" gets replaced by the value of "goner"
        String query = "DELETE FROM students WHERE student_id = ?";

        // Perform the deletion
        // Create a PreparedStatement and store it to set values of "?"
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            // Number specifies the question mark which gets updated from left to right
            statement.setInt(1, goner);

            // Execute the query
            int rowsAltered = statement.executeUpdate();
            if (rowsAltered <= 0) { // If no rows were changed then no data was updated
                System.out.println("\nstudent_id does not exist, no data was altered");
            } else { // Otherwise data was updated
                System.out.println("\nStudent removed successfully");
            }

        } catch (Exception exception) {
            System.out.println("\nError! Invalid data entered");
            exception.printStackTrace();
        }
    }

    // ---------- HELPER FUNCTIONS ----------
    // Get the names of each column in a table dynamically
    public static String[] getColumnNames(Connection connection, String table) throws SQLException {
        // Try not to spend time returning columns
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery("SELECT * FROM " + table + " LIMIT 0"); // Only need metadata
        ResultSetMetaData metaData = result.getMetaData();

        // Get the number of columns
        int columnCount = metaData.getColumnCount();
        String[] columnNames = new String[columnCount];

        // Loop through columns and get the name of each
        for (int i = 1; i <= columnCount; i++) {
            columnNames[i - 1] = metaData.getColumnName(i); // ResultSetMetaData is 1-based
        }

        result.close();
        statement.close();

        return columnNames;
    }

    // Since some columns are not modifiable like fields defined as AUTO INCREMENT, do not return those
    public static String[] getModifiableColumnNames(Connection connection, String table) throws SQLException {
        // Create a statement, provide a fast dummy query, and get the metadata
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery("SELECT * FROM " + table + " LIMIT 0"); // Only need metadata
        ResultSetMetaData metaData = result.getMetaData();

        // Get the number of columns
        int columnCount = metaData.getColumnCount();

        // Count number of modifiable columns
        int modifiableCount = 0;
        for (int i = 1; i <= columnCount; i++) {
            if (!metaData.isAutoIncrement(i)) {
                modifiableCount++;
            }
        }

        String[] columnNames = new String[modifiableCount];

        // Add every column that is not auto increment
        int backCounter = 0;
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            if (!metaData.isAutoIncrement(i)) { // Skip auto-increment columns
                columnNames[i - 1 - backCounter] = metaData.getColumnName(i);
            } else {
                backCounter++;
            }
        }

        result.close();
        statement.close();

        return columnNames;
    }

}
