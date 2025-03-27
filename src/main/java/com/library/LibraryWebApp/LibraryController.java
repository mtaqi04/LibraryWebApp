package com.library.LibraryWebApp;



import com.library.LibraryWebApp.backend.Book;
import com.library.LibraryWebApp.backend.BinaryTree;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Controller
public class LibraryController {

    private BinaryTree tree;
    private HashMap<Book, Boolean> bookAvailability;

    public LibraryController() {
        // Sample data
        Book book1 = new Book("9780134685991", "Joshua Bloch", "Effective Java");
        Book book2 = new Book("9780596009205", "Kathy Sierra", "Head First Java");
        Book book3 = new Book("9780132350884", "Robert C. Martin", "Clean Code");
        Book book4 = new Book("9780201633610", "Erich Gamma", "Design Patterns");
        Book book5 = new Book("9781492056355", "Brian Goetz", "Java Concurrency in Practice");

        tree = new BinaryTree(book1);
        tree.addNode(book2);
        tree.addNode(book3);
        tree.addNode(book4);
        tree.addNode(book5);

        bookAvailability = new HashMap<>();
        bookAvailability.put(book1, true);
        bookAvailability.put(book2, true);
        bookAvailability.put(book3, true);
        bookAvailability.put(book4, true);
        bookAvailability.put(book5, true);
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/borrow")
    public String showBorrowForm() {
        return "borrow";
    }

    @PostMapping("/borrow")
    public String borrowBook(@RequestParam String isbn, Model model) {
        Book book = tree.findNode(isbn);
        if (book != null && bookAvailability.getOrDefault(book, false)) {
            bookAvailability.put(book, false);
            model.addAttribute("message", "Book borrowed: " + book.getTitle());
        } else {
            model.addAttribute("message", "Book not found or unavailable.");
        }
        return "result";
    }
    @GetMapping("/return")
    public String showReturnForm() {
        return "return";
    }

    @PostMapping("/return")
    public String returnBook(@RequestParam String isbn, Model model) {
        Book book = tree.findNode(isbn);
        if (book != null && !bookAvailability.getOrDefault(book, true)) {
            bookAvailability.put(book, true);
            model.addAttribute("message", "Book returned: " + book.getTitle());
        } else {
            model.addAttribute("message", "Book not found or already returned.");
        }
        return "result";
    }

    @GetMapping("/donate")
    public String showDonateForm() {
        return "donate";
    }

    @PostMapping("/donate")
    public String donateBook(@RequestParam String isbn,
                             @RequestParam String author,
                             @RequestParam String title,
                             Model model) {
        Book book = new Book(isbn, author, title);
        tree.addNode(book);
        bookAvailability.put(book, true);
        model.addAttribute("message", "Thanks for donating: " + book.getTitle());
        return "result";
    }


}

