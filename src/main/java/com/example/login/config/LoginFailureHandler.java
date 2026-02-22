package com.example.login.config;

import com.example.login.model.User;
import com.example.login.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

        String username = request.getParameter("username");
        Optional<User> userOptional = userService.getByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (user.isAccountNonLocked()) {
                if (user.getFailedAttempt() < UserService.MAX_FAILED_ATTEMPTS - 1) {
                    userService.increaseFailedAttempts(user);
                } else {
                    userService.lock(user);
                    exception = new LockedException("Your account has been locked due to 3 failed attempts."
                            + " It will remain locked until administrator unlocks it.");
                }
            } else if (!user.isAccountNonLocked()) {
                /*
                 * if (userService.unlockWhenTimeExpired(user)) {
                 * exception = new
                 * LockedException("Your account has been unlocked. Please try to login again."
                 * );
                 * }
                 */
            }
        }

        super.setDefaultFailureUrl("/login?error");
        super.onAuthenticationFailure(request, response, exception);
    }
}
