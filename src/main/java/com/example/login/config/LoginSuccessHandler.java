package com.example.login.config;

import com.example.login.model.User;
import com.example.login.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        String username = authentication.getName();
        Optional<User> user = userService.getByUsername(username);
        if (user.isPresent() && user.get().getFailedAttempt() > 0) {
            userService.resetFailedAttempts(username);
        }

        super.setDefaultTargetUrl("/home");
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
