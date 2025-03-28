package com.library.LibraryWebApp.repository;



import com.library.LibraryWebApp.backend.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    User findByUsername(String username);
}
