package library;

import interfaces.ILibrary;
import entities.Book;
import entities.Member;
import entities.StudentMember;
import entities.TeacherMember;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class Library implements ILibrary {
    @Override
    public void addBook(Book book) {
        if (!isValidISBN(book.getISBN())) {
            System.out.println("Invalid ISBN format.");
            return;
        }
        if (bookExists(book.getISBN())) {
            System.out.println("Book with ISBN " + book.getISBN() + " already exists.");
            return;
        }
        String sql = "INSERT INTO books (title, author, isbn, is_borrowed) VALUES (?, ?, ?, ?)";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getISBN());
            pstmt.setBoolean(4, book.isBorrowed());
            pstmt.executeUpdate();
            System.out.println("Book added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean bookExists(String isbn) {
        String sql = "SELECT COUNT(*) FROM books WHERE isbn = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, isbn);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void removeBook(String isbn) throws Exception {
        if (!bookExists(isbn)) {
            throw new Exception("Book with ISBN " + isbn + " does not exist.");
        }
        String checkSql = "SELECT COUNT(*) FROM borrow_transactions WHERE book_id = (SELECT id FROM books WHERE isbn = ?)";
        String deleteSql = "DELETE FROM books WHERE isbn = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement checkStmt = connection.prepareStatement(checkSql);
             PreparedStatement deleteStmt = connection.prepareStatement(deleteSql)) {
            checkStmt.setString(1, isbn);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                throw new Exception("Cannot remove book. It has active borrow transactions.");
            }
            deleteStmt.setString(1, isbn);
            deleteStmt.executeUpdate();
            System.out.println("Book removed successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Book> searchBooksByTitle(String title) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE title LIKE ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + title + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Book book = new Book(rs.getString("title"), rs.getString("author"), rs.getString("isbn"));
                book.setBorrowed(rs.getBoolean("is_borrowed"));
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    @Override
    public List<Book> searchBooksByAuthor(String author) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE author LIKE ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + author + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Book book = new Book(rs.getString("title"), rs.getString("author"), rs.getString("isbn"));
                book.setBorrowed(rs.getBoolean("is_borrowed"));
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    @Override
    public Book searchBooksByISBN(String isbn) {
        String sql = "SELECT * FROM books WHERE isbn = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, isbn);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Book book = new Book(rs.getString("title"), rs.getString("author"), rs.getString("isbn"));
                book.setBorrowed(rs.getBoolean("is_borrowed"));
                return book;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Book> getAllAvailableBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE is_borrowed = false";
        try (Connection connection = DBUtil.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Book book = new Book(rs.getString("title"), rs.getString("author"), rs.getString("isbn"));
                book.setBorrowed(rs.getBoolean("is_borrowed"));
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    @Override
    public List<Book> getAllBorrowedBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT b.title, b.author, b.isbn, bt.due_date, m.name as member_name, m.member_id as member_id, bt.borrow_date " +
                     "FROM books b " +
                     "JOIN borrow_transactions bt ON b.id = bt.book_id " +
                     "JOIN members m ON bt.member_id = m.id " +
                     "WHERE b.is_borrowed = true";
        try (Connection connection = DBUtil.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Book book = new Book(rs.getString("title"), rs.getString("author"), rs.getString("isbn"));
                book.setBorrowed(true);
                book.setDueDate(rs.getDate("due_date"));
                System.out.println("Book: " + book + ", Borrowed by: " + rs.getString("member_name") + " (ID: " + rs.getString("member_id") + "), Borrowed Date: " + rs.getDate("borrow_date"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    @Override
    public List<Book> getAllOverdueBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT b.title, b.author, b.isbn, bt.due_date, m.name as member_name, m.member_id as member_id, bt.borrow_date " +
                     "FROM books b " +
                     "JOIN borrow_transactions bt ON b.id = bt.book_id " +
                     "JOIN members m ON bt.member_id = m.id " +
                     "WHERE b.is_borrowed = true AND bt.due_date < CURRENT_DATE";
        try (Connection connection = DBUtil.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Book book = new Book(rs.getString("title"), rs.getString("author"), rs.getString("isbn"));
                book.setBorrowed(true);
                book.setDueDate(rs.getDate("due_date"));
                System.out.println("Book: " + book + ", Borrowed by: " + rs.getString("member_name") + " (ID: " + rs.getString("member_id") + "), Borrowed Date: " + rs.getDate("borrow_date"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    @Override
    public void addMember(Member member) {
        if (memberExists(member.getMemberId())) {
            System.out.println("Member with ID " + member.getMemberId() + " already exists.");
            return;
        }
        String sql = "INSERT INTO members (name, member_id, member_type) VALUES (?, ?, ?)";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, member.getName());
            pstmt.setString(2, member.getMemberId());
            pstmt.setString(3, member instanceof StudentMember ? "Student" : "Teacher");
            pstmt.executeUpdate();
            System.out.println("Member added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean memberExists(String memberId) {
        String sql = "SELECT COUNT(*) FROM members WHERE member_id = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, memberId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void removeMember(String memberId) throws Exception {
        if (!memberExists(memberId)) {
            throw new Exception("Member with ID " + memberId + " does not exist.");
        }
        String checkSql = "SELECT COUNT(*) FROM borrow_transactions WHERE member_id = (SELECT id FROM members WHERE member_id = ?)";
        String deleteSql = "DELETE FROM members WHERE member_id = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement checkStmt = connection.prepareStatement(checkSql);
             PreparedStatement deleteStmt = connection.prepareStatement(deleteSql)) {
            checkStmt.setString(1, memberId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                throw new Exception("Cannot remove member. They have active borrow transactions.");
            }
            deleteStmt.setString(1, memberId);
            deleteStmt.executeUpdate();
            System.out.println("Member removed successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateMember(String memberId, String newName, String newMemberId) {
        if (!memberExists(memberId)) {
            System.out.println("Member with ID " + memberId + " does not exist.");
            return;
        }
        String sql = "UPDATE members SET name = ?, member_id = ? WHERE member_id = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newName);
            pstmt.setString(2, newMemberId);
            pstmt.setString(3, memberId);
            pstmt.executeUpdate();
            System.out.println("Member information updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isBookBorrowed(String isbn) {
        String sql = "SELECT is_borrowed FROM books WHERE isbn = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, isbn);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("is_borrowed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isValidISBN(String isbn) {
        return isbn.matches("\\d{10}|\\d{13}");
    }

    @Override
    public void borrowBook(String memberId, String isbn, Date dueDate) {
        if (!memberExists(memberId)) {
            System.out.println("Member with ID " + memberId + " does not exist.");
            return;
        }
        if (!bookExists(isbn)) {
            System.out.println("Book with ISBN " + isbn + " does not exist.");
            return;
        }
        if (isBookBorrowed(isbn)) {
            System.out.println("Book with ISBN " + isbn + " is already borrowed.");
            return;
        }
        String updateBookSql = "UPDATE books SET is_borrowed = true WHERE isbn = ?";
        String insertBorrowSql = "INSERT INTO borrow_transactions (member_id, book_id, borrow_date, due_date) VALUES ((SELECT id FROM members WHERE member_id = ?), (SELECT id FROM books WHERE isbn = ?), ?, ?)";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement updateBookPstmt = connection.prepareStatement(updateBookSql);
             PreparedStatement insertBorrowPstmt = connection.prepareStatement(insertBorrowSql)) {

            // Update the book's borrowed status
            updateBookPstmt.setString(1, isbn);
            updateBookPstmt.executeUpdate();

            // Insert a record into borrow_transactions
            insertBorrowPstmt.setString(1, memberId);
            insertBorrowPstmt.setString(2, isbn);
            insertBorrowPstmt.setDate(3, new java.sql.Date(new Date().getTime())); // Borrow date is today
            insertBorrowPstmt.setDate(4, new java.sql.Date(dueDate.getTime()));
            insertBorrowPstmt.executeUpdate();

            System.out.println("Book borrowed successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void returnBook(String memberId, String isbn) {
        if (!memberExists(memberId)) {
            System.out.println("Member with ID " + memberId + " does not exist.");
            return;
        }
        if (!bookExists(isbn)) {
            System.out.println("Book with ISBN " + isbn + " does not exist.");
            return;
        }
        String updateBookSql = "UPDATE books SET is_borrowed = false WHERE isbn = ?";
        String deleteBorrowSql = "DELETE FROM borrow_transactions WHERE book_id = (SELECT id FROM books WHERE isbn = ?) AND member_id = (SELECT id FROM members WHERE member_id = ?)";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement updateBookPstmt = connection.prepareStatement(updateBookSql);
             PreparedStatement deleteBorrowPstmt = connection.prepareStatement(deleteBorrowSql)) {

            // Update the book's borrowed status
            updateBookPstmt.setString(1, isbn);
            updateBookPstmt.executeUpdate();

            // Delete the record from borrow_transactions
            deleteBorrowPstmt.setString(1, isbn);
            deleteBorrowPstmt.setString(2, memberId);
            deleteBorrowPstmt.executeUpdate();

            System.out.println("Book returned successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Member> getAllMembers() {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members";
        try (Connection connection = DBUtil.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String memberType = rs.getString("member_type");
                Member member;
                if ("student".equals(memberType)) {
                    member = new StudentMember(rs.getString("name"), rs.getString("member_id"));
                } else {
                    member = new TeacherMember(rs.getString("name"), rs.getString("member_id"));
                }
                System.out.println("Name: " + member.getName() + ", Member ID: " + member.getMemberId() + ", Type: " + memberType);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }
}