import javax.xml.crypto.Data;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class HFCMSApp {

    private static DatabaseHandler dbHandler;
    private static Connection connection;

    // Main function which runs the program's loop
    public static void main(String[] args) {

        // Create a database handler
        dbHandler = new DatabaseHandler();

        // Try to connect to the server and if so, run the main loop
        try {
            connection = DriverManager.getConnection(
                    dbHandler.getURL(),
                    dbHandler.getUser(),
                    dbHandler.getPassword()
            );

            System.out.println("Database connection successful.");

            // Scanner to grab user input from the console
            Scanner scanner = new Scanner(System.in);

            // REPL for Login (will also act as main REPL)
            // Asks the user who they're logging in as, or if they'd like to create a new account
            User user = new User(dbHandler);
            while (true) {
                // Ask the user who they're logging in as
                System.out.println("\n--- LOGIN PORTAL ---");
                System.out.println("0. Exit");
                System.out.println("1. Login as Member");
                System.out.println("2. Login as Trainer");
                System.out.println("3. Login as Admin");
                System.out.println("4. Register a New Member");
                System.out.print("Choice (e.g. 1): ");

                // Get the user input
                String choice = scanner.nextLine();

                // Route the program to the choice the user selected
                switch (choice) {
                    case "1":
                        // Begin Member REPL
                        memberREPL(scanner, user);
                        break;

                    case "2":
                        // Begin Trainer REPL
                        trainerREPL(scanner, user);
                        break;

                    case "3":
                        // Begin AdminStaff REPL
                        adminStaffREPL(scanner, user);
                        break;

                    case "4":
                        // Create new Member account
                        System.out.println("Please enter your credentials: ");
                        System.out.print("Email (e.g. fname.lname@email.com): ");
                        String email = scanner.nextLine();
                        System.out.print("First Name: ");
                        String firstN = scanner.nextLine();
                        System.out.print("Last Name: ");
                        String lastN = scanner.nextLine();
                        System.out.print("Password: ");
                        String password = scanner.nextLine();
                        System.out.print("Birth Date (e.g. YYYY-MM-DD): ");
                        String date = scanner.nextLine();
                        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        LocalDate birthDate = LocalDate.parse(date, format);
                        System.out.print("Gender (e.g. M): ");
                        String gender = scanner.nextLine();
                        System.out.print("Phone Number (e.g. XXX-XXX-XXXX): ");
                        String phone = scanner.nextLine();

                        user.registerMember(connection, email, firstN, lastN, password, birthDate, gender, phone);

                        // Show updated Members table for Testing purposes
                        System.out.println("Here are the updated Members accounts for testing purposes:");
                        System.out.println(dbHandler.getAll(connection, "members", new String[]{}));
                        break;

                    case "0": // Exit the program
                        System.out.println("\n Exiting the program...");
                        System.exit(0); // Exit the program
                    default: // If the user enters anything else, consider it invalid
                        System.out.println("\n INVALID CHOICE! Please enter a number from 0 to 4");
                }
            }

        } catch (Exception exception) { // Catch and print the stack trace for debugging
            exception.printStackTrace();
        }
    }

    // ------------------------------------
    // ------     Member REPL       -------
    // ------------------------------------
    private static void memberREPL(Scanner scanner, User user) throws SQLException {
        // For testing purposes, display existing accounts
        System.out.println("\nFor Testing Purposes, here are the existing Members: ");
        String allMembers = dbHandler.getAll(connection, "members", new String[]{""});
        System.out.println(allMembers);

        // Logging in as Member
        System.out.println("\nPlease enter your credentials: ");
        System.out.print("Email (e.g. fname.lname@email.com): ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        if (!user.findUser(connection, email, password, "members")) {
            System.out.println("User not found.");
            return;
        }

        Member member = new Member(email, password, dbHandler);
        System.out.println("Successfully logged in!");

        while (true) {
            System.out.println("\n--- MEMBER MENU ---");
            System.out.println("0. Logout");
            System.out.println("1. Update personal details");
            System.out.println("2. Add a Fitness Goal");
            System.out.println("3. Add a Health Metric");
            System.out.println("4. View Health History");
            System.out.println("5. Register for a Group Class");
            System.out.print("Choice (e.g. 1): ");

            // Get the user input
            String choice = scanner.nextLine();

            // Route the program to the choice the user selected
            switch (choice) {
                case "1":
                    if (member.updateMemberProfile(connection, scanner)) {
                        System.out.println("Details changed successfully.");
                    } else {
                        System.out.println("Details couldn't be updated.");
                    }
                    // Show updated member profile
                    System.out.println("\nHere's your updated member profile: ");
                    System.out.println(dbHandler.getAll(connection, "members", new String[]{member.getEmail()}));
                    break;

                case "2":
                    // Add Fitness Goals
                    if (member.setFitnessGoal(connection, scanner)) {
                        System.out.println("Goal added successfully.");
                    }
                    else {
                        System.out.println("Goal adding failed.");
                    }
                    break;

                case "3":
                    // Add Health Metrics
                    if (member.logHealthMetric(connection, scanner)) {
                        System.out.println("Health metric added successfully.");
                    }
                    else {
                        System.out.println("Health Metric updated successfully.");
                    }
                    break;

                case "4":
                    // Display Health History
                    member.getHealthHistory(connection);
                    break;

                case "0":
                    System.out.print("Logging out...");
                    return;
            }
        }
    }


    // ------------------------------------
    // ------     Trainer REPL     --------
    // ------------------------------------
    private static void trainerREPL(Scanner scanner, User user) throws SQLException {
        // Logging in as Trainer
        System.out.println("\nPlease enter your credentials: ");
        System.out.print("Email (e.g. fname.lname@email.com): ");
        String email = scanner.nextLine();
        // Eliminate anything left in the scanner's buffer
        System.out.print("Password: ");
        String password = scanner.nextLine();
        // Eliminate next line symbol
        if (user.findUser(connection, email, password, "Trainers")) {
            //Trainer trainer = new Trainer(email, password, dbHandler);

        }
    }


    // --------------------------------------
    // ------    AdminStaff REPL      -------
    // --------------------------------------
    private static void adminStaffREPL(Scanner scanner, User user) throws SQLException {
        // Logging in as Admin
        System.out.println("Please enter your credentials: ");
        System.out.print("Email (e.g. fname.lname@email.com): ");
        String email = scanner.nextLine();
        // Eliminate anything left in the scanner's buffer
        System.out.print("Password: ");
        String password = scanner.nextLine();
        // Eliminate next line symbol
        if (user.findUser(connection, email, password, "AdminStaff")) {
            AdminStaff admin = new AdminStaff(email, password, dbHandler);

        }
    }
}