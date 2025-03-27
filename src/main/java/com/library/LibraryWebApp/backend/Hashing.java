package com.library.LibraryWebApp.backend;

import java.util.HashMap;

class Hashing {
    private HashMap<String, Boolean> bookAvailability = new HashMap<>();

    public void addBook(String isbn, boolean available) {
        bookAvailability.put(isbn, available);
    }

    public boolean isAvailable(String isbn) {
        return bookAvailability.getOrDefault(isbn, false);
    }

    public void borrowBook(String isbn) {
        if (bookAvailability.containsKey(isbn) && bookAvailability.get(isbn)) {
            bookAvailability.put(isbn, false);
            System.out.println("Book borrowed successfully.");
        } else {
            System.out.println("Book not available.");
        }
    }

    public void returnBook(String isbn) {
        if (bookAvailability.containsKey(isbn)) {
            bookAvailability.put(isbn, true);
            System.out.println("Book returned successfully.");
        }
    }
}
