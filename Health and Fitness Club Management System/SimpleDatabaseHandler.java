import java.sql.*;
import java.util.Scanner;

public class SimpleDatabaseHandler {

    // Get the required data to connect to the database
    // localhost:5432 is the default port Postgres listens on
    private static final String DATABASE_NAME = "3005A3"; // Your DB name (edit here)
    private static final String USER = "postgres"; // Username (edit here)
    private static final String PASSWORD = "yourPassword"; // User's password (edit here)
    private static final String URL = "jdbc:postgresql://localhost:5432/" + DATABASE_NAME; // (Do not change)

    // Main function which runs the program's loop
    public static void main(String[] args) {
        // Try to connect to the server and if so, run the main loop
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            System.out.println("Database connection successful");

            // Scanner to grab user input from the console
            Scanner scanner = new Scanner(System.in);

            // Main Loop
            // Asks the user what they want to do, then ask user for the data required to do it
            while (true) { // Will loop until the user specifies they want to exit the program
                // Ask the user what they would like to do next
                System.out.println("\nChoose an Option:");
                System.out.println("1. View all students");
                System.out.println("2. Add a new student");
                System.out.println("3. Update a student's email");
                System.out.println("4. Delete a student");
                System.out.println("5. Exit");
                System.out.print("Choice: ");

                // Get the user input as an int
                int choice = scanner.nextInt();
                scanner.nextLine(); // Eliminate anything left in the scanner's buffer

                // Route the program to the choice the user selected
                switch (choice) {
                    case 1: // Get all students
                        getAllStudents(connection);
                        break;
                    case 2: // Add a student
                        addStudent(connection);
                        break;
                    case 3: // Update the student email
                        updateStudentEmail(connection);
                        break;
                    case 4: // Delete a student
                        deleteStudent(connection);
                        break;
                    case 5: // Exit the program
                        System.out.println("\n Exiting the program...");
                        System.exit(0); // Exit the program
                    default: // If the user enters anything else, consider it invalid
                        System.out.println("\n ERROR! Please enter a number from 1 to 5");
                }
            }
        } catch (SQLException exception) { // Catch and print the stack trace for debugging
            exception.printStackTrace();
        }
    }


    // ---------- CRUD OPERATION FUNCTIONS ----------
    // Retrieves and displays all students
    // This is made with the JDBC documentation to grab data from the Postgres database
    // First create a statement with a connection, then execute the statement and store the result
    // Print the result, then close the result and statement
    public static void getAllStudents(Connection connection) throws SQLException {
        System.out.println("\nGetting all students...");

        // Basic query, gets all tuples in the database
        String query = "SELECT * FROM students";

        // Grab the result of a query
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(query);

        // While there is a result, print the result
        System.out.println("student_id | first_name | last_name | email | enrollment_date");
        while (result.next()) {
            // Integer, string, string, string, string, newline pattern
            System.out.printf("%d | %s | %s | %s | %s%n",
                    // Columns
                    result.getInt(1),
                    result.getString(2),
                    result.getString(3),
                    result.getString(4),
                    result.getDate(5)
            );
        }

        result.close();
        statement.close();
    }

    // Adds a new student to the database given all fields
    // This is made with the JDBC documentation to set data in the Postgres database
    // First creates a query and scanner to grab user data for a new student's fields
    // Then it creates a prepared statement which binds data in the query
    public static void addStudent(Connection connection) throws SQLException {
        // Create a scanner object to ask the user what the new student's fields are
        Scanner scanner = new Scanner(System.in);

        // Ask user for student info
        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter enrollment date (YYYY-MM-DD): ");
        String enrollmentDate = scanner.nextLine();

        // Basic query, "?" gets replaced by the values in order of first_name, last_name, email, and enrollment_date
        String query = "INSERT INTO students (first_name, last_name, email, enrollment_date) VALUES (?, ?, ?, ?)";

        // Update the data
        // Create a PreparedStatement and store it to set values of "?"
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            // Number specifies the question mark which gets updated from left to right
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, email);
            statement.setDate(4, Date.valueOf(enrollmentDate));

            // Execute the query
            statement.executeUpdate();
            System.out.println("\nStudent added successfully");

        } catch (IllegalArgumentException illegalArgumentException) { // Date object throws an error if invalid
            System.out.println("\nError! Invalid date entered");
        } catch (Exception exception) {
            System.out.println("\nError! Invalid data entered");
            exception.printStackTrace();
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
}