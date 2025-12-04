package com.springbootTemplate.univ.soa.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeedbackUpdateRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenValidRequest_thenNoViolations() {
        FeedbackUpdateRequest request = FeedbackUpdateRequest.builder()
                .evaluation(4)
                .commentaire("Update")
                .build();

        var violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenFieldsAreNull_thenNoViolations() {
        // Dans FeedbackUpdateRequest, les champs ne sont pas @NotNull
        // Donc un objet vide est valide (mise à jour partielle)
        FeedbackUpdateRequest request = new FeedbackUpdateRequest();

        var violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenEvaluationInvalid_thenViolation() {
        FeedbackUpdateRequest request = FeedbackUpdateRequest.builder()
                .evaluation(10) // Trop grand
                .build();

        var violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }
}