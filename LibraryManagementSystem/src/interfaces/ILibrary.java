package interfaces;

import entities.Book;
import entities.Member;
import java.util.List;
import java.util.Date;

public interface ILibrary {
    void addBook(Book book);
    void removeBook(String isbn) throws Exception;
    List<Book> searchBooksByTitle(String title);
    List<Book> searchBooksByAuthor(String author);
    Book searchBooksByISBN(String isbn);
    List<Book> getAllAvailableBooks();
    List<Book> getAllBorrowedBooks();
    List<Book> getAllOverdueBooks();
    void addMember(Member member);
    void removeMember(String memberId) throws Exception;
    void updateMember(String memberId, String newName, String newMemberId);
    void borrowBook(String memberId, String isbn, Date dueDate);
    void returnBook(String memberId, String isbn);
    List<Member> getAllMembers();
}