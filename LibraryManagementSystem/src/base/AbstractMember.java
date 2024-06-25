package base;

import interfaces.IMember;
import entities.Book;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMember implements IMember {
    protected String name;
    protected String memberId;
    protected List<Book> borrowedBooks;

    public AbstractMember(String name, String memberId) {
        this.name = name;
        this.memberId = memberId;
        this.borrowedBooks = new ArrayList<>();
    }

    public String getName() { return name; }
    public String getMemberId() { return memberId; }
    public List<Book> getBorrowedBooks() { return borrowedBooks; }

    public void borrowBook(Book book) {
        borrowedBooks.add(book);
        book.setBorrowed(true);
    }

    public void returnBook(Book book) {
        borrowedBooks.remove(book);
        book.setBorrowed(false);
    }

    @Override
    public String toString() {
        return "Name: " + name + ", Member ID: " + memberId;
    }
}