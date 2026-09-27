package com.librarymanagement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Scanner;

public class Main {

    static final String url = "jdbc:mysql://localhost:3306/library_management";
    static final String username = "root";

  
    static final String password = "Muni@80088##";

    static Connection con;

    public static void main(String[] args) {

        BookService bs = new BookService();
        Scanner sc = new Scanner(System.in);
        int ch;

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            con = DriverManager.getConnection(url, username, password);

            System.out.println("Database Connected Successfully!");

            String sql = "CREATE TABLE IF NOT EXISTS books ("
                    + "book_id INT PRIMARY KEY, "
                    + "book_name VARCHAR(100), "
                    + "author VARCHAR(100), "
                    + "category VARCHAR(50), "
                    + "is_available BOOLEAN"
                    + ")";

            Statement st = con.createStatement();
            st.executeUpdate(sql);

            System.out.println("Books Table Created Successfully!");

            String sql1 = "CREATE TABLE IF NOT EXISTS members ("
                    + "member_id INT PRIMARY KEY, "
                    + "member_name VARCHAR(50), "
                    + "phone VARCHAR(15), "
                    + "email VARCHAR(100)"
                    + ")";

            Statement stmt = con.createStatement();
            stmt.executeUpdate(sql1);

            System.out.println("Member Table Created Successfully!");

            do {

                System.out.println();
                System.out.println("===========================");
                System.out.println("  LIBRARY MANAGEMENT SYSTEM");
                System.out.println("===========================");
                System.out.println("1. Add Book");
                System.out.println("2. View Book");
                System.out.println("3. Search Book");
                System.out.println("4. Approve Book Issue");
                System.out.println("5. Return Book / Fine Calculation");
                System.out.println("6. Check Availability");
                System.out.println("7. Add Member");
                System.out.println("8. Exit");
                System.out.println("===========================");

                System.out.print("Enter The Choice: ");
                ch = sc.nextInt();
                sc.nextLine();

                switch (ch) {

                case 1:
                    bs.addBook(sc);
                    break;

                case 2:
                    bs.viewBook();
                    break;

                case 3:
                    bs.searchBook(sc);
                    break;

                case 4:
                    bs.approveBookIssue(sc);
                    break;

                case 5:
                    bs.returnBook(sc);
                    break;

                case 6:
                    bs.checkAvailability(sc);
                    break;

                case 7:
                    bs.addMember(sc);
                    break;

                case 8:
                    System.out.println("Thank You!");
                    break;

                default:
                    System.out.println("Invalid Choice. Try Again.");

                }

            } while (ch != 8);

            con.close();
            sc.close();

        } catch (Exception e) {

            System.out.println("Error: " + e.getMessage());

        }
    }
}