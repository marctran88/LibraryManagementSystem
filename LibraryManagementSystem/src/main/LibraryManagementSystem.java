package main;

import interfaces.ILibrary;
import library.Library;
import entities.Book;
import entities.Member;
import entities.StudentMember;
import entities.TeacherMember;

import java.util.Scanner;

public class LibraryManagementSystem {
    private static ILibrary library = new Library();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\nLibrary Management System");
            System.out.println("1. Add Book");
            System.out.println("2. Remove Book");
            System.out.println("3. Add Member");
            System.out.println("4. Remove Member");
            System.out.println("5. Borrow Book");
            System.out.println("6. Return Book");
            System.out.println("7. List All Books");
            System.out.println("8. List All Members");
            System.out.println("9. Exit");
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
                    listAllBooks();
                    break;
                case 8:
                    listAllMembers();
                    break;
                case 9:
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
        System.out.println("Book added successfully.");
    }

    private static void removeBook() {
        System.out.print("Enter ISBN of the book to remove: ");
        String ISBN = scanner.nextLine();
        library.removeBook(ISBN);
        System.out.println("Book removed successfully.");
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

        System.out.println("Member added successfully.");
    }

    private static void removeMember() {
        System.out.print("Enter member ID of the member to remove: ");
        String memberId = scanner.nextLine();
        library.removeMember(memberId);
        System.out.println("Member removed successfully.");
    }

    private static void borrowBook() {
        System.out.print("Enter member ID: ");
        String memberId = scanner.nextLine();
        System.out.print("Enter ISBN of the book to borrow: ");
        String ISBN = scanner.nextLine();
        library.borrowBook(memberId, ISBN);
    }

    private static void returnBook() {
        System.out.print("Enter member ID: ");
        String memberId = scanner.nextLine();
        System.out.print("Enter ISBN of the book to return: ");
        String ISBN = scanner.nextLine();
        library.returnBook(memberId, ISBN);
    }

    private static void listAllBooks() {
        System.out.println("All Books:");
        for (Book book : library.getAllBooks()) {
            System.out.println(book);
        }
    }

    private static void listAllMembers() {
        System.out.println("All Members:");
        for (Member member : library.getAllMembers()) {
            System.out.println(member);
        }
    }
}