# Library Management System

A simple Java-based Library Management System developed using Core Java, JDBC, and MySQL.

## Features

- Add Books
- View Books
- Search Books
- Add Members
- Issue Books
- Return Books
- Check Book Availability
- MySQL Database Connectivity

## Technologies Used

- Java
- JDBC
- MySQL
- Eclipse IDE

## Database Tables

- books
- members

## Concepts Used

- Classes and Objects
- JDBC
- SQL
- PreparedStatement
- CRUD Operations
- Exception Handling
- Scanner

## How to Run

1. Install Java and MySQL.
2. Create the `library_management` database.
3. Add MySQL Connector/J to the project.
4. Update the MySQL username and password in the Java code.
5. Run `Main.java` as a Java Application.

## Project Structure

```text
LibraryManagementSystem
│
├── src
│   └── com.librarymanagement
│       ├── Main.java
│       └── BookService.java
│
├── .gitignore
└── README.md