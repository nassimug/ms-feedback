package com.springbootTemplate.univ.soa;

import com.springbootTemplate.univ.soa.exception.FeedbackNotFoundException;
import com.springbootTemplate.univ.soa.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleValidationExceptions_ShouldReturnBadRequestWithErrors() {
        // Given
        FieldError fieldError1 = new FieldError("feedbackCreateRequest", "evaluation", "L'évaluation doit être entre 1 et 5");
        FieldError fieldError2 = new FieldError("feedbackCreateRequest", "userId", "L'ID utilisateur ne peut pas être vide");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList(fieldError1, fieldError2));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        LocalDateTime beforeCall = LocalDateTime.now();

        // When
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleValidationExceptions(exception);

        LocalDateTime afterCall = LocalDateTime.now();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();

        Map<String, Object> body = response.getBody();
        assertThat(body.get("status")).isEqualTo(400);
        assertThat(body.get("error")).isEqualTo("Erreur de validation");

        LocalDateTime timestamp = (LocalDateTime) body.get("timestamp");
        assertThat(timestamp).isBetween(beforeCall, afterCall);

        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) body.get("errors");
        assertThat(errors).hasSize(2);
        assertThat(errors.get("evaluation")).isEqualTo("L'évaluation doit être entre 1 et 5");
        assertThat(errors.get("userId")).isEqualTo("L'ID utilisateur ne peut pas être vide");
    }

    @Test
    void handleValidationExceptions_ShouldHandleSingleError() {
        // Given
        FieldError fieldError = new FieldError("feedbackCreateRequest", "recetteId", "L'ID recette est obligatoire");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList(fieldError));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        // When
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleValidationExceptions(exception);

        // Then
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) response.getBody().get("errors");
        assertThat(errors).hasSize(1);
        assertThat(errors.get("recetteId")).isEqualTo("L'ID recette est obligatoire");
    }

    @Test
    void handleFeedbackNotFoundException_ShouldReturnNotFound() {
        // Given
        FeedbackNotFoundException exception = new FeedbackNotFoundException("Feedback non trouvé avec l'ID: 507f1f77bcf86cd799439011");

        LocalDateTime beforeCall = LocalDateTime.now();

        // When
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleFeedbackNotFoundException(exception);

        LocalDateTime afterCall = LocalDateTime.now();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();

        Map<String, Object> body = response.getBody();
        assertThat(body.get("status")).isEqualTo(404);
        assertThat(body.get("error")).isEqualTo("Feedback non trouvé");
        assertThat(body.get("message")).isEqualTo("Feedback non trouvé avec l'ID: 507f1f77bcf86cd799439011");

        LocalDateTime timestamp = (LocalDateTime) body.get("timestamp");
        assertThat(timestamp).isBetween(beforeCall, afterCall);
    }

    @Test
    void handleFeedbackNotFoundException_ShouldHandleCustomMessage() {
        // Given
        FeedbackNotFoundException exception = new FeedbackNotFoundException("Aucun feedback trouvé pour l'utilisateur");

        // When
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleFeedbackNotFoundException(exception);

        // Then
        Map<String, Object> body = response.getBody();
        assertThat(body.get("message")).isEqualTo("Aucun feedback trouvé pour l'utilisateur");
    }

    @Test
    void handleGlobalException_ShouldReturnInternalServerError() {
        // Given
        Exception exception = new Exception("Erreur inattendue lors du traitement");

        LocalDateTime beforeCall = LocalDateTime.now();

        // When
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleGlobalException(exception);

        LocalDateTime afterCall = LocalDateTime.now();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();

        Map<String, Object> body = response.getBody();
        assertThat(body.get("status")).isEqualTo(500);
        assertThat(body.get("error")).isEqualTo("Erreur interne du serveur");
        assertThat(body.get("message")).isEqualTo("Erreur inattendue lors du traitement");

        LocalDateTime timestamp = (LocalDateTime) body.get("timestamp");
        assertThat(timestamp).isBetween(beforeCall, afterCall);
    }

    @Test
    void handleGlobalException_ShouldHandleNullPointerException() {
        // Given
        Exception exception = new NullPointerException("Objet null détecté");

        // When
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleGlobalException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        Map<String, Object> body = response.getBody();
        assertThat(body.get("message")).isEqualTo("Objet null détecté");
    }

    @Test
    void handleRuntimeException_ShouldReturnServiceUnavailable() {
        // Given
        RuntimeException exception = new RuntimeException("Service de recommandation non disponible");

        LocalDateTime beforeCall = LocalDateTime.now();

        // When
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleRuntimeException(exception);

        LocalDateTime afterCall = LocalDateTime.now();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody()).isNotNull();

        Map<String, Object> body = response.getBody();
        assertThat(body.get("status")).isEqualTo(503);
        assertThat(body.get("error")).isEqualTo("Service temporairement indisponible");
        assertThat(body.get("message")).isEqualTo("Service de recommandation non disponible");

        LocalDateTime timestamp = (LocalDateTime) body.get("timestamp");
        assertThat(timestamp).isBetween(beforeCall, afterCall);
    }

    @Test
    void handleRuntimeException_ShouldHandleConnectionTimeout() {
        // Given
        RuntimeException exception = new RuntimeException("Timeout lors de la connexion au service externe");

        // When
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleRuntimeException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        Map<String, Object> body = response.getBody();
        assertThat(body.get("message")).isEqualTo("Timeout lors de la connexion au service externe");
    }

    @Test
    void allExceptionHandlers_ShouldIncludeTimestamp() {
        // Test avec FeedbackNotFoundException
        FeedbackNotFoundException notFoundException = new FeedbackNotFoundException("Test");
        ResponseEntity<Map<String, Object>> response1 = exceptionHandler.handleFeedbackNotFoundException(notFoundException);
        assertThat(response1.getBody().get("timestamp")).isInstanceOf(LocalDateTime.class);

        // Test avec Exception générique
        Exception generalException = new Exception("Test");
        ResponseEntity<Map<String, Object>> response2 = exceptionHandler.handleGlobalException(generalException);
        assertThat(response2.getBody().get("timestamp")).isInstanceOf(LocalDateTime.class);

        // Test avec RuntimeException
        RuntimeException runtimeException = new RuntimeException("Test");
        ResponseEntity<Map<String, Object>> response3 = exceptionHandler.handleRuntimeException(runtimeException);
        assertThat(response3.getBody().get("timestamp")).isInstanceOf(LocalDateTime.class);
    }

    @Test
    void allExceptionHandlers_ShouldIncludeStatusCode() {
        // Test avec FeedbackNotFoundException
        FeedbackNotFoundException notFoundException = new FeedbackNotFoundException("Test");
        ResponseEntity<Map<String, Object>> response1 = exceptionHandler.handleFeedbackNotFoundException(notFoundException);
        assertThat(response1.getBody().get("status")).isEqualTo(404);

        // Test avec Exception générique
        Exception generalException = new Exception("Test");
        ResponseEntity<Map<String, Object>> response2 = exceptionHandler.handleGlobalException(generalException);
        assertThat(response2.getBody().get("status")).isEqualTo(500);

        // Test avec RuntimeException
        RuntimeException runtimeException = new RuntimeException("Test");
        ResponseEntity<Map<String, Object>> response3 = exceptionHandler.handleRuntimeException(runtimeException);
        assertThat(response3.getBody().get("status")).isEqualTo(503);
    }
}