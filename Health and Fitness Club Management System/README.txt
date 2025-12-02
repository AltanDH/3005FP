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
- Database exists (Mine is called: 3005FP), and is defined by you in DatabaseHandler.java file
- PostgreSQL username and password is defined by you in DatabaseHandler.java file

Steps to Run:
1. Create a database in Postgres using pgAdmin4.
2. Import the provided "sql/DDL.sql" file into the created Database, then Execute it to make the DB tables.
3. Import the provided "sql/DML.sql" file into the created Database, then Execute it to populate the DB tables with starting data.
4. Create a project in IntelliJ Community Edition by selecting "Maven Archetype" in the Generators section on the left hand side (otherwise you're on your own), then make sure to add the archetype ending with "quickstart" from the dropdown list, then click Create.
5. Delete the "src/test" folder and the contents of "src/main/java/*" that are automatically generated.
6. Place the ".java" files that can be found in the submission's "app/" folder into the "src/main/java/" folder of the project, and click "Refactor" when prompted.
7. Go into DatabaseHandler.java and at the top of the file, set the 'DATABASE_NAME' to your previously created pgAdmin4 database name, as well as the "USER" and "PASSWORD" variables to your pgAdmin4 'username' and 'password'.
8. Drag the submission's "app/pom.xml" file into the IntelliJ project's root folder, click Refactor, and allow it to overwrite all files as prompted
9. Reload the IntelliJ project (Closing and re-opening will suffice)
10. Run the HFCMSApp.java file

Video Link: 
- https://youtu.be/Tme6_dTv1I8

Assumptions:
- It's possible for group fitness classes to have multiple trainers teaching it
- A member can have multiple fitness goals
- Users log into their accounts using their email and password
- All Rooms exist within the same building (and thus have unique identifiers)
- Each Admin Staff member only makes 1 report per equipment, otherwise it gets overwritten
- Trainers can only look up members they are training

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

DDL.sql:
- Defines the Relational Databse Schema, creating all the Tables if they don't already exist.

ERD.pdf:
1NF - Satisfied because all the attributes store single atomic values. 
2NF - Satisfied because all "primary key -> nonprime attribute" relations are fully functionally dependent. Additionally, in the case of a table  having a composite primary key, there are no nonprime attributes that depend on only part of that key, thus we avoid having partial dependency. Note that this is possible for our Reports relationship table as long as we respect its corresponding assumption mentioned in the "Assumptions" section at the top of the README.
3NF - Satisfied because no nonprime attribute depends on another nonprime attribute; all nonprime attributes depend directly on the primary key. In other words, there is no relation such that X -> Z and Z -> Y hold, where Z is a nonprime attribute (no transitive relationships).

DML.sql:
- Populates the Tables with starting data.

pom.xml:
- File which uses Maven to help setup JDBC
- Specifies project dependencies which Maven automatically downloads











































