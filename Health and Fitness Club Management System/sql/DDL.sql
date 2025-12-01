-- Trainers Table
CREATE TABLE IF NOT EXISTS Trainers (
	email VARCHAR(255) PRIMARY KEY,
	first_name VARCHAR(255) NOT NULL,
	last_name VARCHAR(255) NOT NULL,
	password VARCHAR(255) NOT NULL
);

-- Members Table
CREATE TABLE IF NOT EXISTS Members (
	email VARCHAR(255) PRIMARY KEY,
	first_name VARCHAR(255) NOT NULL,
	last_name VARCHAR(255) NOT NULL,
	password VARCHAR(255) NOT NULL,
	birth_date DATE NOT NULL,
	gender VARCHAR(1) NOT NULL,
	phone_number VARCHAR(20) NOT NULL
);

-- Administrative Staff Table
CREATE TABLE IF NOT EXISTS AdminStaff (
	email VARCHAR(255) PRIMARY KEY,
	first_name VARCHAR(255) NOT NULL,
	last_name VARCHAR(255) NOT NULL,
	password VARCHAR(255) NOT NULL
);

-- Rooms Table
CREATE TABLE IF NOT EXISTS Rooms (
	room_id INTEGER PRIMARY KEY,
	capacity INTEGER NOT NULL,
	type VARCHAR(20) NOT NULL
);

-- Equipment Table
CREATE TABLE IF NOT EXISTS Equipment (
	equipment_id SERIAL PRIMARY KEY,
	room_id INTEGER REFERENCES Rooms(room_id),
	type VARCHAR(255) NOT NULL,
	status VARCHAR(255) NOT NULL,
	issue_type VARCHAR(255) NOT NULL,
	issue_desc TEXT NOT NULL
);

-- Fitness Goals Table
CREATE TABLE IF NOT EXISTS FitnessGoals (
	goal_id SERIAL PRIMARY KEY,
	email VARCHAR(255) REFERENCES Members(email),
	type VARCHAR(20) NOT NULL,
	value INTEGER NOT NULL
);

-- Health Metrics Table
CREATE TABLE IF NOT EXISTS HealthMetrics (
	metric_id SERIAL PRIMARY KEY,
	email VARCHAR(255) REFERENCES Members(email),
	weight INTEGER NOT NULL,
	height INTEGER NOT NULL,
	heart_rate INTEGER NOT NULL,
	body_fat_pct INTEGER NOT NULL,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Group Fitness Classes Table
CREATE TABLE IF NOT EXISTS GroupFitnessClasses (
	class_id SERIAL PRIMARY KEY,
	room_id INTEGER REFERENCES Rooms(room_id),
	type VARCHAR(255) NOT NULL,
	capacity INTEGER NOT NULL,
	duration INTEGER NOT NULL,
	start_time TIME NOT NULL,
	recurring_weekly BOOLEAN NOT NULL
);

-- Trainer Availability Periods
CREATE TABLE IF NOT EXISTS AvailabilityPeriods (
	email VARCHAR(255) REFERENCES Trainers(email),
	day VARCHAR(9) NOT NULL,
	start_time TIME NOT NULL,
	end_time TIME NOT NULL,
	recurring_weekly BOOLEAN NOT NULL,
	PRIMARY KEY (email, day, start_time)
);

-- 'Member Participates in Class' relationship Table
CREATE TABLE IF NOT EXISTS Participates (
	email VARCHAR(255) REFERENCES Members(email),
	class_id INTEGER REFERENCES GroupFitnessClasses(class_id),
	PRIMARY KEY (email, class_id)
);

-- 'Trainer Teaches Class' relationship Table
CREATE TABLE IF NOT EXISTS Teaches (
	email VARCHAR(255) REFERENCES Trainers(email),
	class_id INTEGER REFERENCES GroupFitnessClasses(class_id),
	PRIMARY KEY (email, class_id)
);

-- 'Admin Reports Equipment' relationship Table
CREATE TABLE IF NOT EXISTS Reports (
	email VARCHAR(255) REFERENCES AdminStaff(email),
	equipment_id INTEGER REFERENCES Equipment(equipment_id),
	operational_status VARCHAR(255) NOT NULL,
	issue_type TEXT NOT NULL,
	PRIMARY KEY (email, equipment_id)
);

-- Index to speed up health metric lookups by member email (useful for Health History View)
CREATE INDEX IF NOT EXISTS health_metric_email_index 
ON HealthMetrics (email);

-- View that shows the members each trainer teaches along with their latest goal and health details
CREATE OR REPLACE VIEW TrainerMembersLookup AS
WITH latest_metrics AS (
    SELECT DISTINCT ON (email)
        metric_id, email, weight, height, heart_rate, body_fat_pct, created_at
    FROM HealthMetrics
    ORDER BY email, created_at DESC
),
latest_goals AS (
    SELECT DISTINCT ON (email, type)
        goal_id, email, type, value
    FROM FitnessGoals
    ORDER BY email, type, goal_id DESC
)
SELECT 
    t.email AS trainer_email,
    m.first_name AS member_first_name,
	m.last_name AS member_last_name,
    h.weight AS latest_weight,
    h.height AS latest_height,
    h.heart_rate AS latest_heart_rate,
    h.body_fat_pct AS latest_body_fat_pct,
    g.type AS goal_type,
    g.value AS goal_value
FROM Trainers t
JOIN Teaches teach ON t.email = teach.email
JOIN Participates p ON p.class_id = teach.class_id
JOIN Members m ON m.email = p.email
LEFT JOIN latest_metrics h ON h.email = m.email
LEFT JOIN latest_goals g ON g.email = m.email;

-- Trigger function def to prevent trainer periods overlap in time
CREATE OR REPLACE FUNCTION prevent_trainer_periods_overlap()
RETURNS TRIGGER 
LANGUAGE plpgsql
AS 
$$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM AvailabilityPeriods p
        WHERE p.email = NEW.email
          AND p.day = NEW.day
          AND NEW.start_time < p.end_time
		  AND NEW.end_time > p.start_time
		  OR ((p.start_time = NEW.start_time OR p.end_time = NEW.end_time) AND p.day = NEW.day AND p.email = NEW.email)
    ) THEN
        RAISE EXCEPTION 'Trainer % already has an overlapping availability on % (% - %).',
            NEW.email, NEW.day, NEW.start_time, NEW.end_time;
    END IF;
    RETURN NEW;
END;
$$;

-- Trigger fires before INSERT or UPDATE on AvailabilityPeriods
DROP TRIGGER IF EXISTS trigger_overlap ON AvailabilityPeriods;
CREATE TRIGGER trigger_overlap
BEFORE INSERT OR UPDATE ON AvailabilityPeriods
FOR EACH ROW
EXECUTE FUNCTION prevent_trainer_periods_overlap();