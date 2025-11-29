import java.sql.*;
import java.util.Scanner;

public class DatabaseHandler {
    // Get the required data to connect to the database
    // localhost:5432 is the default port Postgres listens on
    private static final String DATABASE_NAME = "3005FP"; // Your DB name (edit here)
    private static final String USER = "postgres"; // Username (edit here)
    private static final String PASSWORD = "Tupras99"; // User's password (edit here)
    private static final String URL = "jdbc:postgresql://localhost:5432/" + DATABASE_NAME; // (Do not change)

    // Getters
    public static String getName() { return DATABASE_NAME; }
    public static String getUser() { return USER; }
    public static String getPassword() { return PASSWORD; }
    public static String getURL() { return URL; }


    // ---------- CRUD OPERATION FUNCTIONS ----------
    // Retrieves and displays all tuples in a table
    public static String getAll(Connection connection, String table) {
        System.out.println("\nGetting all data from: " + table);

        // Basic query, gets all tuples in the database
        String query = "SELECT * FROM " + table;
        String out = "";

        try {
            // Grab the result of a query
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(query);

            // Get the metadata and column count of a passed in query
            ResultSetMetaData metaData = result.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Print column names
            for (int i = 1; i <= columnCount; i++) {
                out += metaData.getColumnName(i);
                if (i < columnCount) { out += " | "; }
            }
            //System.out.println();
            out += "\n";

            // Print each tuple
            while (result.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    out += result.getString(i);
                    if (i < columnCount) { out += " | "; }
                }
                out += "\n";
            }
            result.close();
            statement.close();

        } catch (SQLException sqlException) {
            System.out.println("\nError! Invalid query entered");
        }

