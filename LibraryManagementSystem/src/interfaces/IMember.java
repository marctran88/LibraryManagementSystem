package interfaces;

import entities.Book;
import java.util.List;

public interface IMember {
    String getName();
    String getMemberId();
    List<Book> getBorrowedBooks();
    void borrowBook(Book book);
    void returnBook(Book book);
}