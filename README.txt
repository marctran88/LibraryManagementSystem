# Java Library Management System

This is a simple Library Management System implemented in Java, utilizing MySQL for persistent data storage. The system demonstrates object-oriented principles such as inheritance, abstraction, and composition. It supports basic library operations like adding/removing books and members, borrowing/returning books, and listing all books and members.

## Features

- Add and remove books
- Add and remove members (students and teachers)
- Borrow and return books
- List all books and members
- Persistent data storage using MySQL

## Directory Structure
LibraryManagementSystem/
├── lib/
│   └── mysql-connector-j-8.4.0.jar
└── src/
    ├── interfaces/
    │   ├── ILibrary.java
    │   ├── IBook.java
    │   └── IMember.java
    ├── base/
    │   ├── AbstractBook.java
    │   └── AbstractMember.java
    ├── entities/
    │   ├── Book.java
    │   ├── Member.java
    │   ├── StudentMember.java
    │   └── TeacherMember.java
    ├── library/
    │   └── Library.java
    ├── main/
    │   └── LibraryManagementSystem.java
    └── util/
        └── DBUtil.java

## Prerequisites

- Java Development Kit (JDK) 11 or higher
- MySQL Server
- MySQL Workbench (optional, for managing the database)
- MySQL Connector/J (JDBC driver)

## Setup

### 1. Install MySQL Server

Download and install MySQL Community Server from [MySQL's official website](https://dev.mysql.com/downloads/mysql/).

### 2. Create MySQL Database and Tables

Open MySQL Workbench and connect to your MySQL server. Execute the library_db.sql in the `sql` directory to set up the database and tables.

### 3. Configure Database Connection
Update the UBUtil.java file in the `util` directory with your MySQL database credentials: replace your_mysql_username and your_mysql_password with your actual MySQL credentials.

## Compiling and Running the Program
Use command line to run these commands

### 1.	Navigate to the Project Directory: 
cd ~/LibraryManagementSystem

### 2. Compile the Project: 
javac -cp lib/mysql-connector-j-8.4.0.jar src/interfaces/*.java src/base/*.java src/entities/*.java src/library/*.java src/main/*.java src/util/*.java

### 3. Run the Program:
java -cp .:lib/mysql-connector-j-8.4.0.jar main.LibraryManagementSystem

## Usage
Upon running the program, you will be presented with a menu to perform various library operations:
Library Management System
1. Add Book
2. Remove Book
3. Add Member
4. Remove Member
5. Borrow Book
6. Return Book
7. List All Available Books
8. List All Members
9. Search Books
10. Update Member Information
11. Generate Borrowed Books Report
12. Generate Overdue Books Report
13. Exit
Enter choice: 

Follow the prompts to interact with the system.




