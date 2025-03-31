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

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.HashMap;

@Controller
public class LibraryController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    private BinaryTree tree;
    private HashMap<Book, Boolean> bookAvailability;

    @PostConstruct
    public void init() {
        // Add a default admin user
        if (!userRepository.existsById("admin")) {
            userRepository.save(new User("admin", "password"));
        }
    }

    @ModelAttribute
    public void addGlobalAttributes(HttpSession session, Model model) {
        String currentUser = (String) session.getAttribute("currentUser");
        if (currentUser != null) {
            model.addAttribute("username", currentUser);
        }
    }

    @GetMapping("/")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute("currentUser", username);
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

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        if (session.getAttribute("currentUser") == null) {
            return "redirect:/";
        }
        return "index";
    }

    @GetMapping("/books")
    public String showAllBooks(Model model) {
        model.addAttribute("books", bookRepository.findAll());
        return "books";
    }

    @GetMapping("/borrow")
    public String borrowForm(HttpSession session) {
        if (session.getAttribute("currentUser") == null) {
            return "redirect:/";
        }
        return "borrow";
    }

    @PostMapping("/borrow")
    public String borrowBook(@RequestParam String isbn, HttpSession session, Model model) {
        String currentUser = (String) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/";
        }

        Book book = bookRepository.findById(isbn).orElse(null);
        if (book == null) {
            model.addAttribute("message", "❌ Book not found with ISBN: " + isbn);
            model.addAttribute("username", currentUser);
            return "result";
        }

        if (book.getBorrowedBy() != null) {
            model.addAttribute("message", "❌ Book is already borrowed.");
            model.addAttribute("username", currentUser);
            return "result";
        }

        book.setBorrowedBy(currentUser);
        bookRepository.save(book);
        model.addAttribute("message", "✅ Book borrowed successfully!");
        model.addAttribute("username", currentUser);
        return "result";
    }


    @GetMapping("/return")
    public String returnForm(HttpSession session) {
        if (session.getAttribute("currentUser") == null) {
            return "redirect:/";
        }
        return "return";
    }

    @PostMapping("/return")
    public String returnBook(@RequestParam String isbn, HttpSession session, Model model) {
        String currentUser = (String) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/";
        }

        Book book = bookRepository.findById(isbn).orElse(null);
        if (book == null || !currentUser.equals(book.getBorrowedBy())) {
            model.addAttribute("message", "❌ You haven't borrowed this book.");
            model.addAttribute("username", currentUser);
            return "result";
        }

        book.setBorrowedBy(null);
        bookRepository.save(book);
        model.addAttribute("message", "✅ Book returned successfully!");
        model.addAttribute("username", currentUser);
        return "result";
    }


    @GetMapping("/mybooks")
    public String showMyBorrowedBooks(HttpSession session, Model model) {
        String currentUser = (String) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/";

        // 🧪 Test output
        bookRepository.findAll().forEach(book -> {
            System.out.println(book.getTitle() + " → Borrowed by: " + book.getBorrowedBy());
        });

        List<Book> borrowed = bookRepository.findAll()
                .stream()
                .filter(book -> currentUser.equals(book.getBorrowedBy()))
                .toList();

        model.addAttribute("books", borrowed);
        return "mybooks";
    }



    @GetMapping("/donate")
    public String donateForm(HttpSession session) {
        if (session.getAttribute("currentUser") == null) {
            return "redirect:/";
        }
        return "donate";
    }

    @PostMapping("/donate")
    public String donateBook(@RequestParam String isbn,
                             @RequestParam String title,
                             @RequestParam String author,
                             HttpSession session,
                             Model model) {
        String currentUser = (String) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/";
        }

        Book book = new Book(isbn, title, author);
        bookRepository.save(book);

        model.addAttribute("message", "📚 Book donated successfully! Thank you!");
        model.addAttribute("username", currentUser);
        return "result";
    }

}