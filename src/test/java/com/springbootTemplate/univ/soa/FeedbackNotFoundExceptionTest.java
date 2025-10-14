package com.springbootTemplate.univ.soa;

import com.springbootTemplate.univ.soa.exception.FeedbackNotFoundException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FeedbackNotFoundExceptionTest {

    @Test
    void constructor_ShouldSetMessage() {
        // Given
        String errorMessage = "Feedback non trouvé avec l'ID: 507f1f77bcf86cd799439011";

        // When
        FeedbackNotFoundException exception = new FeedbackNotFoundException(errorMessage);

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(errorMessage);
    }

    @Test
    void exception_ShouldBeInstanceOfRuntimeException() {
        // Given
        FeedbackNotFoundException exception = new FeedbackNotFoundException("Test message");

        // Then
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    void exception_ShouldBeThrowable() {
        // Given & When & Then
        assertThatThrownBy(() -> {
            throw new FeedbackNotFoundException("Feedback introuvable");
        })
                .isInstanceOf(FeedbackNotFoundException.class)
                .hasMessage("Feedback introuvable")
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void exception_ShouldHandleEmptyMessage() {
        // Given
        String emptyMessage = "";

        // When
        FeedbackNotFoundException exception = new FeedbackNotFoundException(emptyMessage);

        // Then
        assertThat(exception.getMessage()).isEmpty();
    }

    @Test
    void exception_ShouldHandleNullMessage() {
        // Given
        String nullMessage = null;

        // When
        FeedbackNotFoundException exception = new FeedbackNotFoundException(nullMessage);

        // Then
        assertThat(exception.getMessage()).isNull();
    }

    @Test
    void exception_ShouldHandleLongMessage() {
        // Given
        String longMessage = "Feedback non trouvé avec l'ID: 507f1f77bcf86cd799439011. " +
                "Veuillez vérifier que l'identifiant est correct et que le feedback existe dans la base de données. " +
                "Si le problème persiste, contactez l'administrateur système.";

        // When
        FeedbackNotFoundException exception = new FeedbackNotFoundException(longMessage);

        // Then
        assertThat(exception.getMessage()).isEqualTo(longMessage);
        assertThat(exception.getMessage()).contains("507f1f77bcf86cd799439011");
    }

    @Test
    void exception_ShouldHandleSpecialCharactersInMessage() {
        // Given
        String messageWithSpecialChars = "Feedback non trouvé: éàù çñ €$£ 🚀";

        // When
        FeedbackNotFoundException exception = new FeedbackNotFoundException(messageWithSpecialChars);

        // Then
        assertThat(exception.getMessage()).isEqualTo(messageWithSpecialChars);
    }

    @Test
    void exception_CanBeCaughtAsRuntimeException() {
        // Given & When & Then
        try {
            throw new FeedbackNotFoundException("Test exception");
        } catch (RuntimeException e) {
            assertThat(e).isInstanceOf(FeedbackNotFoundException.class);
            assertThat(e.getMessage()).isEqualTo("Test exception");
        }
    }

    @Test
    void exception_ShouldSupportStackTrace() {
        // Given
        FeedbackNotFoundException exception = new FeedbackNotFoundException("Test message");

        // When
        StackTraceElement[] stackTrace = exception.getStackTrace();

        // Then
        assertThat(stackTrace).isNotNull();
        assertThat(stackTrace).isNotEmpty();
    }
}