package br.com.notelab.backend.Controllers;

import br.com.notelab.backend.Model.Matter;
import br.com.notelab.backend.Services.MatterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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
class MatterControllerTest {

    @Mock
    private MatterService service;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new MatterController(service)).build();
    }

    @Test
    void createMatterReturnsCreatedMatterAsJson() throws Exception {
        Matter created = new Matter(1L, 10L, "Matematica");
        when(service.createMatter(any(Matter.class))).thenReturn(created);

        Matter request = new Matter(null, 10L, "Matematica");

        mockMvc.perform(post("/matter/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(10))
                .andExpect(jsonPath("$.name").value("Matematica"));
    }

    @Test
    void listMattersReturnsJsonArray() throws Exception {
        when(service.listAllMatters()).thenReturn(List.of(
                new Matter(1L, 10L, "Matematica"),
                new Matter(2L, 10L, "Historia")
        ));

        mockMvc.perform(get("/matter/list"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Matematica"))
                .andExpect(jsonPath("$[1].name").value("Historia"));
    }

    @Test
    void getMattersByUserIdReturnsJsonArray() throws Exception {
        when(service.getMattersByUserId(10L)).thenReturn(List.of(
                new Matter(1L, 10L, "Matematica")
        ));

        mockMvc.perform(get("/matter/user/10"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].userId").value(10))
                .andExpect(jsonPath("$[0].name").value("Matematica"));
    }

    @Test
    void updateMatterReturnsUpdatedMatterAsJson() throws Exception {
        Matter updated = new Matter(1L, 10L, "Fisica");
        when(service.updateMatter(eq(1L), any(Matter.class))).thenReturn(updated);

        Matter request = new Matter(null, null, "Fisica");

        mockMvc.perform(put("/matter/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(10))
                .andExpect(jsonPath("$.name").value("Fisica"));
    }

    @Test
    void updateMatterReturnsNotFoundWhenMatterDoesNotExist() throws Exception {
        when(service.updateMatter(eq(99L), any(Matter.class)))
                .thenThrow(new NoSuchElementException("Materia nao encontrada"));

        Matter request = new Matter(null, null, "Fisica");

        mockMvc.perform(put("/matter/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Materia nao encontrada"));
    }

    @Test
    void deleteMatterReturnsNoContent() throws Exception {
        doNothing().when(service).deleteMatter(1L);

        mockMvc.perform(delete("/matter/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteMatterReturnsNotFoundWhenMatterDoesNotExist() throws Exception {
        doThrow(new NoSuchElementException("Materia nao encontrada"))
                .when(service).deleteMatter(99L);

        mockMvc.perform(delete("/matter/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Materia nao encontrada"));
    }
}
