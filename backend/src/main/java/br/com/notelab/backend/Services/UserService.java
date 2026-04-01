package br.com.notelab.backend.Services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.notelab.backend.Model.User;

@Service
public class UserService {
    List<User> users = new ArrayList<>();

    public User userRegister(User user) {
        users.add(user);
        return user;
    }

    public List<User> getUsers() {
        return users;
    }
}
