package com.springbootTemplate.univ.soa;

import com.springbootTemplate.univ.soa.dto.AverageRatingResponse;
import com.springbootTemplate.univ.soa.dto.FeedbackCreateRequest;
import com.springbootTemplate.univ.soa.dto.FeedbackResponse;
import com.springbootTemplate.univ.soa.factory.FeedbackFactory;
import com.springbootTemplate.univ.soa.model.Feedback;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FeedbackFactoryTest {

    private FeedbackFactory feedbackFactory;
    private LocalDateTime fixedTime;

    @BeforeEach
    void setUp() {
        feedbackFactory = new FeedbackFactory();
        fixedTime = LocalDateTime.of(2025, 1, 15, 10, 30);
    }

    @Test
    void createFeedback_ShouldCreateFeedbackWithAllFields() {
        // Given
        FeedbackCreateRequest request = FeedbackCreateRequest.builder()
                .userId("user123")
                .recetteId("recette456")
                .evaluation(5)
                .commentaire("Excellente recette !")
                .build();

        // When
        Feedback result = feedbackFactory.createFeedback(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo("user123");
        assertThat(result.getRecetteId()).isEqualTo("recette456");
        assertThat(result.getEvaluation()).isEqualTo(5);
        assertThat(result.getCommentaire()).isEqualTo("Excellente recette !");
        assertThat(result.getDateFeedback()).isNotNull();
        assertThat(result.getDateModification()).isNotNull();
        assertThat(result.getId()).isNull(); // L'ID est généré par MongoDB
    }

    @Test
    void createFeedback_ShouldSetCurrentDatesAutomatically() {
        // Given
        FeedbackCreateRequest request = FeedbackCreateRequest.builder()
                .userId("user1")
                .recetteId("recette1")
                .evaluation(4)
                .commentaire("Bon")
                .build();

        LocalDateTime before = LocalDateTime.now();

        // When
        Feedback result = feedbackFactory.createFeedback(request);

        LocalDateTime after = LocalDateTime.now();

        // Then
        assertThat(result.getDateFeedback()).isBetween(before, after);
        assertThat(result.getDateModification()).isBetween(before, after);
    }

    @Test
    void createFeedback_ShouldHandleNullCommentaire() {
        // Given
        FeedbackCreateRequest request = FeedbackCreateRequest.builder()
                .userId("user1")
                .recetteId("recette1")
                .evaluation(3)
                .commentaire(null)
                .build();

        // When
        Feedback result = feedbackFactory.createFeedback(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCommentaire()).isNull();
    }

    @Test
    void createResponse_ShouldMapFeedbackToResponse() {
        // Given
        Feedback feedback = Feedback.builder()
                .id("507f1f77bcf86cd799439011")
                .userId("user123")
                .recetteId("recette456")
                .evaluation(5)
                .commentaire("Excellent")
                .dateFeedback(fixedTime)
                .dateModification(fixedTime)
                .build();

        // When
        FeedbackResponse result = feedbackFactory.createResponse(feedback);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("507f1f77bcf86cd799439011");
        assertThat(result.getUserId()).isEqualTo("user123");
        assertThat(result.getRecetteId()).isEqualTo("recette456");
        assertThat(result.getEvaluation()).isEqualTo(5);
        assertThat(result.getCommentaire()).isEqualTo("Excellent");
        assertThat(result.getDateFeedback()).isEqualTo(fixedTime);
        assertThat(result.getDateModification()).isEqualTo(fixedTime);
    }

    @Test
    void createResponseList_ShouldMapMultipleFeedbacks() {
        // Given
        Feedback feedback1 = Feedback.builder()
                .id("id1")
                .userId("user1")
                .recetteId("recette1")
                .evaluation(5)
                .commentaire("Super")
                .dateFeedback(fixedTime)
                .dateModification(fixedTime)
                .build();

        Feedback feedback2 = Feedback.builder()
                .id("id2")
                .userId("user2")
                .recetteId("recette2")
                .evaluation(4)
                .commentaire("Bien")
                .dateFeedback(fixedTime)
                .dateModification(fixedTime)
                .build();

        List<Feedback> feedbacks = Arrays.asList(feedback1, feedback2);

        // When
        List<FeedbackResponse> result = feedbackFactory.createResponseList(feedbacks);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo("id1");
        assertThat(result.get(0).getUserId()).isEqualTo("user1");
        assertThat(result.get(1).getId()).isEqualTo("id2");
        assertThat(result.get(1).getUserId()).isEqualTo("user2");
    }

    @Test
    void createResponseList_ShouldReturnEmptyListForEmptyInput() {
        // Given
        List<Feedback> feedbacks = Arrays.asList();

        // When
        List<FeedbackResponse> result = feedbackFactory.createResponseList(feedbacks);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void createAverageRatingResponse_ShouldRoundToTwoDecimals() {
        // Given
        String recetteId = "recette123";
        Double averageRating = 4.567;
        Long totalFeedbacks = 10L;

        // When
        AverageRatingResponse result = feedbackFactory.createAverageRatingResponse(
                recetteId, averageRating, totalFeedbacks
        );

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRecetteId()).isEqualTo("recette123");
        assertThat(result.getAverageRating()).isEqualTo(4.57);
        assertThat(result.getTotalFeedbacks()).isEqualTo(10L);
    }

    @Test
    void createAverageRatingResponse_ShouldHandleNullAverageRating() {
        // Given
        String recetteId = "recette123";
        Double averageRating = null;
        Long totalFeedbacks = 0L;

        // When
        AverageRatingResponse result = feedbackFactory.createAverageRatingResponse(
                recetteId, averageRating, totalFeedbacks
        );

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRecetteId()).isEqualTo("recette123");
        assertThat(result.getAverageRating()).isEqualTo(0.0);
        assertThat(result.getTotalFeedbacks()).isEqualTo(0L);
    }

    @Test
    void createAverageRatingResponse_ShouldHandleNullTotalFeedbacks() {
        // Given
        String recetteId = "recette123";
        Double averageRating = 4.5;
        Long totalFeedbacks = null;

        // When
        AverageRatingResponse result = feedbackFactory.createAverageRatingResponse(
                recetteId, averageRating, totalFeedbacks
        );

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAverageRating()).isEqualTo(4.5);
        assertThat(result.getTotalFeedbacks()).isEqualTo(0L);
    }

    @Test
    void createAverageRatingResponse_ShouldRoundUpCorrectly() {
        // Given
        String recetteId = "recette123";
        Double averageRating = 3.999;

        // When
        AverageRatingResponse result = feedbackFactory.createAverageRatingResponse(
                recetteId, averageRating, 5L
        );

        // Then
        assertThat(result.getAverageRating()).isEqualTo(4.0);
    }

    @Test
    void createUpdatedFeedback_ShouldUpdateBothFields() {
        // Given
        Feedback original = Feedback.builder()
                .id("id123")
                .userId("user123")
                .recetteId("recette456")
                .evaluation(3)
                .commentaire("Moyen")
                .dateFeedback(fixedTime)
                .dateModification(fixedTime)
                .build();

        Integer newEvaluation = 5;
        String newCommentaire = "Finalement excellent !";

        LocalDateTime before = LocalDateTime.now();

        // When
        Feedback result = feedbackFactory.createUpdatedFeedback(original, newEvaluation, newCommentaire);

        LocalDateTime after = LocalDateTime.now();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("id123");
        assertThat(result.getUserId()).isEqualTo("user123");
        assertThat(result.getRecetteId()).isEqualTo("recette456");
        assertThat(result.getEvaluation()).isEqualTo(5);
        assertThat(result.getCommentaire()).isEqualTo("Finalement excellent !");
        assertThat(result.getDateFeedback()).isEqualTo(fixedTime);
        assertThat(result.getDateModification()).isBetween(before, after);
    }

    @Test
    void createUpdatedFeedback_ShouldKeepOriginalEvaluationIfNull() {
        // Given
        Feedback original = Feedback.builder()
                .id("id123")
                .userId("user123")
                .recetteId("recette456")
                .evaluation(4)
                .commentaire("Bien")
                .dateFeedback(fixedTime)
                .dateModification(fixedTime)
                .build();

        // When
        Feedback result = feedbackFactory.createUpdatedFeedback(original, null, "Nouveau commentaire");

        // Then
        assertThat(result.getEvaluation()).isEqualTo(4);
        assertThat(result.getCommentaire()).isEqualTo("Nouveau commentaire");
    }

    @Test
    void createUpdatedFeedback_ShouldKeepOriginalCommentaireIfNull() {
        // Given
        Feedback original = Feedback.builder()
                .id("id123")
                .userId("user123")
                .recetteId("recette456")
                .evaluation(4)
                .commentaire("Commentaire original")
                .dateFeedback(fixedTime)
                .dateModification(fixedTime)
                .build();

        // When
        Feedback result = feedbackFactory.createUpdatedFeedback(original, 5, null);

        // Then
        assertThat(result.getEvaluation()).isEqualTo(5);
        assertThat(result.getCommentaire()).isEqualTo("Commentaire original");
    }

    @Test
    void createUpdatedFeedback_ShouldKeepBothOriginalFieldsIfBothNull() {
        // Given
        Feedback original = Feedback.builder()
                .id("id123")
                .userId("user123")
                .recetteId("recette456")
                .evaluation(3)
                .commentaire("Original")
                .dateFeedback(fixedTime)
                .dateModification(fixedTime)
                .build();

        // When
        Feedback result = feedbackFactory.createUpdatedFeedback(original, null, null);

        // Then
        assertThat(result.getEvaluation()).isEqualTo(3);
        assertThat(result.getCommentaire()).isEqualTo("Original");
    }

    @Test
    void createUpdatedFeedback_ShouldPreserveOriginalMetadata() {
        // Given
        Feedback original = Feedback.builder()
                .id("original-id")
                .userId("original-user")
                .recetteId("original-recette")
                .evaluation(3)
                .commentaire("Original")
                .dateFeedback(fixedTime)
                .dateModification(fixedTime)
                .build();

        // When
        Feedback result = feedbackFactory.createUpdatedFeedback(original, 5, "Nouveau");

        // Then
        assertThat(result.getId()).isEqualTo("original-id");
        assertThat(result.getUserId()).isEqualTo("original-user");
        assertThat(result.getRecetteId()).isEqualTo("original-recette");
        assertThat(result.getDateFeedback()).isEqualTo(fixedTime);
    }
}