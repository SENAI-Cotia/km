package br.com.notelab.backend.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import br.com.notelab.backend.Repository.UserRepository;
import org.springframework.http.RequestEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.notelab.backend.Model.User;

@Service
public class UserService {
    //List<User> users = new ArrayList<>();
    private UserRepository repository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public User userRegister(User user) {
        if (repository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        }

        String newPassword = encoder.encode(user.getPassword());
        user.setPassword(newPassword);
        return repository.save(user);
    }

    public User userLogin(String email, String password) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if(!encoder.matches(password, user.getPassword())) {
            throw  new RuntimeException("Senha inválida!");
        }

        return user;
    }

    public List<User> getUsers() {
        return repository.findAll();
    }
}
