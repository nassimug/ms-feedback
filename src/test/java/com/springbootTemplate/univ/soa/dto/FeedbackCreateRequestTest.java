package com.springbootTemplate.univ.soa.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FeedbackCreateRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenAllFieldsCorrect_thenNoViolations() {
        FeedbackCreateRequest request = FeedbackCreateRequest.builder()
                .utilisateurId(1L)
                .recetteId(5L)
                .evaluation(5)
                .commentaire("Parfait")
                .build();

        var violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Il ne devrait pas y avoir d'erreurs de validation");
    }

    @Test
    void whenMissingMandatoryFields_thenViolationsDetected() {
        FeedbackCreateRequest request = new FeedbackCreateRequest();
        // Tout est null

        var violations = validator.validate(request);

        // On s'attend à 3 erreurs (utilisateurId, recetteId, evaluation)
        assertEquals(3, violations.size());
    }

    @Test
    void whenEvaluationTooLow_thenViolation() {
        FeedbackCreateRequest request = FeedbackCreateRequest.builder()
                .utilisateurId(1L)
                .recetteId(5L)
                .evaluation(0) // Invalid (min 1)
                .build();

        var violations = validator.validate(request);
        assertFalse(violations.isEmpty());

        String msg = violations.iterator().next().getMessage();
        assertEquals("L'évaluation doit être entre 1 et 5", msg);
    }

    @Test
    void whenEvaluationTooHigh_thenViolation() {
        FeedbackCreateRequest request = FeedbackCreateRequest.builder()
                .utilisateurId(1L)
                .recetteId(5L)
                .evaluation(6) // Invalid (max 5)
                .build();

        var violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenCommentTooLong_thenViolation() {
        // Création d'une chaine de 1001 caractères
        String longComment = "a".repeat(1001);

        FeedbackCreateRequest request = FeedbackCreateRequest.builder()
                .utilisateurId(1L)
                .recetteId(5L)
                .evaluation(3)
                .commentaire(longComment)
                .build();

        var violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.iterator().next().getMessage().contains("1000"));
    }
}