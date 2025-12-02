import java.sql.*;
import java.time.LocalTime;
import java.util.Scanner;

public class AdminStaff extends User {

    public AdminStaff(String email, String password) {
        email_ = email;
        password_ = password;
    }

    public boolean bookRoomForClass(Connection connection, Scanner scanner) throws SQLException {
        // Display current existing classes
        System.out.println("\nHere is the current Class Schedule: ");
        System.out.println(DatabaseHandler.getAll(connection, "groupfitnessclasses", new String[]{""}));

        // Display current Rooms
        System.out.println("Here are the existing Rooms: ");
        System.out.println(DatabaseHandler.getAll(connection, "rooms", new String[]{""}));

        // Prompt user for update parameters
        System.out.println("Please enter booking details: ");
        System.out.print("Class ID: ");
        int classId = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Room ID: ");
        int roomId = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Start Time of Booking (e.g. HH:mm): ");
        LocalTime startTime = LocalTime.parse(scanner.nextLine());
        System.out.print("Duration (minutes): ");
        int duration = scanner.nextInt();
        scanner.nextLine();
        // Calculate endTime
        LocalTime endTime = startTime.plusMinutes(duration);

        String overlapCheck = """
        SELECT 1 FROM GroupFitnessClasses
        WHERE room_id = ?
          AND ( ? < (start_time + (duration || ' minutes')::interval)
                AND ? > start_time )
          AND class_id != ?
        """;

        String update = """
        UPDATE GroupFitnessClasses
        SET room_id = ?, start_time = ?, duration = ?
        WHERE class_id = ?
        """;

        try (PreparedStatement check = connection.prepareStatement(overlapCheck)) {
            check.setInt(1, roomId);
            check.setTime(2, Time.valueOf(startTime));
            check.setTime(3, Time.valueOf(endTime));
            check.setInt(4, classId);

            System.out.println(check);
            ResultSet rs = check.executeQuery();
            if (rs.next()) {
                System.out.println("Room is already booked for that time.");
                return false;
            }
        }

        try (PreparedStatement stmt = connection.prepareStatement(update)) {
            stmt.setInt(1, roomId);
            stmt.setTime(2, Time.valueOf(startTime));
            stmt.setInt(3, duration);
            stmt.setInt(4, classId);

            System.out.println(stmt);
            stmt.executeUpdate();
            return true;
        }
    }

    public boolean updateEquipmentIssue(Connection connection, Scanner scanner) throws SQLException {

        // Display current equipemnt
        System.out.println("\nHere's the current equipment: ");
        System.out.println(DatabaseHandler.getAll(connection, "equipment", new String[]{""}));

        // Prompt user for update parameters
        System.out.println("Please enter equipment issue details: ");
        System.out.print("Equipment ID: ");
        int equipmentId = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Operational Status: ");
        String operationalStatus = scanner.nextLine();
        System.out.print("Issue Type (e.g. Structural): ");
        String issueType = scanner.nextLine();
        System.out.print("Issue Desc: ");
        String issueDesc = scanner.nextLine();

        String sql = """
        INSERT INTO Reports (email, equipment_id, operational_status, issue_type)
        VALUES (?, ?, ?, ?)
        """;

        String updateEquipment = """
        UPDATE Equipment
        SET status = ?, issue_type = ?, issue_desc = ?
        WHERE equipment_id = ?
        """;

        try (PreparedStatement rep = connection.prepareStatement(sql);
             PreparedStatement upd = connection.prepareStatement(updateEquipment)) {

            rep.setString(1, email_);
            rep.setInt(2, equipmentId);
            rep.setString(3, operationalStatus);
            rep.setString(4, issueType);

            System.out.println(rep);
            rep.executeUpdate();

            upd.setString(1, operationalStatus);
            upd.setString(2, issueType);
            upd.setString(3, issueDesc);
            upd.setInt(4, equipmentId);

            System.out.println(upd);
            upd.executeUpdate();

            return true;

        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }
        catch (Exception e) {
            System.out.print(e);
            return false;
        }
    }

    public boolean createClass(Connection connection, Scanner scanner) throws SQLException {

        // Display current Class Schedule
        System.out.println("\nHere is the current Class Schedule: ");
        System.out.println(DatabaseHandler.getAll(connection, "groupfitnessclasses", new String[]{""}));
        // Prompt user for new class parameters
        System.out.println("Please enter new Class details: ");
        System.out.print("Type (e.g. Yoga): ");
        String type = scanner.nextLine();
        System.out.print("Capacity: ");
        int capacity = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Start Time (e.g. HH:mm): ");
        LocalTime startTime = LocalTime.parse(scanner.nextLine());
        System.out.print("Duration (minutes): ");
        int duration = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Recurring Weekly (True/False): ");
        boolean recurringWeekly = scanner.nextBoolean();
        // Calculate endTime
        LocalTime endTime = startTime.plusMinutes(duration);

        // Display existing rooms to attempt booking
        System.out.println("\nHere are the existing rooms to choose from: ");
        System.out.println(DatabaseHandler.getAll(connection, "rooms", new String[]{""}));
        System.out.print("Room ID: ");
        int roomId = scanner.nextInt();
        scanner.nextLine();
        // Make overlap check
        String overlapCheck = """
        SELECT 1 FROM GroupFitnessClasses
        WHERE room_id = ?
          AND ( ? < (start_time + (duration || ' minutes')::interval)
                AND ? > start_time )
        """;
        try (PreparedStatement check = connection.prepareStatement(overlapCheck)) {
            check.setInt(1, roomId);
            check.setTime(2, Time.valueOf(startTime));
            check.setTime(3, Time.valueOf(endTime));

            System.out.println(check);
            ResultSet rs = check.executeQuery();
            if (rs.next()) {
                System.out.println("Room is already booked for that time.");
                return false;
            }
        }

        // Display Trainers to choose w/ Availabiltiy Periods
        System.out.println("\nHere's a list of Trainers to choose from with their availability periods: ");
        System.out.println(DatabaseHandler.getAll(connection, "availabilityperiods", new String[]{""}));
        System.out.print("Email (e.g. fname.trainer@fitclub.com): ");
        String trainerEmail = scanner.nextLine();


        String insertClass = """
        INSERT INTO GroupFitnessClasses (room_id, type, capacity, duration, start_time, recurring_weekly)
        VALUES (?, ?, ?, ?, ?, ?)
        RETURNING class_id
        """;

        String assignTrainer = """
        INSERT INTO Teaches (email, class_id)
        VALUES (?, ?)
        """;

        try (PreparedStatement cls = connection.prepareStatement(insertClass);
             PreparedStatement teach = connection.prepareStatement(assignTrainer)) {

            cls.setInt(1, roomId);
            cls.setString(2, type);
            cls.setInt(3, capacity);
            cls.setInt(4, duration);
            cls.setTime(5, Time.valueOf(startTime));
            cls.setBoolean(6, recurringWeekly);

            System.out.println(cls);
            ResultSet rs = cls.executeQuery();
            if (!rs.next()) throw new SQLException("Failed to create class.");

            int classId = rs.getInt(1);

            teach.setString(1, trainerEmail);
            teach.setInt(2, classId);

            System.out.println(teach);
            teach.executeUpdate();
            return true;

        }
        catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }
}
