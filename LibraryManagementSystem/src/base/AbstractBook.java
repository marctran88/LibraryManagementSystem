package base;

import interfaces.IBook;

public abstract class AbstractBook implements IBook {
    protected String title;
    protected String author;
    protected String ISBN;
    protected boolean isBorrowed;

    public AbstractBook(String title, String author, String ISBN) {
        this.title = title;
        this.author = author;
        this.ISBN = ISBN;
        this.isBorrowed = false;
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getISBN() { return ISBN; }
    public boolean isBorrowed() { return isBorrowed; }
    public void setBorrowed(boolean borrowed) { isBorrowed = borrowed; }

    @Override
    public String toString() {
        return "Title: " + title + ", Author: " + author + ", ISBN: " + ISBN + ", Borrowed: " + isBorrowed;
    }
}