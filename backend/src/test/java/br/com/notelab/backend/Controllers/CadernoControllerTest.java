package br.com.notelab.backend.Controllers;

import br.com.notelab.backend.Model.Caderno;
import br.com.notelab.backend.Services.CadernoService;
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
class CadernoControllerTest {

    @Mock
    private CadernoService service;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new CadernoController(service)).build();
    }

    @Test
    void createCadernoReturnsCreatedCadernoAsJson() throws Exception {
        Caderno created = new Caderno(1L, 10L, 2L, "POO", "Orientacao a objetos");
        when(service.createCaderno(any(Caderno.class))).thenReturn(created);

        Caderno request = new Caderno(null, 10L, 2L, "POO", "Orientacao a objetos");

        mockMvc.perform(post("/caderno/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(10))
                .andExpect(jsonPath("$.matterId").value(2))
                .andExpect(jsonPath("$.name").value("POO"));
    }

    @Test
    void createCadernoReturnsNotFoundWhenMatterDoesNotExist() throws Exception {
        when(service.createCaderno(any(Caderno.class)))
                .thenThrow(new NoSuchElementException("Materia nao encontrada"));

        Caderno request = new Caderno(null, 10L, 99L, "POO", "Orientacao a objetos");

        mockMvc.perform(post("/caderno/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Materia nao encontrada"));
    }

    @Test
    void listByUserReturnsJsonArray() throws Exception {
        when(service.listByUser(10L)).thenReturn(List.of(
                new Caderno(1L, 10L, 2L, "POO", "Orientacao a objetos")
        ));

        mockMvc.perform(get("/caderno/user/10"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].userId").value(10));
    }

    @Test
    void listByMatterReturnsJsonArray() throws Exception {
        when(service.listByMatter(2L)).thenReturn(List.of(
                new Caderno(1L, 10L, 2L, "POO", "Orientacao a objetos"),
                new Caderno(2L, 10L, 2L, "Spring Boot", "APIs REST")
        ));

        mockMvc.perform(get("/caderno/matter/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].matterId").value(2));
    }

    @Test
    void updateCadernoReturnsUpdatedCadernoAsJson() throws Exception {
        Caderno updated = new Caderno(1L, 10L, 2L, "Spring Boot", "APIs REST");
        when(service.updateCaderno(eq(1L), any(Caderno.class))).thenReturn(updated);

        Caderno request = new Caderno(null, null, null, "Spring Boot", "APIs REST");

        mockMvc.perform(put("/caderno/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Spring Boot"))
                .andExpect(jsonPath("$.description").value("APIs REST"));
    }

    @Test
    void updateCadernoReturnsNotFoundWhenCadernoDoesNotExist() throws Exception {
        when(service.updateCaderno(eq(99L), any(Caderno.class)))
                .thenThrow(new NoSuchElementException("Caderno nao encontrado"));

        Caderno request = new Caderno(null, null, null, "Spring Boot", "APIs REST");

        mockMvc.perform(put("/caderno/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Caderno nao encontrado"));
    }

    @Test
    void deleteCadernoReturnsNoContent() throws Exception {
        doNothing().when(service).deleteCaderno(1L);

        mockMvc.perform(delete("/caderno/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteCadernoReturnsNotFoundWhenCadernoDoesNotExist() throws Exception {
        doThrow(new NoSuchElementException("Caderno nao encontrado"))
                .when(service).deleteCaderno(99L);

        mockMvc.perform(delete("/caderno/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Caderno nao encontrado"));
    }
}
