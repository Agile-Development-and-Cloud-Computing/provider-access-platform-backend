package com.fuas.providers_access_platform.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.fuas.providers_access_platform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    User findByUsername(String username);
    boolean existsByUsername(String Username);
    boolean existsByEmail(String email);

}
