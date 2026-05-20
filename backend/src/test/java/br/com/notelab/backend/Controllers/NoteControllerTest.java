package br.com.notelab.backend.Controllers;

import br.com.notelab.backend.Model.Note;
import br.com.notelab.backend.Services.NoteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class NoteControllerTest {

    @Mock
    private NoteService noteService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new NoteController(noteService)).build();
    }

    @Test
    void createNoteReturnsCreatedNoteAsJson() throws Exception {
        Note created = new Note(1L, 2L, "Aula 1", "Conteudo", LocalDate.now());
        when(noteService.createNote(any(Note.class))).thenReturn(created);

        Note request = new Note(null, 2L, "Aula 1", "Conteudo", null);

        mockMvc.perform(post("/note/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.idCaderno").value(2))
                .andExpect(jsonPath("$.name").value("Aula 1"))
                .andExpect(jsonPath("$.noteContent").value("Conteudo"));
    }

    @Test
    void createNoteReturnsNotFoundWhenCadernoDoesNotExist() throws Exception {
        when(noteService.createNote(any(Note.class)))
                .thenThrow(new NoSuchElementException("Caderno nao encontrado"));

        Note request = new Note(null, 99L, "Aula 1", "Conteudo", null);

        mockMvc.perform(post("/note/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Caderno nao encontrado"));
    }

    @Test
    void listByCadernoReturnsJsonArray() throws Exception {
        when(noteService.listByCaderno(2L)).thenReturn(List.of(
                new Note(1L, 2L, "Aula 1", "Conteudo", LocalDate.now())
        ));

        mockMvc.perform(get("/note/caderno/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idCaderno").value(2));
    }

    @Test
    void updateNoteReturnsUpdatedNoteAsJson() throws Exception {
        Note updated = new Note(1L, 2L, "Aula 2", "Novo conteudo", LocalDate.now());
        when(noteService.updateNote(eq(1L), any(Note.class))).thenReturn(updated);

        Note request = new Note(null, null, "Aula 2", "Novo conteudo", null);

        mockMvc.perform(put("/note/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Aula 2"))
                .andExpect(jsonPath("$.noteContent").value("Novo conteudo"));
    }

    @Test
    void deleteNoteReturnsNoContent() throws Exception {
        doNothing().when(noteService).deleteNote(1L);

        mockMvc.perform(delete("/note/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteNoteReturnsNotFoundWhenNoteDoesNotExist() throws Exception {
        doThrow(new NoSuchElementException("Nota nao encontrada"))
                .when(noteService).deleteNote(99L);

        mockMvc.perform(delete("/note/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Nota nao encontrada"));
    }
}
