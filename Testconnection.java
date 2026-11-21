
package com.ATM;

import java.sql.Connection;

public class Testconnection {

    public static void main(String[] args) {

        Connection con = Dbconnection.getConnection();

        if (con != null) {
            System.out.println("ATM Database Connection Test Passed!");
        } else {
            System.out.println("ATM Database Connection Test Failed!");
        }

    }
}

