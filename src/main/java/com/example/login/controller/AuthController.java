package com.example.login.controller;

import com.example.login.model.User;
import com.example.login.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "register";
        }

        // Check password length explicitly if validation annotation isn't enough or for
        // custom logic
        if (user.getPassword().length() < 8 || user.getPassword().length() > 20) {
            result.rejectValue("password", "error.user", "Password must be between 8 and 20 characters");
            return "register";
        }

        if (userService.getByUsername(user.getUsername()).isPresent()) {
            result.rejectValue("username", "error.user", "Username already exists");
            return "register";
        }

        if (user.getUsername().length() < 5 || user.getUsername().length() > 15) {
            result.rejectValue("username", "error.user", "Username must be between 5 and 15 characters");
            return "register";
        }

        userService.registerUser(user);
        return "redirect:/login?success";
    }
}
