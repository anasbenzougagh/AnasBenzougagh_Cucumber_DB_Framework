package com.Junit;

import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.*;

public class SetupAndDQL {

    //Connection required String:
    String dbURL = "jdbc:sqlite:src/test/resources/northwind.db";
    String dbUsername = "";
    String dbPassword = "";

    @Test
    public void introJDBC() throws SQLException {
        //Get connection needs SQLException to use getConnection
        /** Main interfaces:
         1-Connection: use DriverManager getConnection method to establish connection passing URL, Username and password args.
         2-Statement: once connected, use connection to a created statement (then set limitation to the statement).
         3-ResultSet: once execute query method is initiated, you can pass a query then store in a resultSet variable.
         */
        //Creating connection
        Connection connection = DriverManager.getConnection(dbURL, dbUsername, dbPassword);

        //To execute queries
        Statement statement = connection.createStatement();

        //resultSet holds statement value after execute
        ResultSet resultSet = statement.executeQuery("select * from EMPLOYEES");

        //extracting data from table while there is a row
        while (resultSet.next()) {
            System.out.println("----------------------------------------------------------");
            System.out.println(
                    //return will match getValue
                    resultSet.getString("TitleOfCourtesy")
                            + " " +
                            resultSet.getString("FirstName")
                            + " " +
                            resultSet.getString("LastName")
                            + "\n" +
                            resultSet.getString("Country")
                            + " [" +
                            resultSet.getString(4)
                            + "] Extension: " +
                            resultSet.getInt("Extension")
            );
        }
        connection.close();
        statement.close();
        resultSet.close();
    }

    @Test
    public void basicNavigation() throws SQLException {
        Connection connection = DriverManager.getConnection(dbURL, dbUsername, dbPassword);

        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery("SELECT LastName, FirstName FROM EMPLOYEES");

        //rs.next(); ==> used to move to the first row after metadata.
        resultSet.next();
        System.out.println("First Row");
        System.out.println(resultSet.getString("FirstName") + " - " + resultSet.getString(2));

        //Get second row ----
        resultSet.next();
        System.out.println("Second Row");
        System.out.println(resultSet.getString("FirstName") + " - " + resultSet.getString(2));

        //Get the last row ---
        //resultSet.last();
        //System.out.println("Last Row");
        //System.out.println(resultSet.getString("FirstName") + " - " + resultSet.getString(2));

        //Get row number (works with non TYPE_FORWARD_ONLY Databases)
        int rowNumber = resultSet.getRow();
        System.out.println("Row Number");
        System.out.println(rowNumber);

        //Get row by number (works with non TYPE_FORWARD_ONLY Databases)
        //System.out.println("Absolute");
        //resultSet.absolute(5);
        //System.out.println(resultSet.getString("FirstName") + " - " + resultSet.getString(2));

        //Get previous row
        //System.out.println("Previous");
        //resultSet.previous();
        //System.out.println(resultSet.getRow());
        //System.out.println(resultSet.getString("FirstName") + " - " + resultSet.getString(2));

        //To print the whole table
        System.out.println("Loop");
        //resultSet.absolute(0);
        //resultSet.beforeFirst();
        while (resultSet.next()) {
            System.out.println(resultSet.getString("FirstName") + " - " + resultSet.getString(2));
        }
        connection.close();
        statement.close();
        resultSet.close();
    }

    @Test
    public void metaData() throws SQLException {
        Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
        Statement statement = conn.createStatement(
                //ResultSet.TYPE_SCROLL_INSENSITIVE,
                //ResultSet.CONCUR_READ_ONLY
        );
        ResultSet rs = statement.
                executeQuery("SELECT FirstName,LastName,Title FROM employees Limit 6");

        //Getting database metadata
        System.out.println("Getting database metadata");
        DatabaseMetaData dbMetaData = conn.getMetaData();
        System.out.println("-----------------------------------------");
        System.out.println("dbMetaData = " + dbMetaData);
        System.out.println(dbMetaData.getUserName());
        System.out.println(dbMetaData.getDatabaseProductName());
        System.out.println(dbMetaData.getDatabaseProductVersion());
        System.out.println(dbMetaData.getDriverName());
        System.out.println("-----------------------------------------");

        //Getting resultSetMetadata
        System.out.println("Getting resultSetMetadata");
        ResultSetMetaData rsmd = rs.getMetaData();
        System.out.println("-----------------------------------------");

        //Get number of columns
        System.out.println(".getColumnCount");
        int columnCount = rsmd.getColumnCount();
        System.out.println(columnCount);
        System.out.println("-----------------------------------------");

        //Get column name
        System.out.println(".getColumnName");
        System.out.println(rsmd.getColumnName(2));
        System.out.println("-----------------------------------------");

        //Get all column names
        System.out.println("Print all columns");
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            System.out.print(rsmd.getColumnName(i) + " ");
        }
        System.out.println("-----------------------------------------");


        //ResultSet                               ==> holds table data
        //    rs.next()                           --> iterate each row dynamically (with while loop)
        //    rs.getString(columnIndex)           --> returns data itself
        //    rs.getString(columnName)            --> returns data itself

        //ResultSetMetaData                       ==> holds table information (ColumnNames - Column Count)
        //    rsmd.getColumnCount()               --> returns number of columns
        //    rsmd.getColumnName(index)           --> returns column name point at by index


        System.out.println("Print ALL Data Dynamically");
        //Write a code, that prints whole table information for every query
        //Column Name - Column Value
        //FirstName - Margaret
        //LastName - Peacock
        //Title - Sales Representative

        while (rs.next()) {
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                System.out.println(rsmd.getColumnName(i) + " - " + rs.getString(i));
            }
            System.out.println("______________________________________________");
        }

        conn.close();
        statement.close();
        rs.close();

    }

    @Test
    public void storingDBDataInListOfMaps() throws SQLException {
        Connection connection = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
        Statement statement = connection.createStatement(
                //ResultSet.TYPE_SCROLL_INSENSITIVE,
                //ResultSet.CONCUR_READ_ONLY
        );

        ResultSet rs = statement
                .executeQuery("SELECT FirstName,LastName,Title FROM employees Limit 6");

        //Storing data dynamically
        ResultSetMetaData rsmd = rs.getMetaData();
        List<Map<String, Object>> dataList = new ArrayList<>();

        //Number of column numbers
        int columnCount = rsmd.getColumnCount();

        //iterating through each row
        while (rs.next()) {
            Map<String, Object> rowMap = new HashMap<>();
            //iterate each column dynamically to fill the map
            for (int i = 1; i <= columnCount; i++) {
                //key = column name , value = column value
                rowMap.put(rsmd.getColumnName(i), rs.getString(i));
            }
            //once Maps holds values then save in dataList
            dataList.add(rowMap);
        }
        //System.out.println(dataList);

        for (Map<String, Object> row : dataList) {
            System.out.println(row);
        }

        connection.close();
        statement.close();
        rs.close();
    }
}
