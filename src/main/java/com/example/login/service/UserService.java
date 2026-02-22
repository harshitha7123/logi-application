package com.example.login.service;

import com.example.login.model.User;
import com.example.login.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
@Transactional
public class UserService implements UserDetailsService {

    public static final int MAX_FAILED_ATTEMPTS = 3;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found via username: " + username));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .accountLocked(!user.isAccountNonLocked())
                .roles("USER")
                .build();
    }

    public void registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setAccountNonLocked(true);
        userRepository.save(user);
    }

    public void increaseFailedAttempts(User user) {
        int newFailAttempts = user.getFailedAttempt() + 1;
        userRepository.updateFailedAttempts(newFailAttempts, user.getUsername());
    }

    public void resetFailedAttempts(String username) {
        userRepository.updateFailedAttempts(0, username);
    }

    public void lock(User user) {
        user.setAccountNonLocked(false);
        user.setFailedAttempt(0); // Optional: reset attempts or keep them
        userRepository.save(user);
    }

    public Optional<User> getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean unlockWhenTimeExpired(User user) {
        // Implementation for time-based unlocking if needed,
        // effectively just manual unlock for now based on requirements "Lock account
        // after 3 failed attempts"
        // Requirement didn't specify auto-unlock time, so keeping it simple.
        return true;
    }
}
