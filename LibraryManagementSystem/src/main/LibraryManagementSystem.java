package main;

//import interfaces.ILibrary;
import library.Library;
import entities.Book;
import entities.Member;
import entities.StudentMember;
import entities.TeacherMember;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class LibraryManagementSystem {
    private static Library library = new Library();
    private static Scanner scanner = new Scanner(System.in);
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public static void main(String[] args) {
        while (true) {
            System.out.println("\nLibrary Management System");
            System.out.println("1. Add Book");
            System.out.println("2. Remove Book");
            System.out.println("3. Add Member");
            System.out.println("4. Remove Member");
            System.out.println("5. Borrow Book");
            System.out.println("6. Return Book");
            System.out.println("7. List All Available Books");
            System.out.println("8. List All Members");
            System.out.println("9. Search Books");
            System.out.println("10. Update Member Information");
            System.out.println("11. Generate Borrowed Books Report");
            System.out.println("12. Generate Overdue Books Report");
            System.out.println("13. Exit");
            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();  // consume newline

            switch (choice) {
                case 1:
                    addBook();
                    break;
                case 2:
                    removeBook();
                    break;
                case 3:
                    addMember();
                    break;
                case 4:
                    removeMember();
                    break;
                case 5:
                    borrowBook();
                    break;
                case 6:
                    returnBook();
                    break;
                case 7:
                    listAllAvailableBooks();
                    break;
                case 8:
                    listAllMembers();
                    break;
                case 9:
                    searchBooks();
                    break;
                case 10:
                    updateMemberInfo();
                    break;
                case 11:
                    generateBorrowedBooksReport();
                    break;
                case 12:
                    generateOverdueBooksReport();
                    break;
                case 13:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void addBook() {
        System.out.print("Enter title: ");
        String title = scanner.nextLine();
        System.out.print("Enter author: ");
        String author = scanner.nextLine();
        System.out.print("Enter ISBN: ");
        String ISBN = scanner.nextLine();
        library.addBook(new Book(title, author, ISBN));
    }

    private static void removeBook() {
        System.out.print("Enter ISBN of the book to remove: ");
        String ISBN = scanner.nextLine();
        try {
            library.removeBook(ISBN);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void addMember() {
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter member ID: ");
        String memberId = scanner.nextLine();
        System.out.println("Enter member type (1 for Student, 2 for Teacher): ");
        int type = scanner.nextInt();
        scanner.nextLine();  // consume newline

        if (type == 1) {
            library.addMember(new StudentMember(name, memberId));
        } else if (type == 2) {
            library.addMember(new TeacherMember(name, memberId));
        } else {
            System.out.println("Invalid member type.");
        }
    }

    private static void removeMember() {
        System.out.print("Enter member ID of the member to remove: ");
        String memberId = scanner.nextLine();
        try {
            library.removeMember(memberId);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void borrowBook() {
        System.out.print("Enter member ID: ");
        String memberId = scanner.nextLine();
        System.out.print("Enter ISBN of the book to borrow: ");
        String ISBN = scanner.nextLine();
        System.out.print("Enter due date (yyyy-MM-dd): ");
        String dueDateStr = scanner.nextLine();
        try {
            Date dueDate = sdf.parse(dueDateStr);
            library.borrowBook(memberId, ISBN, dueDate);
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please enter date as yyyy-MM-dd.");
        }
    }

    private static void returnBook() {
        System.out.print("Enter member ID: ");
        String memberId = scanner.nextLine();
        System.out.print("Enter ISBN of the book to return: ");
        String ISBN = scanner.nextLine();
        library.returnBook(memberId, ISBN);
    }

    private static void listAllAvailableBooks() {
        System.out.println("All Available Books:");
        for (Book book : library.getAllAvailableBooks()) {
            System.out.println(book);
        }
    }

    private static void listAllMembers() {
        System.out.println("All Members:");
        for (Member member : library.getAllMembers()) {
            System.out.println(member);
        }
    }

    private static void searchBooks() {
        System.out.println("Search Books by:");
        System.out.println("1. Title");
        System.out.println("2. Author");
        System.out.println("3. ISBN");
        System.out.print("Enter choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();  // consume newline

        switch (choice) {
            case 1:
                System.out.print("Enter title: ");
                String title = scanner.nextLine();
                List<Book> booksByTitle = library.searchBooksByTitle(title);
                System.out.println("Search Results:");
                for (Book book : booksByTitle) {
                    System.out.println(book);
                }
                break;
            case 2:
                System.out.print("Enter author: ");
                String author = scanner.nextLine();
                List<Book> booksByAuthor = library.searchBooksByAuthor(author);
                System.out.println("Search Results:");
                for (Book book : booksByAuthor) {
                    System.out.println(book);
                }
                break;
            case 3:
                System.out.print("Enter ISBN: ");
                String isbn = scanner.nextLine();
                Book bookByISBN = library.searchBooksByISBN(isbn);
                if (bookByISBN != null) {
                    System.out.println(bookByISBN);
                } else {
                    System.out.println("No book found with the given ISBN.");
                }
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private static void updateMemberInfo() {
        System.out.print("Enter current member ID: ");
        String currentMemberId = scanner.nextLine();
        System.out.print("Enter new name: ");
        String newName = scanner.nextLine();
        System.out.print("Enter new member ID: ");
        String newMemberId = scanner.nextLine();
        library.updateMember(currentMemberId, newName, newMemberId);
    }

    private static void generateBorrowedBooksReport() {
        System.out.println("Borrowed Books:");
        for (Book book : library.getAllBorrowedBooks()) {
            System.out.println(book);
        }
    }

    private static void generateOverdueBooksReport() {
        System.out.println("Overdue Books:");
        for (Book book : library.getAllOverdueBooks()) {
            System.out.println(book);
        }
    }
}