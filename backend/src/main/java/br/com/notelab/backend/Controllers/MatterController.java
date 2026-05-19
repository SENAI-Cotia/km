package br.com.notelab.backend.Controllers;

import br.com.notelab.backend.Model.Matter;
import br.com.notelab.backend.Services.MatterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/matter")
public class MatterController {

    private final MatterService service;

    public MatterController(MatterService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createMatter(@RequestBody Matter matter) {
        try {
            Matter created = service.createMatter(matter);
            return ResponseEntity.status(201).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<Matter>> listMatters() {
        return ResponseEntity.ok(service.listAllMatters());
    }

    @PostMapping("/api/create")
    public ResponseEntity<?> createMatterApi(@RequestBody Matter matter) {
        return createMatter(matter);
    }
}
