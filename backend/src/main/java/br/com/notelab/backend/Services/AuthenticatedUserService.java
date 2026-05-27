package br.com.notelab.backend.Services;

import br.com.notelab.backend.Model.User;
import br.com.notelab.backend.Repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class AuthenticatedUserService {

    private final UserRepository userRepository;

    public AuthenticatedUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getAuthenticatedUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            throw new RuntimeException("Usuario nao autenticado");
        }

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NoSuchElementException("Usuario autenticado nao encontrado"));
    }

    public Long getAuthenticatedUserId(Authentication authentication) {
        return getAuthenticatedUser(authentication).getId();
    }
}
