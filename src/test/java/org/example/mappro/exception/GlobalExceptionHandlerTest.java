package org.example.mappro.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new FailingController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void returnsStableNotFoundError() throws Exception {
        mockMvc.perform(get("/test-errors/not-found").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.code").value("TEST_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Test resource not found"))
                .andExpect(jsonPath("$.path").value("/test-errors/not-found"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void returnsStableConflictError() throws Exception {
        mockMvc.perform(get("/test-errors/conflict").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.code").value("TEST_CONFLICT"))
                .andExpect(jsonPath("$.message").value("Test resource already exists"));
    }

    @Test
    void hidesUnexpectedExceptionDetails() throws Exception {
        mockMvc.perform(get("/test-errors/unexpected").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"))
                .andExpect(jsonPath("$.message").value("Внутренняя ошибка сервера"));
    }

    @RestController
    @RequestMapping("/test-errors")
    static class FailingController {

        @GetMapping("/not-found")
        void notFound() {
            throw new ResourceNotFoundException("TEST_NOT_FOUND", "Test resource not found");
        }

        @GetMapping("/conflict")
        void conflict() {
            throw new ResourceConflictException("TEST_CONFLICT", "Test resource already exists");
        }

        @GetMapping("/unexpected")
        void unexpected() {
            throw new RuntimeException("Sensitive internal details");
        }
    }
}
