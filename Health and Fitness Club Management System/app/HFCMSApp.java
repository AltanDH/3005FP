import javax.xml.crypto.Data;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class HFCMSApp {

    private static Connection connection;

    // Main function which runs the program's loop
    public static void main(String[] args) {

        // Try to connect to the server and if so, run the main loop
        try {
            connection = DriverManager.getConnection(
                    DatabaseHandler.getURL(),
                    DatabaseHandler.getUser(),
                    DatabaseHandler.getPassword()
            );

            System.out.println("Database connection successful.");

            // Scanner to grab user input from the console
            Scanner scanner = new Scanner(System.in);

            // REPL for Login (will also act as main REPL)
            // Asks the user who they're logging in as, or if they'd like to create a new account
            User user = new User();
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
                        user.registerMember(connection, scanner);

                        // Show updated Members table for Testing purposes
                        System.out.println("Here are the updated Members accounts for testing purposes:");
                        System.out.println(DatabaseHandler.getAll(connection, "members", new String[]{""}));
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
        String allMembers = DatabaseHandler.getAll(connection, "members", new String[]{""});
        System.out.println(allMembers);

        // Logging in as Member
        System.out.println("Please enter your credentials: ");
        System.out.print("Email (e.g. fname.lname@email.com): ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        if (!user.findUser(connection, email, password, "members")) {
            System.out.println("\n Login failed.");
            return;
        }

        Member member = new Member(email, password);
        System.out.println("\nSuccessfully logged in!");

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
                        user.setEmail(member.getEmail());
                        // Show updated member profile
                        System.out.println("\nHere's your updated member profile: ");
                        System.out.println(DatabaseHandler.getAll(connection, "members", new String[]{member.getEmail()}));
                    } else {
                        System.out.println("Details couldn't be updated.");
                    }
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
                        System.out.println("Health Metric addition failed.");
                    }
                    break;

                case "4":
                    // Display Health History
                    if (!member.getHealthHistory(connection)) {
                        System.out.println("Failed to display health history.");
                    }
                    break;

                case "5":
                    // Initiate Class Registration Process
                    if (!member.registerForClass(connection, scanner)) {
                        System.out.println("Class registration failed.");
                    }
                    break;

                case "0":
                    // Log out
                    System.out.println("\n Logging out...");
                    return;

                default:
                    System.out.println("\n INVALID CHOICE! Please select a number from 0 to 5");
            }
        }
    }


    // ------------------------------------
    // ------     Trainer REPL     --------
    // ------------------------------------
    private static void trainerREPL(Scanner scanner, User user) throws SQLException {
        // For testing purposes, display existing accounts
        System.out.println("\nFor Testing Purposes, here are the existing Trainers: ");
        String allMembers = DatabaseHandler.getAll(connection, "trainers", new String[]{""});
        System.out.println(allMembers);

        // Logging in as Trainer
        System.out.println("Please enter your credentials: ");
        System.out.print("Email (e.g. fname.lname@email.com): ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        if (!user.findUser(connection, email, password, "trainers")) {
            System.out.println("\n Login failed.");
            return;
        }

        Trainer trainer = new Trainer(email, password);
        System.out.println("\nSuccessfully logged in!");

        while (true) {
            System.out.println("\n--- TRAINER MENU ---");
            System.out.println("0. Logout");
            System.out.println("1. Add an Availability Period");
            System.out.println("2. View Schedule");
            System.out.println("3. Lookup Member");
            System.out.print("Choice (e.g. 1): ");

            // Get the user input
            String choice = scanner.nextLine();

            // Route the program to the choice the user selected
            switch (choice) {
                case "1":
                    if (trainer.setAvailabilityPeriod(connection, scanner)) {
                        System.out.println("Availability Periods updated successfully.");
                    }
                    break;

                case "2":
                    // Display schedule
                    System.out.println("\n Here's an overview of your teaching schedule: ");
                    trainer.displaySchedule(connection);
                    break;

                case "3":
                    // Initiate Member Lookup
                    trainer.memberLookup(connection, scanner);
                    break;

                case "0":
                    // Log out
                    System.out.println("\n Logging out...");
                    return;

                default:
                    System.out.println("\n INVALID CHOICE! Please select a number from 0 to 3");
            }
        }
    }


    // --------------------------------------
    // ------    AdminStaff REPL      -------
    // --------------------------------------
    private static void adminStaffREPL(Scanner scanner, User user) throws SQLException {
        // For testing purposes, display existing accounts
        System.out.println("\nFor Testing Purposes, here are the existing Admin Staff: ");
        String allMembers = DatabaseHandler.getAll(connection, "adminstaff", new String[]{""});
        System.out.println(allMembers);

        // Logging in as an Administrative Staff
        System.out.println("Please enter your credentials: ");
        System.out.print("Email (e.g. fname.lname@email.com): ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        if (!user.findUser(connection, email, password, "adminstaff")) {
            System.out.println("\n Login failed.");
            return;
        }

        AdminStaff admin = new AdminStaff(email, password);
        System.out.println("\nSuccessfully logged in!");

        while (true) {
            System.out.println("\n--- ADMIN STAFF MENU ---");
            System.out.println("0. Logout");
            System.out.println("1. Book Room for Classes");
            System.out.println("2. Update Equipment Issues Report");
            System.out.println("3. Setup a New Class");
            System.out.print("Choice (e.g. 1): ");

            // Get the user input
            String choice = scanner.nextLine();

            // Route the program to the choice the user selected
            switch (choice) {
                case "1":
                    // Make room booking for class
                    if (admin.bookRoomForClass(connection, scanner)) {
                        System.out.println("Booking set successfully.");
                    } else {
                        System.out.println("Booking failed.");
                    }
                    // Show updated classes schedule
                    System.out.println("\nHere's the updated class schedule: ");
                    System.out.println(DatabaseHandler.getAll(connection, "groupfitnessclasses", new String[]{""}));
                    break;

                case "2":
                    // Log new equipment issue
                    admin.updateEquipmentIssue(connection, scanner);
                    // Display updated reports
                    System.out.println("Here are the updated reports: ");
                    System.out.println(DatabaseHandler.getAll(connection, "reports", new String[]{""}));
                    // Display updated equipment status
                    System.out.println("Here's the updated equipment statuses: ");
                    System.out.println(DatabaseHandler.getAll(connection, "equipment", new String[]{""}));
                    break;

                case "3":
                    if (admin.createClass(connection, scanner)) {
                        System.out.println("Class added successfully");
                        // Display new class schedule
                        System.out.println("Here's an updated display of the Class Schedule: ");
                        System.out.println(DatabaseHandler.getAll(connection, "groupfitnessclasses", new String[]{""}));
                    }
                    else {
                        System.out.println("Failed to create new Class");
                    }
                    break;

                case "0":
                    // Log out
                    System.out.println("\n Logging out...");
                    return;

                default:
                    System.out.println("\n INVALID CHOICE! Please select a number from 0 to 3");
            }
        }
    }
}