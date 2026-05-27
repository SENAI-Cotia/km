package br.com.notelab.backend.Services;

import java.util.List;
import java.util.Optional;

import br.com.notelab.backend.Repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.notelab.backend.Model.User;

@Service
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder encoder;

    public UserService(UserRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    public User userRegister(User user) {
        if (repository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        }
        user.setPassword(encoder.encode(user.getPassword()));
        return repository.save(user);
    }

    public List<User> getUsers() {
        return repository.findAll();
    }
}