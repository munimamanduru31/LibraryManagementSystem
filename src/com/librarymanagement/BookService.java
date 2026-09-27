package com.librarymanagement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class BookService {

    static final String url = "jdbc:mysql://localhost:3306/library_management";
    static final String username = "root";

    static final String password = "Muni@80088##";

    static Connection con;

    // 1. ADD BOOK
    public void addBook(Scanner sc) throws SQLException {

        con = DriverManager.getConnection(url, username, password);

        System.out.print("Enter Book ID: ");
        int bookId = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter Book Name: ");
        String bookName = sc.nextLine();

        System.out.print("Enter Author Name: ");
        String author = sc.nextLine();

        System.out.print("Enter Category: ");
        String category = sc.nextLine();

        System.out.print("Is Book Available (true/false): ");
        boolean isAvailable = sc.nextBoolean();

        String sql = "INSERT INTO books "
                + "(book_id, book_name, author, category, is_available) "
                + "VALUES (?, ?, ?, ?, ?)";

        PreparedStatement ps = con.prepareStatement(sql);

        ps.setInt(1, bookId);
        ps.setString(2, bookName);
        ps.setString(3, author);
        ps.setString(4, category);
        ps.setBoolean(5, isAvailable);

        int result = ps.executeUpdate();

        if (result > 0) {
            System.out.println("Book Added Successfully!");
        }

        ps.close();
        con.close();
    }

    // 2. VIEW BOOK
    public void viewBook() throws SQLException {

        con = DriverManager.getConnection(url, username, password);

        String sql = "SELECT * FROM books";

        PreparedStatement ps = con.prepareStatement(sql);

        ResultSet rs = ps.executeQuery();

        System.out.println();
        System.out.println("---------------------------------------------------------------");
        System.out.println("                       BOOK DETAILS");
        System.out.println("---------------------------------------------------------------");

        System.out.printf("%-10s %-25s %-20s %-15s %-10s%n",
                "BOOK ID",
                "BOOK NAME",
                "AUTHOR",
                "CATEGORY",
                "AVAILABLE");

        System.out.println("---------------------------------------------------------------");

        while (rs.next()) {

            System.out.printf("%-10d %-25s %-20s %-15s %-10s%n",
                    rs.getInt("book_id"),
                    rs.getString("book_name"),
                    rs.getString("author"),
                    rs.getString("category"),
                    rs.getBoolean("is_available"));
        }

        System.out.println("---------------------------------------------------------------");

        rs.close();
        ps.close();
        con.close();
    }

    // 3. SEARCH BOOK
    public void searchBook(Scanner sc) throws SQLException {

        System.out.print("Enter Book ID to Search: ");
        int bookId = sc.nextInt();

        con = DriverManager.getConnection(url, username, password);

        String sql = "SELECT * FROM books WHERE book_id = ?";

        PreparedStatement ps = con.prepareStatement(sql);

        ps.setInt(1, bookId);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {

            System.out.println();
            System.out.println("---------- BOOK DETAILS ----------");

            System.out.println("Book ID      : " + rs.getInt("book_id"));
            System.out.println("Book Name    : " + rs.getString("book_name"));
            System.out.println("Author       : " + rs.getString("author"));
            System.out.println("Category     : " + rs.getString("category"));
            System.out.println("Availability : " + rs.getBoolean("is_available"));

            System.out.println("----------------------------------");

        } else {

            System.out.println("Book Not Found!");
        }

        rs.close();
        ps.close();
        con.close();
    }

    // 4. ADD MEMBER
    public void addMember(Scanner sc) throws SQLException {

        System.out.print("Enter Member ID: ");
        int memberId = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter Member Name: ");
        String memberName = sc.nextLine();

        System.out.print("Enter Phone Number: ");
        String phone = sc.nextLine();

        System.out.print("Enter Email: ");
        String email = sc.nextLine();

        con = DriverManager.getConnection(url, username, password);

        String sql = "INSERT INTO members VALUES (?, ?, ?, ?)";

        PreparedStatement ps = con.prepareStatement(sql);

        ps.setInt(1, memberId);
        ps.setString(2, memberName);
        ps.setString(3, phone);
        ps.setString(4, email);

        int result = ps.executeUpdate();

        if (result > 0) {
            System.out.println("Member Added Successfully!");
        }

        ps.close();
        con.close();
    }

    // 5. APPROVE BOOK ISSUE
    public void approveBookIssue(Scanner sc) throws SQLException {

        con = DriverManager.getConnection(url, username, password);

        String createTable = "CREATE TABLE IF NOT EXISTS book_issues ("
                + "issue_id INT PRIMARY KEY AUTO_INCREMENT, "
                + "book_id INT NOT NULL, "
                + "member_id INT NOT NULL, "
                + "issue_date DATE NOT NULL, "
                + "due_date DATE NOT NULL, "
                + "return_date DATE, "
                + "fine DECIMAL(10,2) DEFAULT 0, "
                + "FOREIGN KEY (book_id) REFERENCES books(book_id), "
                + "FOREIGN KEY (member_id) REFERENCES members(member_id)"
                + ")";

        Statement st = con.createStatement();
        st.executeUpdate(createTable);

        System.out.print("Enter Book ID: ");
        int bookId = sc.nextInt();

        System.out.print("Enter Member ID: ");
        int memberId = sc.nextInt();

        // CHECK MEMBER
        String memberSql = "SELECT * FROM members WHERE member_id = ?";

        PreparedStatement memberPs = con.prepareStatement(memberSql);

        memberPs.setInt(1, memberId);

        ResultSet memberRs = memberPs.executeQuery();

        if (!memberRs.next()) {

            System.out.println("Member Not Found!");

            memberRs.close();
            memberPs.close();
            con.close();

            return;
        }

        // CHECK BOOK
        String bookSql = "SELECT * FROM books WHERE book_id = ?";

        PreparedStatement bookPs = con.prepareStatement(bookSql);

        bookPs.setInt(1, bookId);

        ResultSet bookRs = bookPs.executeQuery();

        if (bookRs.next()) {

            boolean available = bookRs.getBoolean("is_available");

            if (available) {

                // INSERT ISSUE DETAILS
                String issueSql = "INSERT INTO book_issues "
                        + "(book_id, member_id, issue_date, due_date) "
                        + "VALUES (?, ?, CURDATE(), "
                        + "DATE_ADD(CURDATE(), INTERVAL 7 DAY))";

                PreparedStatement issuePs = con.prepareStatement(issueSql);

                issuePs.setInt(1, bookId);
                issuePs.setInt(2, memberId);

                issuePs.executeUpdate();

                // UPDATE BOOK AVAILABILITY
                String updateSql = "UPDATE books "
                        + "SET is_available = false "
                        + "WHERE book_id = ?";

                PreparedStatement updatePs = con.prepareStatement(updateSql);

                updatePs.setInt(1, bookId);

                updatePs.executeUpdate();

                System.out.println();
                System.out.println("=================================");
                System.out.println("       BOOK ISSUE APPROVED");
                System.out.println("=================================");
                System.out.println("Book ID    : " + bookId);
                System.out.println("Member ID  : " + memberId);
                System.out.println("Issue Date : Today");
                System.out.println("Due Date   : 7 Days");
                System.out.println("Status     : APPROVED");
                System.out.println("=================================");

                issuePs.close();
                updatePs.close();

            } else {

                System.out.println("Book Already Issued!");
            }

        } else {

            System.out.println("Book Not Found!");
        }

        memberRs.close();
        memberPs.close();
        bookRs.close();
        bookPs.close();
        st.close();
        con.close();
    }

    // 6. RETURN BOOK AND FINE CALCULATION
    public void returnBook(Scanner sc) throws SQLException {

        con = DriverManager.getConnection(url, username, password);

        System.out.print("Enter Book ID to Return: ");
        int bookId = sc.nextInt();

        String sql = "SELECT * FROM book_issues "
                + "WHERE book_id = ? "
                + "AND return_date IS NULL";

        PreparedStatement ps = con.prepareStatement(sql);

        ps.setInt(1, bookId);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {

            int issueId = rs.getInt("issue_id");

            String updateIssue = "UPDATE book_issues SET "
                    + "return_date = CURDATE(), "
                    + "fine = CASE "
                    + "WHEN CURDATE() > due_date "
                    + "THEN DATEDIFF(CURDATE(), due_date) * 10 "
                    + "ELSE 0 END "
                    + "WHERE issue_id = ?";

            PreparedStatement updatePs = con.prepareStatement(updateIssue);

            updatePs.setInt(1, issueId);

            updatePs.executeUpdate();

            // MAKE BOOK AVAILABLE
            String updateBook = "UPDATE books "
                    + "SET is_available = true "
                    + "WHERE book_id = ?";

            PreparedStatement bookPs = con.prepareStatement(updateBook);

            bookPs.setInt(1, bookId);

            bookPs.executeUpdate();

            // SHOW FINE
            String fineSql = "SELECT book_id, issue_date, due_date, "
                    + "return_date, fine "
                    + "FROM book_issues "
                    + "WHERE issue_id = ?";

            PreparedStatement finePs = con.prepareStatement(fineSql);

            finePs.setInt(1, issueId);

            ResultSet fineRs = finePs.executeQuery();

            if (fineRs.next()) {

                System.out.println();
                System.out.println("=================================");
                System.out.println("       BOOK RETURN RECEIPT");
                System.out.println("=================================");

                System.out.println("Book ID     : " + fineRs.getInt("book_id"));
                System.out.println("Issue Date  : " + fineRs.getDate("issue_date"));
                System.out.println("Due Date    : " + fineRs.getDate("due_date"));
                System.out.println("Return Date : " + fineRs.getDate("return_date"));
                System.out.println("Fine Amount : Rs."
                        + fineRs.getDouble("fine"));

                System.out.println("Status      : RETURNED");

                System.out.println("=================================");
            }

            fineRs.close();
            finePs.close();
            updatePs.close();
            bookPs.close();

            System.out.println("Book Returned Successfully!");

        } else {

            System.out.println("No Active Issue Found!");
        }

        rs.close();
        ps.close();
        con.close();
    }

    // 7. CHECK BOOK AVAILABILITY
    public void checkAvailability(Scanner sc) throws SQLException {

        con = DriverManager.getConnection(url, username, password);

        System.out.print("Enter Book ID: ");
        int bookId = sc.nextInt();

        String sql = "SELECT book_name, is_available "
                + "FROM books WHERE book_id = ?";

        PreparedStatement ps = con.prepareStatement(sql);

        ps.setInt(1, bookId);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {

            System.out.println();
            System.out.println("Book Name : "
                    + rs.getString("book_name"));

            if (rs.getBoolean("is_available")) {

                System.out.println("Status    : Available");

            } else {

                System.out.println("Status    : Not Available");
            }

        } else {

            System.out.println("Book Not Found!");
        }

        rs.close();
        ps.close();
        con.close();
    }
}