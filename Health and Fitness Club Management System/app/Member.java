import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Member extends User {

    public Member(String email, String password, String role) {
        email_ = email;
        password_ = password;
        role_ = role;
    }

    public boolean updateMemberProfile(Connection connection,
                                       String email,
                                       String firstName,
                                       String lastName,
                                       String phoneNumber) throws SQLException {

        String sql = """
        UPDATE Members
        SET first_name = ?, last_name = ?, phone_number = ?
        WHERE email = ?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, phoneNumber);
            stmt.setString(4, email);

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean setFitnessGoal(Connection connection,
                                  String email,
                                  String type,
                                  int value) throws SQLException {

        String sql = """
        INSERT INTO FitnessGoals (email, type, value)
        VALUES (?, ?, ?)
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, type);
            stmt.setInt(3, value);

            stmt.executeUpdate();
            return true;
        }
    }

    public boolean logHealthMetric(Connection connection,
                                   String email,
                                   int weight,
                                   int height,
                                   int heartRate,
                                   int bodyFatPct) throws SQLException {

        String sql = """
        INSERT INTO HealthMetrics (email, weight, height, heart_rate, body_fat_pct)
        VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setInt(2, weight);
            stmt.setInt(3, height);
            stmt.setInt(4, heartRate);
            stmt.setInt(5, bodyFatPct);

            stmt.executeUpdate();
            return true;
        }
    }

    public List<Map<String, Object>> getHealthHistory(Connection connection, String email) throws SQLException {
        String sql = """
        SELECT weight, height, heart_rate, body_fat_pct, created_at
        FROM HealthMetrics
        WHERE email = ?
        ORDER BY created_at DESC
        """;

        List<Map<String, Object>> history = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("weight", rs.getInt("weight"));
                row.put("height", rs.getInt("height"));
                row.put("heart_rate", rs.getInt("heart_rate"));
                row.put("body_fat_pct", rs.getInt("body_fat_pct"));
                row.put("timestamp", rs.getTimestamp("created_at"));
                history.add(row);
            }
        }
        return history;
    }

    public boolean registerForClass(Connection connection, String email, int classId) throws SQLException {

        String capacityCheck = """
        SELECT capacity,
               (SELECT COUNT(*) FROM Participates WHERE class_id = ?) AS enrolled
        FROM GroupFitnessClasses
        WHERE class_id = ?
        """;

        String insert = """
        INSERT INTO Participates (email, class_id)
        VALUES (?, ?)
        """;

        connection.setAutoCommit(false);

        try (PreparedStatement checkStmt = connection.prepareStatement(capacityCheck)) {

            checkStmt.setInt(1, classId);
            checkStmt.setInt(2, classId);

            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                connection.rollback();
                throw new SQLException("Class does not exist.");
            }

            int capacity = rs.getInt("capacity");
            int enrolled = rs.getInt("enrolled");

            if (enrolled >= capacity) {
                connection.rollback();
                System.out.println("Class is full.");
                return false;
            }

            try (PreparedStatement insertStmt = connection.prepareStatement(insert)) {
                insertStmt.setString(1, email);
                insertStmt.setInt(2, classId);

                insertStmt.executeUpdate();
            }

            connection.commit();
            return true;

        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }
}
