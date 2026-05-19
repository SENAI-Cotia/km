package br.com.notelab.backend.Services;

import br.com.notelab.backend.Model.Matter;
import br.com.notelab.backend.Repository.MatterRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MatterServiceTest {

    @Mock
    private MatterRepository repository;

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
}
