package br.com.notelab.backend.Services;

import br.com.notelab.backend.Model.Caderno;
import br.com.notelab.backend.Repository.CadernoRepository;
import br.com.notelab.backend.Repository.MatterRepository;
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
class CadernoServiceTest {

    @Mock
    private CadernoRepository cadernoRepository;

    @Mock
    private MatterRepository matterRepository;

    @InjectMocks
    private CadernoService service;

    @Test
    void createCadernoValidatesMatterAndSaves() {
        Caderno saved = new Caderno(1L, 10L, 2L, "POO", "Orientacao a objetos");
        when(matterRepository.existsById(2L)).thenReturn(true);
        when(cadernoRepository.save(any(Caderno.class))).thenReturn(saved);

        Caderno result = service.createCaderno(new Caderno(99L, 10L, 2L, " POO ", " Orientacao a objetos "));

        ArgumentCaptor<Caderno> captor = ArgumentCaptor.forClass(Caderno.class);
        verify(cadernoRepository).save(captor.capture());

        assertEquals(saved, result);
        assertNull(captor.getValue().getId());
        assertEquals(10L, captor.getValue().getUserId());
        assertEquals(2L, captor.getValue().getMatterId());
        assertEquals("POO", captor.getValue().getName());
        assertEquals("Orientacao a objetos", captor.getValue().getDescription());
    }

    @Test
    void createCadernoRejectsMissingMatterId() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.createCaderno(new Caderno(null, 10L, null, "POO", "Orientacao a objetos")));

        assertEquals("matterId e obrigatorio", exception.getMessage());
        verify(matterRepository, never()).existsById(any());
    }

    @Test
    void createCadernoThrowsWhenMatterDoesNotExist() {
        when(matterRepository.existsById(99L)).thenReturn(false);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> service.createCaderno(new Caderno(null, 10L, 99L, "POO", "Orientacao a objetos")));

        assertEquals("Materia nao encontrada", exception.getMessage());
        verify(cadernoRepository, never()).save(any());
    }

    @Test
    void updateCadernoChangesOnlyNameAndDescription() {
        Caderno existing = new Caderno(1L, 10L, 2L, "POO", "Orientacao a objetos");
        Caderno saved = new Caderno(1L, 10L, 2L, "Spring Boot", "APIs REST");
        when(cadernoRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(cadernoRepository.save(any(Caderno.class))).thenReturn(saved);

        Caderno result = service.updateCaderno(1L, new Caderno(null, 99L, 99L, " Spring Boot ", " APIs REST "));

        ArgumentCaptor<Caderno> captor = ArgumentCaptor.forClass(Caderno.class);
        verify(cadernoRepository).save(captor.capture());

        assertEquals(saved, result);
        assertEquals(1L, captor.getValue().getId());
        assertEquals(10L, captor.getValue().getUserId());
        assertEquals(2L, captor.getValue().getMatterId());
        assertEquals("Spring Boot", captor.getValue().getName());
        assertEquals("APIs REST", captor.getValue().getDescription());
    }

    @Test
    void updateCadernoThrowsWhenCadernoDoesNotExist() {
        when(cadernoRepository.findById(99L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> service.updateCaderno(99L, new Caderno(null, null, null, "Spring Boot", "APIs REST")));

        assertEquals("Caderno nao encontrado", exception.getMessage());
    }

    @Test
    void listByUserUsesRepositoryFilter() {
        List<Caderno> cadernos = List.of(new Caderno(1L, 10L, 2L, "POO", "Orientacao a objetos"));
        when(cadernoRepository.findByUserId(10L)).thenReturn(cadernos);

        List<Caderno> result = service.listByUser(10L);

        assertEquals(cadernos, result);
        verify(cadernoRepository).findByUserId(10L);
    }

    @Test
    void listByMatterUsesRepositoryFilter() {
        List<Caderno> cadernos = List.of(new Caderno(1L, 10L, 2L, "POO", "Orientacao a objetos"));
        when(cadernoRepository.findByMatterId(2L)).thenReturn(cadernos);

        List<Caderno> result = service.listByMatter(2L);

        assertEquals(cadernos, result);
        verify(cadernoRepository).findByMatterId(2L);
    }

    @Test
    void deleteCadernoDeletesWhenCadernoExists() {
        when(cadernoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(cadernoRepository).deleteById(1L);

        service.deleteCaderno(1L);

        verify(cadernoRepository).deleteById(1L);
    }

    @Test
    void deleteCadernoThrowsWhenCadernoDoesNotExist() {
        when(cadernoRepository.existsById(99L)).thenReturn(false);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> service.deleteCaderno(99L));

        assertEquals("Caderno nao encontrado", exception.getMessage());
        verify(cadernoRepository, never()).deleteById(99L);
    }
}
