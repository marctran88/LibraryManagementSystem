package interfaces;

public interface IBook {
    String getTitle();
    String getAuthor();
    String getISBN();
    boolean isBorrowed();
    void setBorrowed(boolean borrowed);
}