

package br.com.notelab.backend.Controllers;

import br.com.notelab.backend.Model.User;
import br.com.notelab.backend.Services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
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
    public String home(Model model) {
        return "home";
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return service.getUsers();
    }
}
