//package br.com.notelab.backend.Services;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import br.com.notelab.backend.Repository.UserRepository;
//import org.springframework.http.RequestEntity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import br.com.notelab.backend.Model.User;
//
//@Service
//public class UserService {
//    private UserRepository repository;
//    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//
//    public UserService(UserRepository repository) {
//        this.repository = repository;
//    }
//
//    public User userRegister(User user) {
//        if (repository.findByEmail(user.getEmail()).isPresent()) {
//            throw new RuntimeException("Email já cadastrado");
//        }
//
//        String newPassword = encoder.encode(user.getPassword());
//        user.setPassword(newPassword);
//        return repository.save(user);
//    }
//
//    public User userLogin(String email, String password) {
//        User user = repository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
//
//       if(!encoder.matches(password, user.getPassword())) {
//            throw  new RuntimeException("Senha inválida!");
//        }
//
//        return user;
//    }
//
//    public List<User> getUsers() {
//        return repository.findAll();
//    }
//}
//

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