package br.com.notelab.backend.Controllers;

import br.com.notelab.backend.Model.Matter;
import br.com.notelab.backend.Services.MatterService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/matter")
public class MatterController {

    private final MatterService service;

    public MatterController(MatterService service) {
        this.service = service;
    }

    // Exibe o formulário de criação
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("matter", new Matter());
        return "matter/create";
    }

    // Processa o envio do formulário (Thymeleaf)
    @PostMapping("/create")
    public String createMatter(@ModelAttribute Matter matter, Model model) {
        try {
            service.createMatter(matter);
            model.addAttribute("success", "Matéria criada com sucesso!");
            model.addAttribute("matter", new Matter());
        } catch (RuntimeException e) {
            model.addAttribute("error", "Erro ao criar matéria: " + e.getMessage());
            model.addAttribute("matter", matter);
        }
        return "matter/create";
    }

    // Lista todas as matérias (Thymeleaf)
    @GetMapping("/list")
    public String listMatters(Model model) {
        model.addAttribute("matters", service.listAllMatters());
        return "matter/list";
    }

    // Cria matéria via API REST (Postman)
    @PostMapping("/api/create")
    @ResponseBody
    public ResponseEntity<Matter> createMatterApi(@RequestBody Matter matter) {
        try {
            Matter created = service.createMatter(matter);
            return ResponseEntity.status(201).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}