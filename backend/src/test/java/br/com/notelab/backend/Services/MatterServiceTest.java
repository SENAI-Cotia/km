package br.com.notelab.backend.Services;

import br.com.notelab.backend.Model.Matter;
import br.com.notelab.backend.Repository.CadernoRepository;
import br.com.notelab.backend.Repository.MatterRepository;
import br.com.notelab.backend.Repository.NoteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MatterServiceTest {

    @Mock
    private MatterRepository repository;

    @Mock
    private CadernoRepository cadernoRepository;

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private MatterService service;

    @Test
    void createMatterTrimsNameClearsIdAndSaves() {
        Matter saved = new Matter(1L, 10L, "Matematica");
        when(repository.save(any(Matter.class))).thenReturn(saved);

        Matter result = service.createMatter(new Matter(99L, 10L, " Matematica "));

        ArgumentCaptor<Matter> captor = ArgumentCaptor.forClass(Matter.class);
        verify(repository).save(captor.capture());

        assertEquals(saved, result);
        assertNull(captor.getValue().getId());
        assertEquals(10L, captor.getValue().getUserId());
        assertEquals("Matematica", captor.getValue().getName());
    }

    @Test
    void createMatterRejectsMissingName() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.createMatter(new Matter(null, 10L, " ")));

        assertEquals("name e obrigatorio", exception.getMessage());
    }

    @Test
    void updateMatterChangesOnlyNameWhenMatterExists() {
        Matter existingMatter = new Matter(1L, 10L, "Matematica");
        Matter saved = new Matter(1L, 10L, "Fisica");
        when(repository.findById(1L)).thenReturn(Optional.of(existingMatter));
        when(repository.save(any(Matter.class))).thenReturn(saved);

        Matter result = service.updateMatter(1L, new Matter(null, 99L, " Fisica "));

        ArgumentCaptor<Matter> captor = ArgumentCaptor.forClass(Matter.class);
        verify(repository).save(captor.capture());

        assertEquals(saved, result);
        assertEquals(1L, captor.getValue().getId());
        assertEquals(10L, captor.getValue().getUserId());
        assertEquals("Fisica", captor.getValue().getName());
    }

    @Test
    void updateMatterRejectsMissingMatter() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.updateMatter(1L, null));

        assertEquals("Materia nao informada", exception.getMessage());
        verify(repository, never()).findById(any());
    }

    @Test
    void updateMatterThrowsWhenMatterDoesNotExist() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> service.updateMatter(99L, new Matter(null, null, "Fisica")));

        assertEquals("Materia nao encontrada", exception.getMessage());
    }

    @Test
    void getMattersByUserIdUsesRepositoryFilter() {
        List<Matter> matters = List.of(new Matter(1L, 10L, "Matematica"));
        when(repository.findByUserId(10L)).thenReturn(matters);

        List<Matter> result = service.getMattersByUserId(10L);

        assertEquals(matters, result);
        verify(repository).findByUserId(10L);
    }

    @Test
    void deleteMatterDeletesWhenMatterExists() {
        Matter matter = new Matter(1L, 10L, "Matematica");
        when(repository.findById(1L)).thenReturn(Optional.of(matter));
        when(cadernoRepository.findByMatterIdAndUserId(1L, 10L)).thenReturn(List.of());

        service.deleteMatter(1L);

        verify(repository).delete(matter);
    }

    @Test
    void deleteMatterThrowsWhenMatterDoesNotExist() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> service.deleteMatter(99L));

        assertEquals("Materia nao encontrada", exception.getMessage());
        verify(repository, never()).delete(any());
    }
}
