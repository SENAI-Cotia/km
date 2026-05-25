

package br.com.notelab.backend.Controllers;

import br.com.notelab.backend.Model.User;
import br.com.notelab.backend.Repository.UserRepository;
import br.com.notelab.backend.Services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/")
public class UserController {

    private final UserService service;
    private final UserRepository userRepository;

    public UserController(UserService service, UserRepository userRepository) {
        this.service = service;
        this.userRepository = userRepository;
    }

    @GetMapping("/register")
    public String paginaDeCadastro(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/registrar")
    public String userRegister(@ModelAttribute User user, Model model) {
        try {
            if (!user.getPassword().equals(user.getConfirmPassword())) {
                throw new RuntimeException("Senhas não conferem");
            }
            service.userRegister(user);
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("erro", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/login")
    public String paginaDeLogin(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @GetMapping("/home")
    public String home(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            userRepository.findByEmail(email)
                    .map(User::getName)
                    .ifPresent(username -> model.addAttribute("username", username));
        }

        return "home";
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return service.getUsers();
    }
}
