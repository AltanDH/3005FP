import javax.xml.crypto.Data;
import java.sql.*;
import java.time.LocalTime;

public class AdminStaff extends User {

    public AdminStaff(String email, String password) {
        email_ = email;
        password_ = password;
    }

    public boolean bookRoomForClass(Connection connection,
                                    int classId,
                                    int roomId,
                                    LocalTime startTime,
                                    int durationMinutes) throws SQLException {

        LocalTime endTime = startTime.plusMinutes(durationMinutes);

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
            check.setTime(2, Time.valueOf(endTime));
            check.setTime(3, Time.valueOf(startTime));
            check.setInt(4, classId);

            ResultSet rs = check.executeQuery();
            if (rs.next()) {
                System.out.println("Room is already booked for that time.");
                return false;
            }
        }

        try (PreparedStatement stmt = connection.prepareStatement(update)) {
            stmt.setInt(1, roomId);
            stmt.setTime(2, Time.valueOf(startTime));
            stmt.setInt(3, durationMinutes);
            stmt.setInt(4, classId);
            stmt.executeUpdate();
            return true;
        }
    }

    public boolean logEquipmentIssue(Connection connection,
                                     String adminEmail,
                                     int equipmentId,
                                     String operationalStatus,
                                     String issueType,
                                     String issueDesc) throws SQLException {

        String sql = """
        INSERT INTO Reports (email, equipment_id, operational_status, issue_type)
        VALUES (?, ?, ?, ?)
        """;

        String updateEquipment = """
        UPDATE Equipment
        SET status = ?, issue_type = ?, issue_desc = ?
        WHERE equipment_id = ?
        """;

        connection.setAutoCommit(false);

        try (PreparedStatement rep = connection.prepareStatement(sql);
             PreparedStatement upd = connection.prepareStatement(updateEquipment)) {

            rep.setString(1, adminEmail);
            rep.setInt(2, equipmentId);
            rep.setString(3, operationalStatus);
            rep.setString(4, issueType);
            rep.executeUpdate();

            upd.setString(1, operationalStatus);
            upd.setString(2, issueType);
            upd.setString(3, issueDesc);
            upd.setInt(4, equipmentId);
            upd.executeUpdate();

            connection.commit();
            return true;

        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public int createClass(Connection connection,
                           int roomId,
                           String type,
                           int capacity,
                           int duration,
                           LocalTime startTime,
                           boolean recurringWeekly,
                           String trainerEmail) throws SQLException {

        String insertClass = """
        INSERT INTO GroupFitnessClasses (room_id, type, capacity, duration, start_time, recurring_weekly)
        VALUES (?, ?, ?, ?, ?, ?)
        RETURNING class_id
        """;

        String assignTrainer = """
        INSERT INTO Teaches (email, class_id)
        VALUES (?, ?)
        """;

        connection.setAutoCommit(false);

        try (PreparedStatement cls = connection.prepareStatement(insertClass);
             PreparedStatement teach = connection.prepareStatement(assignTrainer)) {

            cls.setInt(1, roomId);
            cls.setString(2, type);
            cls.setInt(3, capacity);
            cls.setInt(4, duration);
            cls.setTime(5, Time.valueOf(startTime));
            cls.setBoolean(6, recurringWeekly);

            ResultSet rs = cls.executeQuery();
            if (!rs.next()) throw new SQLException("Failed to create class.");

            int classId = rs.getInt(1);

            teach.setString(1, trainerEmail);
            teach.setInt(2, classId);
            teach.executeUpdate();

            connection.commit();
            return classId;

        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }
}
