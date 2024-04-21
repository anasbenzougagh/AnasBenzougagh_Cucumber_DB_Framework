package com.steps;

import com.utility.ConfigurationReader;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.sql.*;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DatabaseTest {
    Connection connection;
    Statement statement;
    ResultSet resultSet;


    @Given("base connection established")
    public void base_connection_established() throws SQLException {
        connection = DriverManager.getConnection(
                ConfigurationReader.getProperty("db.url"),
                ConfigurationReader.getProperty("db.username"),
                ConfigurationReader.getProperty("db.password")
        );
        statement = connection.createStatement();
    }

    @Given("following query {string} is executed")
    public void following_query_is_executed(String query) throws SQLException {
        switch (query) {
            case "employee":
                resultSet = statement.executeQuery("select * from Employees");
                break;
            case "products":
                resultSet = statement.executeQuery("select * from Products");
                break;
            default:
                System.out.println("Invalid input");
        }
    }

    @When("I retrieve a random record from the database")
    public void i_retrieve_a_random_record_from_the_database() throws SQLException {
        resultSet = statement.executeQuery("select LastName,FirstName,Title,TitleOfCourtesy,Address,HomePhone,Notes from Employees\n" +
                "where LastName = 'Leverling';");
    }

    @Then("then verify single data that is matching")
    public void then_verify_single_data_that_is_matching() throws SQLException {
        //Assert that this person's data exist in the database (resources/data/employee.csv)

        assertEquals(resultSet.getString("LastName"), "Leverling");
        assertEquals(resultSet.getString("FirstName"), "Janet");
        assertEquals(resultSet.getString("Title"), "Sales Representative");
        assertEquals(resultSet.getString("TitleOfCourtesy"), "Ms.");
        assertEquals(resultSet.getString("Address"), "722 Moss Bay Blvd.");
        assertEquals(resultSet.getString("HomePhone"), "(206) 555-3412");
        assertEquals(resultSet.getString("Notes"), "Janet has a BS degree in chemistry from Boston College (1984).  She has also completed a certificate program in food retailing management.  Janet was hired as a sales associate in 1991 and promoted to sales representative in February 1992.");
    }

    HashMap<String,Object> product = new HashMap<>();
    @When("I randomly select a record then store it in a map")
    public void i_randomly_select_a_record_then_store_it_in_a_map() throws SQLException {
        resultSet = statement
                .executeQuery("select ProductName , QuantityPerUnit, UnitPrice,UnitsInStock,ReorderLevel from Products\n" +
                "where ProductName = 'Chai'");

        for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
            product.put(resultSet.getMetaData().getColumnName(i), resultSet.getString(i));
        }
        System.out.println("product = " + product);

    }

    @Then("assert that data provided matches record exists in the database")
    public void assert_that_data_provided_matches_record_exists_in_the_database() {
        //Assert that this person's data exist in the database (resources/data/employee.csv)
        assertEquals(product.get("ProductName"),"Chai");
        assertEquals(product.get("QuantityPerUnit"),"10 boxes x 20 bags");
        assertEquals(product.get("UnitPrice"),"18");
        assertEquals(product.get("UnitsInStock"),"39");
        assertEquals(product.get("ReorderLevel"),"10");
    }
}
