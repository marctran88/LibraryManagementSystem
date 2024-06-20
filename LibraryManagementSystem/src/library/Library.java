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

public class Library implements ILibrary {
    public void addBook(Book book) {
        String sql = "INSERT INTO books (title, author, isbn, is_borrowed) VALUES (?, ?, ?, ?)";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getISBN());
            pstmt.setBoolean(4, book.isBorrowed());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeBook(String ISBN) {
        String sql = "DELETE FROM books WHERE isbn = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, ISBN);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addMember(Member member) {
        String sql = "INSERT INTO members (name, member_id, type) VALUES (?, ?, ?)";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, member.getName());
            pstmt.setString(2, member.getMemberId());
            pstmt.setString(3, member instanceof StudentMember ? "Student" : "Teacher");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeMember(String memberId) {
        String sql = "DELETE FROM members WHERE member_id = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, memberId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void borrowBook(String memberId, String ISBN) {
        String findBookSql = "SELECT * FROM books WHERE isbn = ?";
        String findMemberSql = "SELECT * FROM members WHERE member_id = ?";
        String borrowBookSql = "INSERT INTO transactions (member_id, book_id) VALUES (?, ?)";
        String updateBookSql = "UPDATE books SET is_borrowed = true WHERE id = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement findBookStmt = connection.prepareStatement(findBookSql);
             PreparedStatement findMemberStmt = connection.prepareStatement(findMemberSql);
             PreparedStatement borrowBookStmt = connection.prepareStatement(borrowBookSql);
             PreparedStatement updateBookStmt = connection.prepareStatement(updateBookSql)) {

            findBookStmt.setString(1, ISBN);
            ResultSet bookRs = findBookStmt.executeQuery();
            if (!bookRs.next()) {
                System.out.println("Book not found.");
                return;
            }
            int bookId = bookRs.getInt("id");
            boolean isBorrowed = bookRs.getBoolean("is_borrowed");
            if (isBorrowed) {
                System.out.println("Book is already borrowed.");
                return;
            }

            findMemberStmt.setString(1, memberId);
            ResultSet memberRs = findMemberStmt.executeQuery();
            if (!memberRs.next()) {
                System.out.println("Member not found.");
                return;
            }
            int memberIdInt = memberRs.getInt("id");

            borrowBookStmt.setInt(1, memberIdInt);
            borrowBookStmt.setInt(2, bookId);
            borrowBookStmt.executeUpdate();

            updateBookStmt.setInt(1, bookId);
            updateBookStmt.executeUpdate();

            System.out.println("Book borrowed successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void returnBook(String memberId, String ISBN) {
        String findBookSql = "SELECT * FROM books WHERE isbn = ?";
        String findMemberSql = "SELECT * FROM members WHERE member_id = ?";
        String returnBookSql = "UPDATE transactions SET return_date = CURRENT_TIMESTAMP WHERE member_id = ? AND book_id = ? AND return_date IS NULL";
        String updateBookSql = "UPDATE books SET is_borrowed = false WHERE id = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement findBookStmt = connection.prepareStatement(findBookSql);
             PreparedStatement findMemberStmt = connection.prepareStatement(findMemberSql);
             PreparedStatement returnBookStmt = connection.prepareStatement(returnBookSql);
             PreparedStatement updateBookStmt = connection.prepareStatement(updateBookSql)) {
    
            findBookStmt.setString(1, ISBN);
            ResultSet bookRs = findBookStmt.executeQuery();
            if (!bookRs.next()) {
                System.out.println("Book not found.");
                return;
            }
            int bookId = bookRs.getInt("id");
    
            findMemberStmt.setString(1, memberId);
            ResultSet memberRs = findMemberStmt.executeQuery();
            if (!memberRs.next()) {
                System.out.println("Member not found.");
                return;
            }
            int memberIdInt = memberRs.getInt("id");
    
            returnBookStmt.setInt(1, memberIdInt);
            returnBookStmt.setInt(2, bookId);
            int updatedRows = returnBookStmt.executeUpdate();
            if (updatedRows == 0) {
                System.out.println("No active borrowing found for this book and member.");
                return;
            }
    
            updateBookStmt.setInt(1, bookId);
            updateBookStmt.executeUpdate();
    
            System.out.println("Book returned successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";
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
    
    public List<Member> getAllMembers() {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members";
        try (Connection connection = DBUtil.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Member member;
                String type = rs.getString("type");
                if (type.equals("Student")) {
                    member = new StudentMember(rs.getString("name"), rs.getString("member_id"));
                } else {
                    member = new TeacherMember(rs.getString("name"), rs.getString("member_id"));
                }
                members.add(member);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }
    }