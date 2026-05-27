package br.com.notelab.backend.Services;

import br.com.notelab.backend.Model.Note;
import br.com.notelab.backend.Repository.CadernoRepository;
import br.com.notelab.backend.Repository.NoteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final CadernoRepository cadernoRepository;

    public NoteService(NoteRepository noteRepository, CadernoRepository cadernoRepository) {
        this.noteRepository = noteRepository;
        this.cadernoRepository = cadernoRepository;
    }

    public Note createNote(Note note) {
        validateCreate(note);

        if (!cadernoRepository.existsById(note.getIdCaderno())) {
            throw new NoSuchElementException("Caderno nao encontrado");
        }

        note.setId(null);
        note.setName(note.getName().trim());
        note.setNoteContent(normalizeContent(note.getNoteContent()));
        note.setNoteEditDate(LocalDate.now());
        return noteRepository.save(note);
    }

    public Note createNoteForUser(Note note, Long userId) {
        validateCreate(note);

        if (userId == null) {
            throw new RuntimeException("userId e obrigatorio");
        }
        if (cadernoRepository.findByIdAndUserId(note.getIdCaderno(), userId).isEmpty()) {
            throw new NoSuchElementException("Caderno nao encontrado");
        }

        note.setId(null);
        note.setName(note.getName().trim());
        note.setNoteContent(normalizeContent(note.getNoteContent()));
        note.setNoteEditDate(LocalDate.now());
        return noteRepository.save(note);
    }

    public List<Note> listAllNotes() {
        return noteRepository.findAll();
    }

    public List<Note> listByCaderno(Long idCaderno) {
        if (idCaderno == null) {
            throw new RuntimeException("idCaderno e obrigatorio");
        }

        return noteRepository.findByIdCaderno(idCaderno);
    }

    public List<Note> listByCadernoForUser(Long idCaderno, Long userId) {
        if (idCaderno == null) {
            throw new RuntimeException("idCaderno e obrigatorio");
        }
        if (userId == null) {
            throw new RuntimeException("userId e obrigatorio");
        }
        if (cadernoRepository.findByIdAndUserId(idCaderno, userId).isEmpty()) {
            throw new NoSuchElementException("Caderno nao encontrado");
        }

        return noteRepository.findByIdCaderno(idCaderno);
    }

    public Note getNoteById(Long id) {
        if (id == null) {
            throw new RuntimeException("id e obrigatorio");
        }

        return noteRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Nota nao encontrada"));
    }

    public Note getNoteByIdForUser(Long id, Long userId) {
        if (id == null) {
            throw new RuntimeException("id e obrigatorio");
        }
        if (userId == null) {
            throw new RuntimeException("userId e obrigatorio");
        }

        Note note = getNoteById(id);
        if (cadernoRepository.findByIdAndUserId(note.getIdCaderno(), userId).isEmpty()) {
            throw new NoSuchElementException("Nota nao encontrada");
        }

        return note;
    }

    public Note updateNote(Long id, Note note) {
        if (id == null) {
            throw new RuntimeException("id e obrigatorio");
        }
        validateUpdate(note);

        Note existingNote = noteRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Nota nao encontrada"));

        existingNote.setName(note.getName().trim());
        existingNote.setNoteContent(normalizeContent(note.getNoteContent()));
        existingNote.setNoteEditDate(LocalDate.now());
        return noteRepository.save(existingNote);
    }

    public Note updateNoteForUser(Long id, Note note, Long userId) {
        if (id == null) {
            throw new RuntimeException("id e obrigatorio");
        }
        if (userId == null) {
            throw new RuntimeException("userId e obrigatorio");
        }
        validateUpdate(note);

        Note existingNote = getNoteByIdForUser(id, userId);
        existingNote.setName(note.getName().trim());
        existingNote.setNoteContent(normalizeContent(note.getNoteContent()));
        existingNote.setNoteEditDate(LocalDate.now());
        return noteRepository.save(existingNote);
    }

    public void deleteNote(Long id) {
        if (id == null) {
            throw new RuntimeException("id e obrigatorio");
        }
        if (!noteRepository.existsById(id)) {
            throw new NoSuchElementException("Nota nao encontrada");
        }

        noteRepository.deleteById(id);
    }

    public void deleteNoteForUser(Long id, Long userId) {
        Note note = getNoteByIdForUser(id, userId);
        noteRepository.delete(note);
    }

    private void validateCreate(Note note) {
        validateNote(note);
        if (note.getIdCaderno() == null) {
            throw new RuntimeException("idCaderno e obrigatorio");
        }
    }

    private void validateUpdate(Note note) {
        validateNote(note);
    }

    private void validateNote(Note note) {
        if (note == null) {
            throw new RuntimeException("Nota nao informada");
        }
        if (note.getName() == null || note.getName().isBlank()) {
            throw new RuntimeException("name e obrigatorio");
        }
    }

    private String normalizeContent(String content) {
        return content == null ? "" : content.trim();
    }
}
