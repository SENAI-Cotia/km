package br.com.notelab.backend.Controllers;

import br.com.notelab.backend.Model.Note;
import br.com.notelab.backend.Services.AuthenticatedUserService;
import br.com.notelab.backend.Services.NoteService;
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
@RequestMapping("/note")
public class NoteController {

    private final NoteService noteService;
    private final AuthenticatedUserService authenticatedUserService;

    public NoteController(NoteService noteService, AuthenticatedUserService authenticatedUserService) {
        this.noteService = noteService;
        this.authenticatedUserService = authenticatedUserService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createNote(@RequestBody Note note, Authentication authentication) {
        try {
            Note created = authenticatedUserService == null || authentication == null
                    ? noteService.createNote(note)
                    : noteService.createNoteForUser(note, authenticatedUserService.getAuthenticatedUserId(authentication));
            return ResponseEntity.status(201).body(created);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<Note>> listAllNotes() {
        return ResponseEntity.ok(noteService.listAllNotes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getNoteById(@PathVariable Long id, Authentication authentication) {
        try {
            Note note = authenticatedUserService == null || authentication == null
                    ? noteService.getNoteById(id)
                    : noteService.getNoteByIdForUser(id, authenticatedUserService.getAuthenticatedUserId(authentication));
            return ResponseEntity.ok(note);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/caderno/{idCaderno}")
    public ResponseEntity<?> listByCaderno(@PathVariable Long idCaderno, Authentication authentication) {
        try {
            List<Note> notes = authenticatedUserService == null || authentication == null
                    ? noteService.listByCaderno(idCaderno)
                    : noteService.listByCadernoForUser(idCaderno, authenticatedUserService.getAuthenticatedUserId(authentication));
            return ResponseEntity.ok(notes);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateNote(@PathVariable Long id, @RequestBody Note note, Authentication authentication) {
        try {
            Note updated = authenticatedUserService == null || authentication == null
                    ? noteService.updateNote(id, note)
                    : noteService.updateNoteForUser(id, note, authenticatedUserService.getAuthenticatedUserId(authentication));
            return ResponseEntity.ok(updated);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNote(@PathVariable Long id, Authentication authentication) {
        try {
            if (authenticatedUserService == null || authentication == null) {
                noteService.deleteNote(id);
            } else {
                noteService.deleteNoteForUser(id, authenticatedUserService.getAuthenticatedUserId(authentication));
            }
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
