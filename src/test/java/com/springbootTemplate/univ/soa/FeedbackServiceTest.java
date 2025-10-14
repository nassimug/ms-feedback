package com.springbootTemplate.univ.soa;

import com.springbootTemplate.univ.soa.dto.FeedbackCreateRequest;
import com.springbootTemplate.univ.soa.dto.FeedbackUpdateRequest;
import com.springbootTemplate.univ.soa.dto.AverageRatingResponse;
import com.springbootTemplate.univ.soa.dto.FeedbackResponse;
import com.springbootTemplate.univ.soa.exception.FeedbackNotFoundException;
import com.springbootTemplate.univ.soa.factory.FeedbackFactory;
import com.springbootTemplate.univ.soa.model.Feedback;
import com.springbootTemplate.univ.soa.repository.FeedbackRepository;
import com.springbootTemplate.univ.soa.service.FeedbackServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceTest {

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private FeedbackFactory feedbackFactory;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private FeedbackServiceImpl feedbackService;

    private Feedback feedback;
    private FeedbackCreateRequest feedbackCreateRequest;
    private FeedbackResponse feedbackResponse;

    @BeforeEach
    void setUp() {
        // Création d'une entité Feedback
        feedback = Feedback.builder()
                .id("507f1f77bcf86cd799439011")
                .userId("user123")
                .recetteId("recette456")
                .evaluation(5)
                .commentaire("Excellente recette !")
                .dateFeedback(LocalDateTime.now())
                .dateModification(LocalDateTime.now())
                .build();

        // Création d'une requête
        feedbackCreateRequest = FeedbackCreateRequest.builder()
                .userId("user123")
                .recetteId("recette456")
                .evaluation(5)
                .commentaire("Excellente recette !")
                .build();

        // Création d'une réponse
        feedbackResponse = FeedbackResponse.builder()
                .id("507f1f77bcf86cd799439011")
                .userId("user123")
                .recetteId("recette456")
                .evaluation(5)
                .commentaire("Excellente recette !")
                .dateFeedback(LocalDateTime.now())
                .dateModification(LocalDateTime.now())
                .build();
    }

    @Test
    void createFeedback_ShouldReturnCreatedFeedback() {
        // Given
        when(feedbackFactory.createFeedback(feedbackCreateRequest)).thenReturn(feedback);
        when(feedbackRepository.save(feedback)).thenReturn(feedback);
        when(feedbackFactory.createResponse(feedback)).thenReturn(feedbackResponse);

        // When
        FeedbackResponse result = feedbackService.createFeedback(feedbackCreateRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo("user123");
        assertThat(result.getRecetteId()).isEqualTo("recette456");
        assertThat(result.getEvaluation()).isEqualTo(5);

        verify(feedbackFactory, times(1)).createFeedback(feedbackCreateRequest);
        verify(feedbackRepository, times(1)).save(feedback);
        verify(feedbackFactory, times(1)).createResponse(feedback);
    }

    @Test
    void getAllFeedbacks_ShouldReturnListOfFeedbacks() {
        // Given
        List<Feedback> feedbacks = Arrays.asList(feedback);
        List<FeedbackResponse> responses = Arrays.asList(feedbackResponse);

        when(feedbackRepository.findAll()).thenReturn(feedbacks);
        when(feedbackFactory.createResponseList(feedbacks)).thenReturn(responses);

        // When
        List<FeedbackResponse> result = feedbackService.getAllFeedbacks();

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo("user123");

        verify(feedbackRepository, times(1)).findAll();
        verify(feedbackFactory, times(1)).createResponseList(feedbacks);
    }

    @Test
    void getFeedbackById_ShouldReturnFeedback_WhenExists() {
        // Given
        String feedbackId = "507f1f77bcf86cd799439011";
        when(feedbackRepository.findById(feedbackId)).thenReturn(Optional.of(feedback));
        when(feedbackFactory.createResponse(feedback)).thenReturn(feedbackResponse);

        // When
        FeedbackResponse result = feedbackService.getFeedbackById(feedbackId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(feedbackId);
        assertThat(result.getUserId()).isEqualTo("user123");

        verify(feedbackRepository, times(1)).findById(feedbackId);
        verify(feedbackFactory, times(1)).createResponse(feedback);
    }

    @Test
    void getFeedbackById_ShouldThrowException_WhenNotFound() {
        // Given
        String feedbackId = "507f1f77bcf86cd799439011";
        when(feedbackRepository.findById(feedbackId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> feedbackService.getFeedbackById(feedbackId))
                .isInstanceOf(FeedbackNotFoundException.class)
                .hasMessageContaining("Feedback non trouvé avec l'ID: " + feedbackId);

        verify(feedbackRepository, times(1)).findById(feedbackId);
        verify(feedbackFactory, never()).createResponse(any());
    }

    @Test
    void getFeedbacksByUserId_ShouldReturnUserFeedbacks() {
        // Given
        List<Feedback> feedbacks = Arrays.asList(feedback);
        List<FeedbackResponse> responses = Arrays.asList(feedbackResponse);

        when(feedbackRepository.findByUserIdOrderByDateFeedbackDesc("user123")).thenReturn(feedbacks);
        when(feedbackFactory.createResponseList(feedbacks)).thenReturn(responses);

        // When
        List<FeedbackResponse> result = feedbackService.getFeedbacksByUserId("user123");

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo("user123");

        verify(feedbackRepository, times(1)).findByUserIdOrderByDateFeedbackDesc("user123");
        verify(feedbackFactory, times(1)).createResponseList(feedbacks);
    }

    @Test
    void getFeedbacksByRecetteId_ShouldReturnRecetteFeedbacks() {
        // Given
        List<Feedback> feedbacks = Arrays.asList(feedback);
        List<FeedbackResponse> responses = Arrays.asList(feedbackResponse);

        when(feedbackRepository.findByRecetteIdOrderByDateFeedbackDesc("recette456")).thenReturn(feedbacks);
        when(feedbackFactory.createResponseList(feedbacks)).thenReturn(responses);

        // When
        List<FeedbackResponse> result = feedbackService.getFeedbacksByRecetteId("recette456");

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRecetteId()).isEqualTo("recette456");

        verify(feedbackRepository, times(1)).findByRecetteIdOrderByDateFeedbackDesc("recette456");
        verify(feedbackFactory, times(1)).createResponseList(feedbacks);
    }

    @Test
    void getAverageRatingByRecetteId_ShouldReturnStatistics() {
        // Given
        String recetteId = "recette456";
        Double averageRating = 4.5;
        Long totalFeedbacks = 10L;

        AverageRatingResponse expectedResponse = AverageRatingResponse.builder()
                .recetteId(recetteId)
                .averageRating(averageRating)
                .totalFeedbacks(totalFeedbacks)
                .build();

        when(feedbackRepository.findAverageRatingByRecetteId(recetteId)).thenReturn(averageRating);
        when(feedbackRepository.countByRecetteId(recetteId)).thenReturn(totalFeedbacks);
        when(feedbackFactory.createAverageRatingResponse(recetteId, averageRating, totalFeedbacks))
                .thenReturn(expectedResponse);

        // When
        AverageRatingResponse result = feedbackService.getAverageRatingByRecetteId(recetteId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRecetteId()).isEqualTo(recetteId);
        assertThat(result.getAverageRating()).isEqualTo(averageRating);
        assertThat(result.getTotalFeedbacks()).isEqualTo(totalFeedbacks);

        verify(feedbackRepository, times(1)).findAverageRatingByRecetteId(recetteId);
        verify(feedbackRepository, times(1)).countByRecetteId(recetteId);
        verify(feedbackFactory, times(1)).createAverageRatingResponse(recetteId, averageRating, totalFeedbacks);
    }

    @Test
    void updateFeedback_ShouldReturnUpdatedFeedback() {
        // Given
        String feedbackId = "507f1f77bcf86cd799439011";
        FeedbackUpdateRequest updateRequest = FeedbackUpdateRequest.builder()
                .evaluation(4)
                .commentaire("Mise à jour du commentaire")
                .build();

        Feedback updatedFeedback = Feedback.builder()
                .id(feedbackId)
                .userId("user123")
                .recetteId("recette456")
                .evaluation(4)
                .commentaire("Mise à jour du commentaire")
                .dateFeedback(feedback.getDateFeedback())
                .dateModification(LocalDateTime.now())
                .build();

        when(feedbackRepository.findById(feedbackId)).thenReturn(Optional.of(feedback));
        when(feedbackFactory.createUpdatedFeedback(feedback, 4, "Mise à jour du commentaire"))
                .thenReturn(updatedFeedback);
        when(feedbackRepository.save(updatedFeedback)).thenReturn(updatedFeedback);
        when(feedbackFactory.createResponse(updatedFeedback)).thenReturn(feedbackResponse);

        // When
        FeedbackResponse result = feedbackService.updateFeedback(feedbackId, updateRequest);

        // Then
        assertThat(result).isNotNull();

        verify(feedbackRepository, times(1)).findById(feedbackId);
        verify(feedbackFactory, times(1)).createUpdatedFeedback(feedback, 4, "Mise à jour du commentaire");
        verify(feedbackRepository, times(1)).save(updatedFeedback);
        verify(feedbackFactory, times(1)).createResponse(updatedFeedback);
    }

    @Test
    void deleteFeedback_ShouldDeleteFeedback_WhenExists() {
        // Given
        String feedbackId = "507f1f77bcf86cd799439011";
        when(feedbackRepository.existsById(feedbackId)).thenReturn(true);
        doNothing().when(feedbackRepository).deleteById(feedbackId);

        // When
        feedbackService.deleteFeedback(feedbackId);

        // Then
        verify(feedbackRepository, times(1)).existsById(feedbackId);
        verify(feedbackRepository, times(1)).deleteById(feedbackId);
    }

    @Test
    void deleteFeedback_ShouldThrowException_WhenNotFound() {
        // Given
        String feedbackId = "507f1f77bcf86cd799439011";
        when(feedbackRepository.existsById(feedbackId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> feedbackService.deleteFeedback(feedbackId))
                .isInstanceOf(FeedbackNotFoundException.class)
                .hasMessageContaining("Feedback non trouvé avec l'ID: " + feedbackId);

        verify(feedbackRepository, times(1)).existsById(feedbackId);
        verify(feedbackRepository, never()).deleteById(feedbackId);
    }

    @Test
    void sendFeedbacksToRecommendationService_ShouldSendFeedbacks_WhenFeedbacksExist() {
        // Given
        List<Feedback> recentFeedbacks = Arrays.asList(feedback);
        when(feedbackRepository.findTop100ByOrderByDateFeedbackDesc()).thenReturn(recentFeedbacks);
        when(restTemplate.postForObject(anyString(), any(), eq(String.class))).thenReturn("Success");

        // When
        feedbackService.sendFeedbacksToRecommendationService();

        // Then
        verify(feedbackRepository, times(1)).findTop100ByOrderByDateFeedbackDesc();
        verify(restTemplate, times(1)).postForObject(anyString(), eq(recentFeedbacks), eq(String.class));
    }

    @Test
    void sendFeedbacksToRecommendationService_ShouldNotSend_WhenNoFeedbacks() {
        // Given
        when(feedbackRepository.findTop100ByOrderByDateFeedbackDesc()).thenReturn(Arrays.asList());

        // When
        feedbackService.sendFeedbacksToRecommendationService();

        // Then
        verify(feedbackRepository, times(1)).findTop100ByOrderByDateFeedbackDesc();
        verify(restTemplate, never()).postForObject(anyString(), any(), any());
    }
}