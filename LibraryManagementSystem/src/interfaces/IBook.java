package interfaces;

import java.util.Date;

public interface IBook {
    String getTitle();
    String getAuthor();
    String getISBN();
    boolean isBorrowed();
    void setBorrowed(boolean borrowed);
    Date getDueDate();
    void setDueDate(Date dueDate);
}