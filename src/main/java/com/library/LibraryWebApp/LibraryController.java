package com.library.LibraryWebApp;


import com.library.LibraryWebApp.backend.Book;
import com.library.LibraryWebApp.backend.BinaryTree;
import com.library.LibraryWebApp.backend.User;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Controller
public class LibraryController {

    // 🔐 User login state & accounts
    private final HashMap<String, String> users = new HashMap<>();
    private boolean loggedIn = false;

    // 📚 Book data
    private BinaryTree tree;
    private HashMap<Book, Boolean> bookAvailability;
    private String currentUser = "";


    // 👤 Add a default user
    @PostConstruct
    public void init() {
        users.put("admin", "password");
    }

    // 🔐 Login + Signup

    @GetMapping("/")
    public String showLoginPage(Model model) {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        Model model) {
        String storedPassword = users.get(username);
        if (storedPassword != null && storedPassword.equals(password)) {
            loggedIn = true;
            currentUser = username;
            setupBooks();  // ← Initialize book data only after login
            return "redirect:/home";
        } else {
            model.addAttribute("error", "Invalid username or password.");
            return "login";
        }
    }

    @GetMapping("/signup")
    public String showSignupPage() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam String username,
                         @RequestParam String password,
                         Model model) {
        if (users.containsKey(username)) {
            model.addAttribute("error", "Username already exists.");
            return "signup";
        }
        users.put(username, password);
        model.addAttribute("success", "Account created! Please log in.");
        return "login";
    }

    @GetMapping("/home")
    public String home(Model model) {
        if (!loggedIn) return "redirect:/";
        model.addAttribute("username", currentUser);
        return "index";
    }


    // 📚 Book Routes

    @GetMapping("/logout")
    public String logout() {
        loggedIn = false;
        return "redirect:/";
    }


    @GetMapping("/borrow")
    public String showBorrowForm(Model model) {
        if (!loggedIn) return "redirect:/";
        model.addAttribute("username", currentUser);
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
        model.addAttribute("username", currentUser);
        return "result";
    }

    @GetMapping("/return")
    public String showReturnForm(Model model) {
        if (!loggedIn) return "redirect:/";
        model.addAttribute("username", currentUser);
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
        model.addAttribute("username", currentUser);
        return "result";
    }

    @GetMapping("/donate")
    public String showDonateForm(Model model) {
        if (!loggedIn) return "redirect:/";
        model.addAttribute("username", currentUser);
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
        model.addAttribute("username", currentUser);
        return "result";
    }

    // 📚 Set up books (called after login)
    private void setupBooks() {
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
}

