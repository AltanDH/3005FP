import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class HFCMSApp {

    // Main function which runs the program's loop
    public static void main(String[] args) {
        // Create a database handler
        DatabaseHandler dbHandler = new DatabaseHandler();

        // Try to connect to the server and if so, run the main loop
        try (Connection connection = DriverManager.getConnection(dbHandler.getURL(), dbHandler.getUser(), dbHandler.getPassword())) {
            System.out.println("Database connection successful");

            // Scanner to grab user input from the console
            Scanner scanner = new Scanner(System.in);

            // Logging in loop
            // Asks the user who they're logging in as, or if they'd like to create a new account
            boolean logged_in = false;
            User user = new User();
            while (!logged_in) {
                // Ask the user who they're logging in as
                System.out.println("\nChoose an Option:");
                System.out.println("1. Log in as Member");
                System.out.println("2. Log in as Trainer");
                System.out.println("3. Log in as Admin");
                System.out.println("4. Register a new Member");
                System.out.println("5. Exit");
                System.out.print("Choice: ");

                // Get the user input as an int
                int choice = scanner.nextInt();
                scanner.nextLine(); // Eliminate anything left in the scanner's buffer

                // Route the program to the choice the user selected
                switch (choice) {
                    case 1: // Logging in as Member
                        System.out.println("Please enter your credentials: ");
                        System.out.print("Email (e.g. fname.lname@email.com): ");
                        String email = scanner.nextLine();
                        // Eliminate anything left in the scanner's buffer
                        System.out.print("Password: ");
                        String password = scanner.nextLine();
                        // Eliminate next line symbol
                        if (user.findUser(connection, dbHandler, email, password, "Members")) {
                            user = new Member(email, password, "Member");
                            logged_in = true;
                        }
                        break;
                    case 2: // Logging in as Trainer
                        System.out.println("Please enter your credentials: ");
                        System.out.print("Email (e.g. fname.lname@email.com): ");
                        email = scanner.nextLine();
                        // Eliminate anything left in the scanner's buffer
                        System.out.print("Password: ");
                        password = scanner.nextLine();
                        // Eliminate next line symbol
                        if (user.findUser(connection, dbHandler, email, password, "Trainers")) {
                            user = new Trainer(email, password, "Trainer");
                            logged_in = true;
                        }
                        break;
                    case 3: // Logging in as Admin
                        System.out.println("Please enter your credentials: ");
                        System.out.print("Email (e.g. fname.lname@email.com): ");
                        email = scanner.nextLine();
                        // Eliminate anything left in the scanner's buffer
                        System.out.print("Password: ");
                        password = scanner.nextLine();
                        // Eliminate next line symbol
                        if (user.findUser(connection, dbHandler, email, password, "AdminStaff")) {
                            user = new Trainer(email, password, "Admin");
                            logged_in = true;
                        }
                        break;
                    case 4: // Creating new Member account
                        System.out.println("Please enter your credentials: ");
                        System.out.print("Email (e.g. fname.lname@email.com): ");
                        email = scanner.nextLine();
                        // Eliminate anything left in the scanner's buffer
                        System.out.print("First Name: ");
                        String firstN = scanner.nextLine();
                        // Eliminate next line symbol
                        System.out.print("Last Name: ");
                        String lastN = scanner.nextLine();
                        // Eliminate next line symbol
                        System.out.print("Password: ");
                        password = scanner.nextLine();
                        // Eliminate next line symbol
                        System.out.print("Birth Date (e.g. YYYY-MM-DD): ");
                        String date = scanner.nextLine();
                        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-mm-dd");
                        LocalDate birthDate = LocalDate.parse(date, format);
                        // Eliminate next line symbol
                        System.out.print("Gender (e.g. M): ");
                        String gender = scanner.nextLine();
                        // Eliminate next line symbol
                        System.out.print("Phone Number (e.g. XXX-XXX-XXXX): ");
                        String phone = scanner.nextLine();
                        // Eliminate next line symbol
                        user.registerMember(connection, email, firstN, lastN, password, birthDate, gender, phone);
                        break;
                    case 5: // Exit the program
                        System.out.println("\n Exiting the program...");
                        System.exit(0); // Exit the program
                    default: // If the user enters anything else, consider it invalid
                        System.out.println("\n ERROR! Please enter a number from 1 to 5");
                }
            }

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
                        String[] empty = {""};
                        String[] out = dbHandler.getAll(connection,"Trainers", empty);
                        System.out.println(out);
                        break;
                    case 2: // Add a student
                        String[] test = {"alex1.trainer@fitclub.com", "Alex", "Moreno", "pass1"};
                        dbHandler.addTuple(connection, "Trainers", test);
                        break;
                    case 3: // Update the student email
                        String[] newTuple = {"1", "1", "1", "1"};
                        String[] id = {"alex1.trainer@fitclub.com"};
                        dbHandler.updateTuple(connection, "trainers", newTuple, id);
                        break;
                    case 4: // Delete a student
                        String[] id1 = {"alex1.trainer@fitclub.com"};
                        dbHandler.deleteTuple(connection, "trainers", id1);
                        break;
                    case 5: // Exit the program
                        System.out.println("\n Exiting the program...");
                        System.exit(0); // Exit the program
                    default: // If the user enters anything else, consider it invalid
                        System.out.println("\n ERROR! Please enter a number from 1 to 5");
                }
            }
        } catch (Exception exception) { // Catch and print the stack trace for debugging
            exception.printStackTrace();
        }
    }

}