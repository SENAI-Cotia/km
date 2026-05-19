package br.com.notelab.backend.Controllers;

import br.com.notelab.backend.Model.Caderno;
import br.com.notelab.backend.Services.CadernoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/caderno")
public class CadernoController {

    private final CadernoService service;

    public CadernoController(CadernoService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createCaderno(@RequestBody Caderno caderno) {
        try {
            Caderno created = service.createCaderno(caderno);
            return ResponseEntity.status(201).body(created);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Caderno>> listByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(service.listByUser(userId));
    }

    @GetMapping("/matter/{matterId}")
    public ResponseEntity<List<Caderno>> listByMatter(@PathVariable Long matterId) {
        return ResponseEntity.ok(service.listByMatter(matterId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCaderno(@PathVariable Long id, @RequestBody Caderno caderno) {
        try {
            Caderno updated = service.updateCaderno(id, caderno);
            return ResponseEntity.ok(updated);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCaderno(@PathVariable Long id) {
        try {
            service.deleteCaderno(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }
}
