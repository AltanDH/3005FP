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