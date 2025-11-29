## Health and Fitness Club Management System ##

-- Introduction --
Group Members: 
Altan Dogan Hoca, 101311866
Teddy Al Houwayek, 101316419

Documentation:
- Postgres documentation for JDBC: https://jdbc.postgresql.org/documentation/setup/
- Maven: https://mvnrepository.com/artifact/org.postgresql/postgresql/42.7.7

Prerequisites:
- Java 17 installed (will likely work with higher versions assuming backwards compatibility)
- Maven installed
- PostgreSQL
- Database exists (Mine is called: 3005FP), and is defined by you in SimpleDatabaseHandler.Java file
- PostgreSQL username and password is defined by you in SimpleDatabaseHandler.Java file

Steps to Run:
1. Create a database in Postgres using pgAdmin4
2. Run the database with the dml.sql file provided in pgAdmin4
3. Create a project in IntelliJ Community Edition with Maven selected (otherwise you're on your own)
4. Go into SimpleDatabaseHandler.Java and set your database name, username, and password (pgAdmin4 data)
5. Place the pom.xml file in the root
6. Run the SimpleDatabaseHandler.Java file

Video Link: 
- 

Assumptions:
- It's possible for group fitness classes to have multiple trainers teaching it
- A member can have multiple fitness goals
- Users log into their accounts using their email and password

-- Summary --
This is a health and fitness management system with a relational database (postgreSQL) to manage the daily activities of a modern fitness center with persistent storage in Java. It offers individual and group training classes, with appropriate scheduling, capacity management, and trainer coordination. There are 3 different types of users: Members, trainers, and administrative staff, each with specialized access priviledges and functional responsibilities.

Members:
Members can access and their training history, fitness goals with their respective progress, and joining/leaving group training sessions.

Trainers:
Trainers can define their work schedules, availability periods as either weekly recurring timeslots or individual time intervals, and update/view these as needed ahead of time. The system prevents overlapping of timeslots for the same trainer to ensure correctness. Trainers have controlled access to member profiles to view relevant information such as health metrics, progress towards goals, and class attendance.

Administrators:
Administrators handle the operational backbone of the facility by managing room bookings, ensuring studios and training spaces are allocated without scheduling conflicts. They oversee equipment maintenance by tracking machine status, logging maintenance issues, assigning repair tasks, and updating maintenance records once issues are resolved. Admins also manage class scheduling, including creating classes, assigning trainers, setting capacities, and updating or canceling sessions. They maintain the billing system by generating invoices for memberships, training sessions, and class enrollments, recording payment details, and tracking financial status.


-- Application Operations --
Member Functions: (5)
- User Registration: Create a new member with unique email and basic profile info.
- Profile Management: Update personal details, fitness goals (e.g., weight target), and input new health metrics (e.g., weight, heart rate).
- Group Class Registration: Register for scheduled classes if capacity permits.
- Health History: Log multiple metric entries; do not overwrite. Must support time-stamped entries.

Trainer Functions: (3)
- Set Availability: Define time windows when available for sessions or classes. Prevent overlap.
- Schedule View: See assigned PT sessions and classes.
- Member Lookup: Search by name (case-insensitive) and view current goal and last metric. No editing rights.

Administrative Staff Functions: (2)
- Room Booking: Assign rooms for sessions or classes. Prevent double-booking.
- Class Management: Define new classes, assign trainers/rooms/time, update schedules.
- Equipment Maintenance: Log issues, track repair status, associate with room/equipment.


-- Files -- 
ER Model:
- Uses UML notation
- As per the notation in the notes, an arrow indicates a cardinality of 1, otherwise cardinality is N
- It is normalized to 1NF because there are no multivalued attributes or composite attributes in the database
- It is normalized to 2NF because all primary key -> non-key attribute relations are fully functionally dependent
- It is normalized to 3NF because there is no relation such that X -> Y, Y -> Z (no transitive relationships)

DDL.sql:
- Defines all the Database Tables if they don't already exist.

DML.sql:
- Populates the Tables with starting data.

pom.xml:
- File which uses Maven to help setup JDBC
- Specifies project dependencies which Maven automatically downloads





















