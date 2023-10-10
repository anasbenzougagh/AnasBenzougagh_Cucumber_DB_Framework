package com.steps;

import com.utility.DB_Util;
import io.cucumber.java.After;
import io.cucumber.java.Before;


public class Hooks {

    @Before("@db")
    public void setUpDB(){
        System.out.println("Connecting to database...");
        DB_Util.createConnection();
    }

    @After("@db")
    public void tearDownDB(){
        System.out.println("close database connection...");
        DB_Util.destroy();
    }

}