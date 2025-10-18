package com.doorserve.security;

import com.doorserve.model.User;
import com.doorserve.model.UserType;
import com.doorserve.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    
    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException {
        try {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            String userEmail = oAuth2User.getAttribute("email");
            log.info("OAuth2 authentication successful for user: " + userEmail);
            
            User user = processOAuthUser(oAuth2User);
            String token = jwtService.generateToken(user.getEmail());

            String redirectUrl = UriComponentsBuilder.fromUriString(frontendUrl)
                    .queryParam("token", token)
                    .queryParam("userType", user.getUserType())
                    .build().toUriString();

            log.info("Redirecting to: " + redirectUrl);
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
        } catch (Exception e) {
            log.error("Error during OAuth2 authentication success handling", e);
            String errorUrl = UriComponentsBuilder.fromUriString(frontendUrl)
                    .queryParam("error", "authentication_failed")
                    .build().toUriString();
            getRedirectStrategy().sendRedirect(request, response, errorUrl);
        }
    }

    private User processOAuthUser(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        log.debug("Processing OAuth user with email: " + email);
        
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("Email not provided by OAuth2 provider");
        }
        
        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            log.info("Existing user found: " + email);
            return existingUser.get();
        }

        log.info("Creating new user: " + email);
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setFirstName(oAuth2User.getAttribute("given_name"));
        newUser.setLastName(oAuth2User.getAttribute("family_name"));
        newUser.setUserType(UserType.CUSTOMER); // Default to customer, can be updated later
        newUser.setPassword(""); // OAuth users don't have a password

        return userRepository.save(newUser);
    }
}
