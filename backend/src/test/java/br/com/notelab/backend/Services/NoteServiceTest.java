package br.com.notelab.backend.Services;

import br.com.notelab.backend.Model.Note;
import br.com.notelab.backend.Repository.CadernoRepository;
import br.com.notelab.backend.Repository.NoteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private CadernoRepository cadernoRepository;

    @InjectMocks
    private NoteService noteService;

    @Test
    void createNoteValidatesCadernoAndSavesWithCurrentDate() {
        Note saved = new Note(1L, 2L, "Aula 1", "Conteudo", LocalDate.now());
        when(cadernoRepository.existsById(2L)).thenReturn(true);
        when(noteRepository.save(any(Note.class))).thenReturn(saved);

        Note result = noteService.createNote(new Note(99L, 2L, " Aula 1 ", " Conteudo ", null));

        ArgumentCaptor<Note> captor = ArgumentCaptor.forClass(Note.class);
        verify(noteRepository).save(captor.capture());

        assertEquals(saved, result);
        assertNull(captor.getValue().getId());
        assertEquals(2L, captor.getValue().getIdCaderno());
        assertEquals("Aula 1", captor.getValue().getName());
        assertEquals("Conteudo", captor.getValue().getNoteContent());
        assertNotNull(captor.getValue().getNoteEditDate());
    }

    @Test
    void createNoteRejectsMissingIdCaderno() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> noteService.createNote(new Note(null, null, "Aula 1", "Conteudo", null)));

        assertEquals("idCaderno e obrigatorio", exception.getMessage());
        verify(cadernoRepository, never()).existsById(any());
    }

    @Test
    void createNoteThrowsWhenCadernoDoesNotExist() {
        when(cadernoRepository.existsById(99L)).thenReturn(false);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> noteService.createNote(new Note(null, 99L, "Aula 1", "Conteudo", null)));

        assertEquals("Caderno nao encontrado", exception.getMessage());
        verify(noteRepository, never()).save(any());
    }

    @Test
    void updateNoteChangesOnlyNameAndContent() {
        Note existing = new Note(1L, 2L, "Aula 1", "Conteudo", LocalDate.now());
        Note saved = new Note(1L, 2L, "Aula 2", "Novo conteudo", LocalDate.now());
        when(noteRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(noteRepository.save(any(Note.class))).thenReturn(saved);

        Note result = noteService.updateNote(1L, new Note(null, 99L, " Aula 2 ", " Novo conteudo ", null));

        ArgumentCaptor<Note> captor = ArgumentCaptor.forClass(Note.class);
        verify(noteRepository).save(captor.capture());

        assertEquals(saved, result);
        assertEquals(1L, captor.getValue().getId());
        assertEquals(2L, captor.getValue().getIdCaderno());
        assertEquals("Aula 2", captor.getValue().getName());
        assertEquals("Novo conteudo", captor.getValue().getNoteContent());
    }

    @Test
    void listByCadernoUsesRepositoryFilter() {
        List<Note> notes = List.of(new Note(1L, 2L, "Aula 1", "Conteudo", LocalDate.now()));
        when(noteRepository.findByIdCaderno(2L)).thenReturn(notes);

        List<Note> result = noteService.listByCaderno(2L);

        assertEquals(notes, result);
        verify(noteRepository).findByIdCaderno(2L);
    }

    @Test
    void deleteNoteDeletesWhenNoteExists() {
        when(noteRepository.existsById(1L)).thenReturn(true);
        doNothing().when(noteRepository).deleteById(1L);

        noteService.deleteNote(1L);

        verify(noteRepository).deleteById(1L);
    }

    @Test
    void deleteNoteThrowsWhenNoteDoesNotExist() {
        when(noteRepository.existsById(99L)).thenReturn(false);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> noteService.deleteNote(99L));

        assertEquals("Nota nao encontrada", exception.getMessage());
        verify(noteRepository, never()).deleteById(99L);
    }
}
