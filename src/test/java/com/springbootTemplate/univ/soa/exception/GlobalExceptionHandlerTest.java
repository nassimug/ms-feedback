package com.springbootTemplate.univ.soa.exception;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = {
        GlobalExceptionHandlerTest.TestController.class,
        GlobalExceptionHandler.class
})
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    // --- CONTROLLER FICTIF (Public Static) ---
    @RestController
    public static class TestController {

        @GetMapping("/test/not-found")
        public void throwNotFound() {
            throw new FeedbackNotFoundException("L'élément n'existe pas");
        }

        @GetMapping("/test/runtime")
        public void throwRuntime() {
            throw new RuntimeException("Erreur inattendue");
        }

        @GetMapping("/test/global")
        public void throwGlobal() throws Exception {
            throw new IOException("Erreur IO système");
        }

        @PostMapping("/test/validation")
        public void validate(@RequestBody @Valid TestDto dto) {
            // Spring valide l'objet avant d'entrer ici.
        }
    }

    // DTO interne
    @Data
    static class TestDto {
        @NotNull(message = "Champ obligatoire")
        private String field;
    }

    // --- TESTS ---

    @Test
    void handleFeedbackNotFoundException_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Feedback non trouvé"));
    }

    @Test
    void handleValidationExceptions_ShouldReturn400() throws Exception {
        mockMvc.perform(post("/test/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Erreur de validation"));
    }

    @Test
    void handleRuntimeException_ShouldReturn503() throws Exception {
        mockMvc.perform(get("/test/runtime"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value(503));
    }

    @Test
    void handleGlobalException_ShouldReturn500() throws Exception {
        mockMvc.perform(get("/test/global"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500));
    }
}