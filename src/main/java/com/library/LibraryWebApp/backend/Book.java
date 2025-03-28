package com.library.LibraryWebApp.backend;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Book {

    // Inside Book.java
    private String borrowedBy; // 🔑 Username of who borrowed the book

    public String getBorrowedBy() {
        return borrowedBy;
    }

    public void setBorrowedBy(String borrowedBy) {
        this.borrowedBy = borrowedBy;
    }


    @Id
    private String isbn;

    private String title;
    private String author;
    private boolean available = true;

    public Book() {}

    public Book(String isbn, String author, String title) {
        this.isbn = isbn;
        this.author = author;
        this.title = title;
        this.available = true;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