        return out;
    }

    // Adds a new tuple to the table
    public static void addTuple(Connection connection, String table, String[] values) throws SQLException {
        // Joins the query with the table and fields of the tuple to create a syntactically valid query
        String[] colNames = getModifiableColumnNames(connection, table);
        String query = "INSERT INTO " + table + " (" + String.join(", ", colNames) + ") VALUES (";
        for (int i = 0; i < colNames.length - 1; i++) {
            query += "?, ";
        }
        query += "?)";

        // Update the data
        try  {
            // Create a PreparedStatement and store it to set values of "?"
            PreparedStatement statement = connection.prepareStatement(query);
            // Number specifies the question mark which gets updated from left to right
            for (int i = 0; i < colNames.length; i++) {
                statement.setString(i + 1, values[i]);
            }

            // Execute the query
            statement.executeUpdate();
            System.out.println("\nAdded successfully");

        } catch (SQLException sqlException) {
            System.out.println("\nError! Invalid Query Entered");
        }
        catch (Exception exception) {
            System.out.println(query);
            System.out.println("\nError! Invalid data entered");
        }
    }

    // Updates a tuple from a table
    // newTuple is an array consisting of all non auto increment columns of a tuple
    public static void updateTuple(Connection connection, String table, String[] newTuple, String[] primaryKeyValues) throws SQLException {
        try {
            String[] modifiableColumnNames = getModifiableColumnNames(connection, table);

            // Set
            String set = "";
            // Iterate over every non id column
            for (int i = 0; i < modifiableColumnNames.length; i++) {
                if (i != 0) { set += ", "; }
                set += modifiableColumnNames[i] + " = ?";
            }

            // Where
            String where = "";
            String[] pkColNames = getPrimaryKeyColumns(connection, table);
            // Iterate over every modifiable column
            for (int i = 0; i < pkColNames.length; i++) {
                if (i > 0) { where += " AND ";}
                where += pkColNames[i] + " = ?";
            }

            String query = "UPDATE " + table + " SET " + set + " WHERE " + where;

            // Create a PreparedStatement and store it to set values of "?"
            PreparedStatement statement = connection.prepareStatement(query);

            // Set
            // Fill in the ?
            int i;
            for (i = 0; i < newTuple.length; i++) {
                statement.setString(i + 1, newTuple[i]);
            }

            // Where
            // Fill in the ?
            for (int j = 0; j < primaryKeyValues.length; j++) {
                statement.setString(j + i + 1, primaryKeyValues[j]);
            }

            // Execute the query
            int rowsAltered = statement.executeUpdate();
            if (rowsAltered > 0) {
                System.out.println("\nUpdate successful");
            } else {
                System.out.println("\nNo data was updated");
            }
        } catch (SQLException e) {
            System.out.println("\nError! Duplicate data entered");
        }
        catch (Exception exception) {
            System.out.println("\nError! Invalid data entered");
            exception.printStackTrace();
        }
    }

    // Deletes a tuple by taking in a table and the primary key values
    public static void deleteTuple(Connection connection, String table, String[] primaryKeyValues) {
        // Create a PreparedStatement and store it to set values of "?"
        try {
            // Where
            String where = "";
            String[] pkColNames = getPrimaryKeyColumns(connection, table);
            // Iterate over every modifiable column
            for (int i = 0; i < pkColNames.length; i++) {
                if (i > 0) { where += " AND ";}
                where += pkColNames[i] + " = ?";
            }

            String query = "DELETE FROM " + table + " WHERE " + where ;
            PreparedStatement statement = connection.prepareStatement(query);

            // Where
            // Fill in the ?
            for (int i = 0; i < primaryKeyValues.length; i++) {
                statement.setString(i + 1, primaryKeyValues[i]);
            }

            // Execute the query
            int rowsAltered = statement.executeUpdate();
            if (rowsAltered <= 0) { // If no rows were changed then no data was updated
                System.out.println("\nTuple does not exist, no data was altered");
            } else { // Otherwise data was updated
                System.out.println("\nTuple removed successfully");
            }

        } catch (Exception exception) {
            System.out.println("\nError! Invalid data entered");
            exception.printStackTrace();
        }
    }

    // ---------- HELPER FUNCTIONS ----------
    // Get the names of each column in a table dynamically
    public static String[] getColumnNames(Connection connection, String table) throws SQLException {
        // Try not to spend time returning columns
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery("SELECT * FROM " + table + " LIMIT 0"); // Only need metadata
        ResultSetMetaData metaData = result.getMetaData();

        // Get the number of columns
        int columnCount = metaData.getColumnCount();
        String[] columnNames = new String[columnCount];

        // Loop through columns and get the name of each
        for (int i = 1; i <= columnCount; i++) {
            columnNames[i - 1] = metaData.getColumnName(i); // ResultSetMetaData is 1-based
        }

        result.close();
        statement.close();

        return columnNames;
    }

    // Since some columns are not modifiable like fields defined as AUTO INCREMENT, do not return those
    public static String[] getModifiableColumnNames(Connection connection, String table) throws SQLException {
        // Create a statement, provide a fast dummy query, and get the metadata
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery("SELECT * FROM " + table + " LIMIT 0"); // Only need metadata
        ResultSetMetaData metaData = result.getMetaData();

        // Get the number of columns
        int columnCount = metaData.getColumnCount();

        // Count number of modifiable columns
        int modifiableCount = 0;
        for (int i = 1; i <= columnCount; i++) {
            if (!metaData.isAutoIncrement(i)) {
                modifiableCount++;
            }
        }

        String[] columnNames = new String[modifiableCount];

        // Add every column that is not auto increment
        int backCounter = 0;
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            if (!metaData.isAutoIncrement(i)) { // Skip auto-increment columns
                columnNames[i - 1 - backCounter] = metaData.getColumnName(i);
            } else {
                backCounter++;
            }
        }

        result.close();
        statement.close();

        return columnNames;
    }

    // Gets the primary key columns of a table
    public static String[] getPrimaryKeyColumns(Connection connection, String table) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet result = metaData.getPrimaryKeys(null, null, table);

        // Count the number of primary keys
        int count = 0;
        while (result.next()) {
            count++;
        }
        result.close();
        result = metaData.getPrimaryKeys(null, null, table);

        // Create the array to be filled with pk columns
        String[] pkCols = new String[count];

        // Fill the array
        int i = 0;
        while (result.next()) {
            pkCols[i] = result.getString("COLUMN_NAME");
            i++;
        }
        result.close();

        return pkCols;
    }

}
