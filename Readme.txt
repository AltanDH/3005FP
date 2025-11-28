Name (SID): Altan Dogan Hoca (101311866)
Name (SID): Teddy Al Houwayek (101316419)

-- Summary --
This is a health and fitness management system with a relational database (postgreSQL) to manage the daily activities of a modern fitness center with persistent storage in Java. 
It offers individual and group training classes, with appropriate scheduling, capacity management, and trainer coordination.
There are 3 different types of users: Members, trainers, and administrative staff, each with specialized access priviledges and functional responsibilities.

Members:
Members can access and their training history, fitness goals with their respective progress, and joining/leaving personal or group training sessions.

Trainers:
Trainers can define their work schedules, availability periods as either weekly recurring timeslots or individual time intervals, and update/view these as needed ahead of time. 
The system prevents overlapping of timeslots for the same trainer to ensure correctness.
Trainers have controlled access to member profiles to view relevant information such as health metrics, progress towards goals, and class attendance.

Administrators:
Administrators handle the operational backbone of the facility by managing room bookings, ensuring studios and training spaces are allocated without scheduling conflicts. 
They oversee equipment maintenance by tracking machine status, logging maintenance issues, assigning repair tasks, and updating maintenance records once issues are resolved. 
Admins also manage class scheduling, including creating classes, assigning trainers, setting capacities, and updating or canceling sessions. 
They maintain the billing system by generating invoices for memberships, training sessions, and class enrollments, recording payment details, and tracking financial status.

-- Application Operations --
Member Functions: (5)
- User Registration: Create a new member with unique email and basic profile info.
- Profile Management: Update personal details, fitness goals (e.g., weight target), and input new health metrics (e.g., weight, heart rate).
- Dashboard: Show latest health stats, active goals, past class count, upcoming sessions.
- Group Class Registration: Register for scheduled classes if capacity permits.
- Health History: Log multiple metric entries; do not overwrite. Must support time-stamped entries.

Trainer Functions: (3)
- Set Availability: Define time windows when available for sessions or classes. Prevent overlap.
- Schedule View: See assigned PT sessions and classes.
- Member Lookup: Search by name (case-insensitive) and view current goal and last metric. No editing rights.

Administrative Staff Functions: (2)
- Room Booking: Assign rooms for sessions or classes. Prevent double-booking.
- Class Management: Define new classes, assign trainers/rooms/time, update schedules.


-- Files -- 
ER Model:



Video Link: 

