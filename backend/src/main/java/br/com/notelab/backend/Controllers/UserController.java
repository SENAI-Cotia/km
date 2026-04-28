package br.com.notelab.backend.Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import br.com.notelab.backend.Model.User;
import br.com.notelab.backend.Services.UserService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping("/")
public class UserController {
    private UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<?> userRegister(@RequestBody User user) {
       try {
           User newUser = service.userRegister(user);
           return ResponseEntity.ok(newUser);
       } catch (RuntimeException e) {
           return ResponseEntity.badRequest().body(e.getMessage());
       }
    }

   // @PostMapping("/login")
    //public ResponseEntity<?> userLogin(@RequestBody User user) {
      //  try {
        //    User login = service.userLogin(
//                    user.getEmail(),
//                    user.getPassword()
//            );
//            return ResponseEntity.ok(login);
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }

    @GetMapping("/login")
    public String paginaDeLogin(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }


    @PostMapping("/logar")
    public String userLogin(User user, Model model) {
        try {
            User login = service.userLogin(user.getEmail(), user.getPassword());
            model.addAttribute("user", login);
            return "index";
        } catch (RuntimeException e) {
            model.addAttribute("erro", e.getMessage());
            return "home";
        }
    }


    @GetMapping("/users")
    public List<User> getUsers() {
        return service.getUsers();
    }
}
