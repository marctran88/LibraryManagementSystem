package interfaces;

import entities.Book;
import entities.Member;
import java.util.List;

public interface ILibrary {
    void addBook(Book book);
    void removeBook(String ISBN);
    void addMember(Member member);
    void removeMember(String memberId);
    void borrowBook(String memberId, String ISBN);
    void returnBook(String memberId, String ISBN);
    List<Book> getAllBooks();
    List<Member> getAllMembers();
}