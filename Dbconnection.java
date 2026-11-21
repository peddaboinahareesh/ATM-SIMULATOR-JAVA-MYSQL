 

package com.ATM;

import java.sql.Connection;
import java.sql.DriverManager;
public class Dbconnection {

    public static Connection getConnection() {

        Connection con = null;

        try {

            String url = "jdbc:mysql://localhost:3306/atm_db";
            String username = "root";
            String password = "987654321";

            con = DriverManager.getConnection(
                url, username, password
            );

            System.out.println("Database Connected Successfully!");

        } catch (Exception e) {

            System.out.println("Database Connection Failed!");
            e.printStackTrace();

        }

        return con;
    }
}


