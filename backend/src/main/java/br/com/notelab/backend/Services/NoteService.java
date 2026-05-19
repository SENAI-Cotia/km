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
        note.setNoteContent(note.getNoteContent().trim());
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

    public Note getNoteById(Long id) {
        if (id == null) {
            throw new RuntimeException("id e obrigatorio");
        }

        return noteRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Nota nao encontrada"));
    }

    public Note updateNote(Long id, Note note) {
        if (id == null) {
            throw new RuntimeException("id e obrigatorio");
        }
        validateUpdate(note);

        Note existingNote = noteRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Nota nao encontrada"));

        existingNote.setName(note.getName().trim());
        existingNote.setNoteContent(note.getNoteContent().trim());
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
        if (note.getNoteContent() == null || note.getNoteContent().isBlank()) {
            throw new RuntimeException("noteContent e obrigatorio");
        }
    }
}
