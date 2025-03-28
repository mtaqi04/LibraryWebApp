package com.library.LibraryWebApp;


import com.library.LibraryWebApp.backend.Book;
import com.library.LibraryWebApp.backend.BinaryTree;
import com.library.LibraryWebApp.backend.User;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.library.LibraryWebApp.repository.UserRepository;
import com.library.LibraryWebApp.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;


import java.util.HashMap;

@Controller
public class LibraryController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;


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
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            loggedIn = true;
            currentUser = username;
            return "redirect:/home";
        }
        model.addAttribute("error", "Invalid username or password.");
        return "login";
    }

    @GetMapping("/signup")
    public String showSignupPage() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam String username,
                         @RequestParam String password,
                         Model model) {
        if (userRepository.existsById(username)) {
            model.addAttribute("error", "Username already exists.");
            return "signup";
        }
        userRepository.save(new User(username, password));
        model.addAttribute("success", "Account created! Please log in.");
        return "login";
    }

    @GetMapping("/home")
    public String home(Model model) {
        if (!loggedIn) return "redirect:/";
        model.addAttribute("username", currentUser);
        return "index";
    }

    @GetMapping("/books")
    public String showAllBooks(Model model) {
        model.addAttribute("books", bookRepository.findAll());
        model.addAttribute("username", currentUser); // optional
        return "books";
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
        Book book = bookRepository.findById(isbn).orElse(null);
        if (book != null && book.isAvailable()) {
            book.setAvailable(false);
            book.setBorrowedBy(currentUser); // ✅ Save who borrowed it
            bookRepository.save(book);
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
        Book book = bookRepository.findById(isbn).orElse(null);
        if (book != null && !book.isAvailable()) {
            book.setAvailable(true);
            book.setBorrowedBy(null); // ✅ Remove borrower
            bookRepository.save(book);
            model.addAttribute("message", "Book returned: " + book.getTitle());
        } else {
            model.addAttribute("message", "Book not found or already returned.");
        }
        model.addAttribute("username", currentUser);
        return "result";
    }

    @GetMapping("/mybooks")
    public String showMyBorrowedBooks(Model model) {
        List<Book> borrowed = bookRepository.findAll()
                .stream()
                .filter(book -> currentUser.equals(book.getBorrowedBy()))
                .toList();

        model.addAttribute("books", borrowed);
        model.addAttribute("username", currentUser);
        return "mybooks";
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
        book.setAvailable(true);
        bookRepository.save(book);
        model.addAttribute("message", "Thanks for donating: " + title);
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

