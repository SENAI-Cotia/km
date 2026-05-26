package br.com.notelab.backend.Controllers;

import br.com.notelab.backend.Model.Matter;
import br.com.notelab.backend.Services.AuthenticatedUserService;
import br.com.notelab.backend.Services.MatterService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
@RequestMapping("/matter")
public class MatterController {

    private final MatterService service;
    private final AuthenticatedUserService authenticatedUserService;

    public MatterController(MatterService service, AuthenticatedUserService authenticatedUserService) {
        this.service = service;
        this.authenticatedUserService = authenticatedUserService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createMatter(@RequestBody Matter matter, Authentication authentication) {
        try {
            Matter created = authenticatedUserService == null || authentication == null
                    ? service.createMatter(matter)
                    : service.createMatterForUser(matter, authenticatedUserService.getAuthenticatedUserId(authentication));
            return ResponseEntity.status(201).body(created);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<Matter>> listMatters() {
        return ResponseEntity.ok(service.listAllMatters());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getMattersByUserId(@PathVariable Long userId, Authentication authentication) {
        try {
            if (authenticatedUserService != null && authentication != null) {
                Long authenticatedUserId = authenticatedUserService.getAuthenticatedUserId(authentication);
                if (!authenticatedUserId.equals(userId)) {
                    return ResponseEntity.status(403).body(Map.of("error", "Acesso negado"));
                }
            }

            return ResponseEntity.ok(service.getMattersByUserId(userId));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/user/me")
    public ResponseEntity<?> getMyMatters(Authentication authentication) {
        try {
            Long userId = authenticatedUserService.getAuthenticatedUserId(authentication);
            return ResponseEntity.ok(service.getMattersByUserId(userId));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMatter(@PathVariable Long id, @RequestBody Matter matter) {
        try {
            Matter updated = service.updateMatter(id, matter);
            return ResponseEntity.ok(updated);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMatter(@PathVariable Long id, Authentication authentication) {
        try {
            if (authenticatedUserService == null || authentication == null) {
                service.deleteMatter(id);
            } else {
                service.deleteMatterForUser(id, authenticatedUserService.getAuthenticatedUserId(authentication));
            }
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/api/create")
    public ResponseEntity<?> createMatterApi(@RequestBody Matter matter, Authentication authentication) {
        return createMatter(matter, authentication);
    }
}
