-- Populate Trainers Table
INSERT INTO Trainers (email, first_name, last_name, password) VALUES
('alex.trainer@fitclub.com', 'Alex', 'Moreno', 'pass1'),
('jamie.trainer@fitclub.com', 'Jamie', 'Chen', 'pass2'),
('riley.trainer@fitclub.com', 'Riley', 'Patel', 'pass3');

-- Populate Members Table
INSERT INTO Members (email, first_name, last_name, password, birth_date, gender, phone_number) VALUES
('john.smith@gmail.com', 'John', 'Smith', 'js123', '1995-04-10', 'M', '343-111-2222'),
('maria.lopez@gmail.com', 'Maria', 'Lopez', 'ml123', '1988-12-20', 'F', '343-333-4444'),
('david.tan@gmail.com', 'David', 'Tan', 'dt123', '2005-07-03', 'M', '343-555-6666');

-- Populate Administrative Staff Table
INSERT INTO AdminStaff (email, first_name, last_name, password) VALUES
('sarah.admin@fitclub.com', 'Sarah', 'Olsen', 'admin1'),
('mark.admin@fitclub.com', 'Mark', 'Benson', 'admin2');

-- Populate Rooms Table
INSERT INTO Rooms (room_id, capacity, type) VALUES
(101, 25, 'Studio'),
(102, 40, 'Studio'),
(201, 15, 'Training Room'),
(202, 8, 'Training Room');

-- Populate Equipment Table
INSERT INTO Equipment (room_id, type, status, issue_type, issue_desc) VALUES
(101, 'Treadmill', 'operational', 'none', 'No issues reported'),
(101, 'Rowing Machine', 'maintenance', 'belt', 'Belt slipping during use'),
(102, 'Stationary Bike', 'operational', 'none', 'No issues reported'),
(201, 'Bench Press', 'maintenance', 'structural', 'Loose bolt causing instability');

-- Populate Fitness Goals Table 
INSERT INTO FitnessGoals (email, type, value) VALUES
('john.smith@gmail.com', 'weight', 170),
('maria.lopez@gmail.com', 'bodyfat', 22),
('david.tan@gmail.com', 'weight', 150);

-- Populate Health Metrics Table
INSERT INTO HealthMetrics (email, weight, height, heart_rate, body_fat_pct, created_at) VALUES
('john.smith@gmail.com', 185, 178, 75, 24, NOW() - INTERVAL '14 days'),
('john.smith@gmail.com', 182, 178, 72, 23, NOW() - INTERVAL '7 days'),
('maria.lopez@gmail.com', 150, 165, 80, 26, NOW() - INTERVAL '10 days'),
('maria.lopez@gmail.com', 148, 165, 78, 25, NOW()),
('david.tan@gmail.com', 160, 172, 70, 20, NOW() - INTERVAL '5 days');

-- Populate Group Fitness Classes Table 
INSERT INTO GroupFitnessClasses (room_id, type, capacity, duration, start_time, recurring_weekly) VALUES
(101, 'Yoga', 20, 60, '09:00', TRUE),
(102, 'Cardio', 30, 45, '18:00', TRUE),
(101, 'Pilates', 25, 50, '12:00', FALSE);

-- Populate Trainer Availability Periods Table
INSERT INTO AvailabilityPeriods (email, day, start_time, end_time, recurring_weekly) VALUES
('alex.trainer@fitclub.com', 'Monday', '08:00', '12:00', TRUE),
('alex.trainer@fitclub.com', 'Wednesday', '14:00', '18:00', TRUE),
('jamie.trainer@fitclub.com', 'Tuesday', '10:00', '16:00', TRUE),
('riley.trainer@fitclub.com', 'Friday', '09:00', '17:00', TRUE);

-- Populate 'Member Participates in Class' relationship Table
INSERT INTO Participates (email, class_id) VALUES
('john.smith@gmail.com', 1),
('maria.lopez@gmail.com', 1),
('david.tan@gmail.com', 2),
('john.smith@gmail.com', 3);

-- Populate 'Trainer Teaches Class' relationship Table
INSERT INTO Teaches (email, class_id) VALUES
('alex.trainer@fitclub.com', 1),  -- Yoga
('jamie.trainer@fitclub.com', 2), -- Cardio
('riley.trainer@fitclub.com', 3); -- Pilates

-- Populate 'Admin Reports Equipment' relationship Table
INSERT INTO Reports (email, equipment_id, operational_status, issue_type) VALUES
('sarah.admin@fitclub.com', 2, 'needs repair', 'belt'),
('mark.admin@fitclub.com', 4, 'needs inspection', 'structural');
