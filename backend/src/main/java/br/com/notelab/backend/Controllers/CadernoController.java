package br.com.notelab.backend.Controllers;

import br.com.notelab.backend.Model.Caderno;
import br.com.notelab.backend.Services.AuthenticatedUserService;
import br.com.notelab.backend.Services.CadernoService;
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

import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/caderno")
public class CadernoController {

    private final CadernoService service;
    private final AuthenticatedUserService authenticatedUserService;

    public CadernoController(CadernoService service, AuthenticatedUserService authenticatedUserService) {
        this.service = service;
        this.authenticatedUserService = authenticatedUserService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createCaderno(@RequestBody Caderno caderno, Authentication authentication) {
        try {
            Caderno created = authenticatedUserService == null || authentication == null
                    ? service.createCaderno(caderno)
                    : service.createCadernoForUser(caderno, authenticatedUserService.getAuthenticatedUserId(authentication));
            return ResponseEntity.status(201).body(created);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> listByUser(@PathVariable Long userId, Authentication authentication) {
        try {
            if (authenticatedUserService != null && authentication != null) {
                Long authenticatedUserId = authenticatedUserService.getAuthenticatedUserId(authentication);
                if (!authenticatedUserId.equals(userId)) {
                    return ResponseEntity.status(403).body(Map.of("error", "Acesso negado"));
                }
            }

            return ResponseEntity.ok(service.listByUser(userId));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/user/me")
    public ResponseEntity<?> listMyCadernos(Authentication authentication) {
        try {
            Long userId = authenticatedUserService.getAuthenticatedUserId(authentication);
            return ResponseEntity.ok(service.listByUser(userId));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/matter/{matterId}")
    public ResponseEntity<?> listByMatter(@PathVariable Long matterId, Authentication authentication) {
        try {
            if (authenticatedUserService == null || authentication == null) {
                return ResponseEntity.ok(service.listByMatter(matterId));
            }

            Long userId = authenticatedUserService.getAuthenticatedUserId(authentication);
            return ResponseEntity.ok(service.listByMatterAndUser(matterId, userId));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
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
    public ResponseEntity<?> deleteCaderno(@PathVariable Long id, Authentication authentication) {
        try {
            if (authenticatedUserService == null || authentication == null) {
                service.deleteCaderno(id);
            } else {
                service.deleteCadernoForUser(id, authenticatedUserService.getAuthenticatedUserId(authentication));
            }
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
