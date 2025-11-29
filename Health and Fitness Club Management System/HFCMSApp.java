import java.sql.*;
import java.util.Scanner;

public class HFCMSAPP {

    // Main function which runs the program's loop
    public static void main(String[] args) {
        // Create a database handler
        DatabaseHandler dbHandler = new DatabaseHandler();

        // Try to connect to the server and if so, run the main loop
        try (Connection connection = DriverManager.getConnection(dbHandler.getURL(), dbHandler.getUser(), dbHandler.getPassword())) {
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
                        dbHandler.getAllStudents(connection);
                        break;
                    case 2: // Add a student
                        dbHandler.addStudent(connection);
                        break;
                    case 3: // Update the student email
                        dbHandler.updateStudentEmail(connection);
                        break;
                    case 4: // Delete a student
                        dbHandler.deleteStudent(connection);
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

}