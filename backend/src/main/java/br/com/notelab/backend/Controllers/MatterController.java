package br.com.notelab.backend.Controllers;

import br.com.notelab.backend.Model.Matter;
import br.com.notelab.backend.Model.User;
import br.com.notelab.backend.Services.MatterService;
import br.com.notelab.backend.Services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/matter")
public class MatterController {
    private MatterService service;

    public MatterController(MatterService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public Matter createMatter(@RequestBody Matter matter, Model model) {
        try {
            return service.createMatter(matter);

        } catch (RuntimeException e) {
            model.addAttribute("Erro ao criar matéria!", e.getMessage());
            return matter;
        }
    }

}
