package com.library.LibraryWebApp.repository;



import com.library.LibraryWebApp.backend.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, String> {

}

