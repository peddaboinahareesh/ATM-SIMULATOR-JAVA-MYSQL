
package com.ATM;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class ATM {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("===== ATM MACHINE =====");

        System.out.print("Enter Account Number: ");
        int accountNumber = sc.nextInt();

        System.out.print("Enter PIN: ");
        int pin = sc.nextInt();

        try {
            Connection con = Dbconnection.getConnection();

            String query = "SELECT * FROM accounts WHERE account_number = ? AND pin = ?";

            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, accountNumber);
            ps.setInt(2, pin);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                System.out.println("Login Successful!");
                System.out.println("Welcome " + rs.getString("name"));

                boolean running = true;

                while (running) {

                    System.out.println("\n===== ATM MENU =====");
                    System.out.println("1. Check Balance");
                    System.out.println("2. Deposit Money");
                    System.out.println("3. Withdraw Money");
                    System.out.println("4. Mini Statement");
                    System.out.println("5. Exit");

                    System.out.print("Enter Your Choice: ");
                    int choice = sc.nextInt();

                    switch (choice) {

                        case 1:
                            System.out.println("Current Balance: " +
                                    rs.getDouble("balance"));
                            break;

                        case 2:
                            System.out.print("Enter Deposit Amount: ");
                            double deposit = sc.nextDouble();

                            if (deposit > 0) {
                                updateBalance(con, accountNumber, deposit);
                                System.out.println("Amount Deposited Successfully!");
                            } else {
                                System.out.println("Invalid Amount!");
                            }
                            break;

                        case 3:
                            System.out.print("Enter Withdraw Amount: ");
                            double withdraw = sc.nextDouble();

                            double balance = getBalance(con, accountNumber);

                            if (withdraw > 0 && withdraw <= balance) {
                                updateBalance(con, accountNumber, -withdraw);
                                System.out.println("Please Collect Your Cash!");
                            } else {
                                System.out.println("Insufficient Balance or Invalid Amount!");
                            }
                            break;

                        case 4:
                            showTransactions(con, accountNumber);
                            break;

                        case 5:
                            System.out.println("Thank You for Using ATM!");
                            running = false;
                            break;

                        default:
                            System.out.println("Invalid Choice!");
                    }
                }

            } else {
                System.out.println("Invalid Account Number or PIN!");
            }

            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        sc.close();
    }

    public static double getBalance(Connection con, int accountNumber)
            throws Exception {

        String query = "SELECT balance FROM accounts WHERE account_number = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, accountNumber);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getDouble("balance");
        }

        return 0;
    }

    public static void updateBalance(Connection con, int accountNumber,
            double amount) throws Exception {

        String query = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
        PreparedStatement ps = con.prepareStatement(query);

        ps.setDouble(1, amount);
        ps.setInt(2, accountNumber);

        ps.executeUpdate();

        String type = amount > 0 ? "DEPOSIT" : "WITHDRAW";
        double transactionAmount = Math.abs(amount);

        String transactionQuery =
                "INSERT INTO transactions (account_number, transaction_type, amount) VALUES (?, ?, ?)";

        PreparedStatement transactionPs =
                con.prepareStatement(transactionQuery);

        transactionPs.setInt(1, accountNumber);
        transactionPs.setString(2, type);
        transactionPs.setDouble(3, transactionAmount);

        transactionPs.executeUpdate();
    }

    public static void showTransactions(Connection con, int accountNumber)
            throws Exception {

        String query = "SELECT * FROM transactions WHERE account_number = ? ORDER BY transaction_date DESC";

        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, accountNumber);

        ResultSet rs = ps.executeQuery();

        System.out.println("\n===== MINI STATEMENT =====");

        boolean found = false;

        while (rs.next()) {
            found = true;

            System.out.println(
                    rs.getString("transaction_type") + " - Rs. " +
                    rs.getDouble("amount") + " - " +
                    rs.getTimestamp("transaction_date"));
        }

        if (!found) {
            System.out.println("No Transactions Found!");
        }
    }
}
