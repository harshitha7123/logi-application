package com.example.login.repository;

import com.example.login.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Query("UPDATE User u SET u.failedAttempt = ?1 WHERE u.username = ?2")
    @Modifying
    @Transactional
    public void updateFailedAttempts(int failAttempts, String username);

}
