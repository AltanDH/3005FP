import java.awt.dnd.DropTarget;
import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Trainer extends User {

    public Trainer(String email, String password) {
        email_ = email;
        password_ = password;
    }

    public boolean setAvailabilityPeriod(Connection connection,
                                          String trainerEmail,
                                          String day,
                                          LocalTime start,
                                          LocalTime end,
                                          boolean recurringWeekly) throws SQLException {
        // Two time ranges overlap if:
        // new.start < existing.end AND new.end > existing.start

        String overlapCheck = """
        SELECT 1 FROM AvailabilityPeriods
        WHERE email = ?
          AND day = ?
          AND ( ? < end_time AND ? > start_time )
        """;

        String insert = """
        INSERT INTO AvailabilityPeriods (email, day, start_time, end_time, recurring_weekly)
        VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement check = connection.prepareStatement(overlapCheck)) {
            check.setString(1, trainerEmail);
            check.setString(2, day);
            check.setTime(3, Time.valueOf(end));
            check.setTime(4, Time.valueOf(start));

            ResultSet rs = check.executeQuery();
            if (rs.next()) {
                System.out.println("Availability overlaps existing schedule.");
                return false;
            }
        }

        try (PreparedStatement stmt = connection.prepareStatement(insert)) {
            stmt.setString(1, trainerEmail);
            stmt.setString(2, day);
            stmt.setTime(3, Time.valueOf(start));
            stmt.setTime(4, Time.valueOf(end));
            stmt.setBoolean(5, recurringWeekly);
            stmt.executeUpdate();
            return true;
        }
    }

    public List<Map<String, Object>> getSchedule(Connection connection, String trainerEmail)
            throws SQLException {

        String sql = """
        SELECT c.class_id, c.room_id, c.start_time, c.duration, c.type
        FROM GroupFitnessClasses c
        JOIN Teaches t ON c.class_id = t.class_id
        WHERE t.email = ?
        ORDER BY c.start_time
        """;

        List<Map<String, Object>> schedule = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, trainerEmail);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("class_id", rs.getInt("class_id"));
                row.put("room_id", rs.getInt("room_id"));
                row.put("type", rs.getString("type"));
                row.put("start_time", rs.getTime("start_time"));
                row.put("duration", rs.getInt("duration"));
                schedule.add(row);
            }
        }

        return schedule;
    }

    public List<Map<String, Object>> memberLookup(Connection connection, String search)
            throws SQLException {

        String sql = """
        SELECT m.email, m.first_name, m.last_name,
               fg.type AS goal_type, fg.value AS goal_value,
               hm.weight, hm.height, hm.heart_rate, hm.body_fat_pct, hm.created_at
        FROM Members m
        LEFT JOIN LATERAL (
            SELECT type, value
            FROM FitnessGoals fg
            WHERE fg.email = m.email
            ORDER BY fg.goal_id DESC
            LIMIT 1
        ) fg ON true
        LEFT JOIN LATERAL (
            SELECT weight, height, heart_rate, body_fat_pct, created_at
            FROM HealthMetrics hm
            WHERE hm.email = m.email
            ORDER BY created_at DESC
            LIMIT 1
        ) hm ON true
        WHERE LOWER(m.first_name) LIKE LOWER(?) OR LOWER(m.last_name) LIKE LOWER(?)
        """;

        List<Map<String, Object>> results = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            String pattern = "%" + search + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("email", rs.getString("email"));
                row.put("first_name", rs.getString("first_name"));
                row.put("last_name", rs.getString("last_name"));
                row.put("goal_type", rs.getString("goal_type"));
                row.put("goal_value", rs.getObject("goal_value"));
                row.put("weight", rs.getObject("weight"));
                row.put("height", rs.getObject("height"));
                row.put("heart_rate", rs.getObject("heart_rate"));
                row.put("body_fat_pct", rs.getObject("body_fat_pct"));
                row.put("metric_time", rs.getObject("created_at"));
                results.add(row);
            }
        }

        return results;
    }
}
