package br.com.notelab.backend.Controllers;

import org.springframework.stereotype.Controller;

import br.com.notelab.backend.Model.User;
import br.com.notelab.backend.Services.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@Controller
public class UserController {
    private UserService users;

    public UserController(UserService users) {
        this.users = users;
    }

    @PostMapping("/register")
    public User userRegister(@RequestBody User user) {
        users.userRegister(user);
        return user;
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return users.getUsers();
    }
}
