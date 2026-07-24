package org.example.mappro.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.mappro.exception.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class AuthErrorResponseWriter {
    private final ObjectMapper objectMapper;

    public void write(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        HttpStatus httpStatus = HttpStatus.valueOf(status);
        objectMapper.writeValue(
                response.getOutputStream(),
                ApiErrorResponse.of(
                        status,
                        httpStatus.getReasonPhrase(),
                        status == HttpServletResponse.SC_FORBIDDEN ? "ACCESS_DENIED" : "AUTHENTICATION_FAILED",
                        message,
                        null
                )
        );
    }
}
