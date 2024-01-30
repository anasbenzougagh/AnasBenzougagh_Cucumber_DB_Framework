package com.junit;

import com.utility.DB_Util;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;
import java.util.*;

public class JUnitTestReview {

    //Connection required String:
    String dbURL = "jdbc:oracle:thin:@52.87.197.190:1521:XE";
    String dbUsername = "hr";
    String dbPassword = "hr";

    @Test
    public void introJDBC() throws SQLException {
        //Get connection needs SQLException to use getConnection
        /** Main interfaces:
         1-Connection : use DriverManager getConnection method to establish connection passing URL,Username and password args.
         2-Statement : once connected use connection to created statement (then set limitation to the statement).
         3-ResultSet : once execute query method is initiated you can pass query then store in a resultSet variable.
         */
        //Creating connection
        Connection connection = DriverManager.getConnection(dbURL, dbUsername, dbPassword);

        //To execute queries
        //Statement statement = connection.createStatement();
        Statement statement = connection.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,  //means that result set is scrollable
                ResultSet.CONCUR_READ_ONLY);        //means that result set is readable only

        //This holds statement value after execute
        ResultSet resultSet = statement.executeQuery("select * from EMPLOYEES");

        //extracting data from table as long there is a row
        while (resultSet.next()) {
            System.out.println(
                    //return will match getValue
                    resultSet.getString("EMAIL")
                            + "-" +
                            resultSet.getString(4)
                            + "-\n" +
                            resultSet.getInt("EMPLOYEE_ID")
                            + "-" +
                            resultSet.getInt(1)
            );
        }


        connection.close();
        statement.close();
        resultSet.close();
    }

    @Test
    public void basicNavigation() throws SQLException {
        Connection connection = DriverManager.getConnection(dbURL, dbUsername, dbPassword);

        //ResultSet.TYPE_SCROLL_INSENSITIVE   --> to do flexible navigation between rows
        //ResultSet.CONCUR_READ_ONLY          --> do not update anything form DB, only display
        Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE
                , ResultSet.CONCUR_READ_ONLY);

        ResultSet resultSet = statement.executeQuery("SELECT FIRST_NAME, LAST_NAME FROM EMPLOYEES");

        //Get first row ----
        //rs.next(); ==> used to move to first row after metadata.
        resultSet.next();
        System.out.println("First Row");
        System.out.println(resultSet.getString("FIRST_NAME") + " - " + resultSet.getString(2));

        //Get second row ----
        resultSet.next();
        System.out.println("Second Row");
        System.out.println(resultSet.getString("FIRST_NAME") + " - " + resultSet.getString(2));

        //Get last row ---
        resultSet.last();
        System.out.println("Last Row");
        System.out.println(resultSet.getString("FIRST_NAME") + " - " + resultSet.getString(2));

        //Get row number
        int rowNumber = resultSet.getRow();
        System.out.println("Row Number");
        System.out.println(rowNumber);

        //Get row by number
        System.out.println("Absolute");
        resultSet.absolute(40);
        System.out.println(resultSet.getString("FIRST_NAME") + " - " + resultSet.getString(2));

        //Get previous row
        System.out.println("Previous");
        resultSet.previous();
        System.out.println(resultSet.getRow());
        System.out.println(resultSet.getString("FIRST_NAME") + " - " + resultSet.getString(2));

        //Task print the whole table
        System.out.println("Loop");
        //resultSet.absolute(0);
        resultSet.beforeFirst();
        while (resultSet.next()) {
            System.out.println(resultSet.getString("FIRST_NAME") + " - " + resultSet.getString(2));
        }
        connection.close();
        statement.close();
        resultSet.close();
    }

    @Test
    public void metaData() throws SQLException {
        Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
        Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = statement.executeQuery(""
                + "SELECT FIRST_NAME,LAST_NAME,SALARY FROM EMPLOYEES WHERE ROWNUM<6"
                + "");

        //Getting database metadata
        System.out.println("Getting database metadata");
        DatabaseMetaData dbMetaData = conn.getMetaData();
        System.out.println("dbMetaData = " + dbMetaData);
        System.out.println(dbMetaData.getUserName());
        System.out.println(dbMetaData.getDatabaseProductName());
        System.out.println(dbMetaData.getDatabaseProductVersion());
        System.out.println(dbMetaData.getDriverName());

        //Getting resultSetMetadata
        System.out.println("Getting resultSetMetadata");
        ResultSetMetaData rsmd = rs.getMetaData();

        //Get number of columns
        System.out.println(".getColumnCount");
        int columnCount = rsmd.getColumnCount();
        System.out.println(columnCount);

        //Get column name
        System.out.println(".getColumnName");
        System.out.println(rsmd.getColumnName(2));

        //Get all column names
        System.out.println("Print all columns");
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            System.out.print(rsmd.getColumnName(i) + " ");
        }


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
        //EMPLOYEE_ID - 100
        //FIRST_NAME - Steven

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
        Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = statement.executeQuery(""
                + "SELECT FIRST_NAME,LAST_NAME,SALARY FROM EMPLOYEES WHERE ROWNUM<6"
                + "");

        //Storing data dynamically
        ResultSetMetaData rsmd = rs.getMetaData();
        List<Map<String, Object>> dataList = new ArrayList<>();

        //Number of column number
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

    @Test
    public void DBUtils() {
        //create a connection
        //DB_Util.createConnection(dbURL,dbUsername,dbPassword);
        DB_Util.createConnection();

        //run query
        DB_Util.runQuery("SELECT FIRST_NAME,LAST_NAME,SALARY FROM EMPLOYEES");

        //get result
        System.out.println("=====Get ME First Row First Column=====");
        System.out.println(DB_Util.getFirstRowFirstColumn());

        System.out.println("=====Get ME All Column=====");
        System.out.println(DB_Util.getAllColumnNamesAsList());

        System.out.println("=====Get ME All First Names=====");
        System.out.println(DB_Util.getColumnDataAsList(1));

        System.out.println("=====Get ME Rows Number=====");
        System.out.println(DB_Util.getRowCount());

        System.out.println("=====Get ME Specific Row Info=====");
        System.out.println("=====Get ME AS MAP=====");
        System.out.println(DB_Util.getRowMap(1));

        System.out.println("=====Get ME ALL DATA AS LIST OF MAP=====");
        System.out.println(DB_Util.getRowDataAsList(2));

        List<Map<String, Object>> allRowAsListOfMap = DB_Util.getAllRowAsListOfMap();

        for (Map<String, Object> rowMap : allRowAsListOfMap) {
            System.out.println(rowMap);
        }

        //close the connection
        DB_Util.destroy();
    }
}
