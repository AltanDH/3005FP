-- Populate Trainers Table
INSERT INTO Trainers (email, first_name, last_name, password) VALUES
('alex.trainer@fitclub.com', 'Alex', 'Moreno', 'pass1'),
('jamie.trainer@fitclub.com', 'Jamie', 'Chen', 'pass2'),
('riley.trainer@fitclub.com', 'Riley', 'Patel', 'pass3');

-- Populate Members Table
INSERT INTO Members (email, first_name, last_name, password, birth_date, gender, phone_number) VALUES
('john.smith@gmail.com', 'John', 'Smith', 'js123', '1995-04-10', 'M', '343-111-2222'),
('maria.lopez@gmail.com', 'Maria', 'Lopez', 'ml123', '1988-12-20', 'F', '343-333-4444'),
('david.tan@gmail.com', 'David', 'Tan', 'dt123', '2005-07-03', 'M', '343-555-6666'),
('emma.jones@gmail.com', 'Emma', 'Jones', 'ej123', '1999-09-15', 'F', '343-777-8888'),
('li.wang@gmail.com', 'Li', 'Wang', 'lw123', '1990-03-22', 'M', '343-999-1111'),
('sophia.martin@gmail.com', 'Sophia', 'Martin', 'sm123', '1985-01-17', 'F', '343-222-3333');

-- Populate Administrative Staff Table
INSERT INTO AdminStaff (email, first_name, last_name, password) VALUES
('sarah.admin@fitclub.com', 'Sarah', 'Olsen', 'admin1'),
('mark.admin@fitclub.com', 'Mark', 'Benson', 'admin2'),
('julia.admin@fitclub.com', 'Julia', 'Park', 'admin3');

-- Populate Rooms Table
INSERT INTO Rooms (room_id, capacity, type) VALUES
(101, 25, 'Studio'),
(102, 40, 'Studio'),
(201, 1, 'Training Room'),
(202, 8, 'Training Room'),
(301, 1, 'Studio');

-- Populate Equipment Table
INSERT INTO Equipment (room_id, type, status, issue_type, issue_desc) VALUES
(101, 'Treadmill', 'operational', 'none', 'No issues reported'),
(101, 'Rowing Machine', 'maintenance', 'belt', 'Belt slipping during use'),
(102, 'Stationary Bike', 'operational', 'none', 'No issues reported'),
(201, 'Bench Press', 'maintenance', 'structural', 'Loose bolt causing instability'),
(102, 'Medicine Ball Set', 'maintenance', 'tear', 'Surface tear on 10lb ball'),
(301, 'Rowing Machine', 'maintenance', 'chain', 'Chain needs lubrication'),
(301, 'Squat Rack', 'operational', 'none', 'Sturdy and ready');

-- Populate Fitness Goals Table 
INSERT INTO FitnessGoals (email, type, value) VALUES
('john.smith@gmail.com', 'weight', 170),
('maria.lopez@gmail.com', 'bodyfat', 22),
('emma.jones@gmail.com', 'weight', 130),
('emma.jones@gmail.com', 'bodyfat', 20),
('li.wang@gmail.com', 'strength', 200),
('sophia.martin@gmail.com', 'weight', 145),
('sophia.martin@gmail.com', 'bodyfat', 24);

-- Populate Health Metrics Table
INSERT INTO HealthMetrics (email, weight, height, heart_rate, body_fat_pct, created_at) VALUES
('john.smith@gmail.com', 185, 178, 75, 24, NOW() - INTERVAL '14 days'),
('john.smith@gmail.com', 182, 178, 72, 23, NOW() - INTERVAL '7 days'),
('maria.lopez@gmail.com', 150, 165, 80, 26, NOW() - INTERVAL '10 days'),
('maria.lopez@gmail.com', 148, 165, 78, 25, NOW()),
('david.tan@gmail.com', 160, 172, 70, 20, NOW() - INTERVAL '5 days'),
('emma.jones@gmail.com', 135, 162, 82, 23, NOW() - INTERVAL '3 days'),
('li.wang@gmail.com', 170, 175, 68, 21, NOW() - INTERVAL '2 days'),
('sophia.martin@gmail.com', 150, 168, 79, 26, NOW() - INTERVAL '1 day'),
('emma.jones@gmail.com', 134, 162, 78, 22, NOW()),
('li.wang@gmail.com', 168, 175, 70, 20, NOW());

-- Populate Group Fitness Classes Table 
INSERT INTO GroupFitnessClasses (room_id, type, capacity, duration, start_time, recurring_weekly) VALUES
(101, 'Yoga', 20, 60, '09:00', TRUE),
(102, 'Cardio', 30, 45, '18:00', TRUE),
(101, 'Pilates', 25, 50, '12:00', FALSE),
(201, 'Calisthenics', 1, 30, '15:00', FALSE);

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
('riley.trainer@fitclub.com', 3),  -- Pilates
('jamie.trainer@fitclub.com', 4);   -- Calisthenics

-- Populate 'Admin Reports Equipment' relationship Table
INSERT INTO Reports (email, equipment_id, operational_status, issue_type) VALUES
('sarah.admin@fitclub.com', 2, 'needs repair', 'belt'),
('mark.admin@fitclub.com', 4, 'needs inspection', 'structural'),
('julia.admin@fitclub.com', 6, 'needs lubrication', 'chain'),
('julia.admin@fitclub.com', 5, 'needs repair', 'tear');