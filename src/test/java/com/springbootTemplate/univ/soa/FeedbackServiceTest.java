package com.springbootTemplate.univ.soa;

import com.springbootTemplate.univ.soa.dto.FeedbackCreateRequest;
import com.springbootTemplate.univ.soa.dto.FeedbackResponse;
import com.springbootTemplate.univ.soa.dto.FeedbackUpdateRequest;
import com.springbootTemplate.univ.soa.exception.FeedbackNotFoundException;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceTest {

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private FeedbackServiceImpl feedbackService;

    private Feedback feedback;
    private FeedbackCreateRequest feedbackCreateRequest;

    @BeforeEach
    void setUp() {
        feedback = new Feedback();
        feedback.setId("507f1f77bcf86cd799439011"); // MongoDB ObjectId
        feedback.setUserId("user123");
        feedback.setRecetteId("recette456");
        feedback.setEvaluation(5);
        feedback.setCommentaire("Excellente recette !");
        feedback.setDateFeedback(LocalDateTime.now());
        feedback.setDateModification(LocalDateTime.now());

        feedbackCreateRequest = new FeedbackCreateRequest();
        feedbackCreateRequest.setUserId("user123");
        feedbackCreateRequest.setRecetteId("recette456");
        feedbackCreateRequest.setEvaluation(5);
        feedbackCreateRequest.setCommentaire("Excellente recette !");
    }

    @Test
    void createFeedback_ShouldReturnCreatedFeedback() {
        // Given
        when(feedbackRepository.save(any(Feedback.class))).thenReturn(feedback);

        // When
        FeedbackResponse result = feedbackService.createFeedback(feedbackCreateRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo("user123");
        assertThat(result.getRecetteId()).isEqualTo("recette456");
        assertThat(result.getEvaluation()).isEqualTo(5);
        verify(feedbackRepository, times(1)).save(any(Feedback.class));
    }

    @Test
    void getAllFeedbacks_ShouldReturnListOfFeedbacks() {
        // Given
        List<Feedback> feedbacks = Arrays.asList(feedback);
        when(feedbackRepository.findAll()).thenReturn(feedbacks);

        // When
        List<FeedbackResponse> result = feedbackService.getAllFeedbacks();

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo("user123");
        verify(feedbackRepository, times(1)).findAll();
    }

    @Test
    void getFeedbackById_ShouldReturnFeedback_WhenExists() {
        // Given
        String feedbackId = "507f1f77bcf86cd799439011";
        when(feedbackRepository.findById(feedbackId)).thenReturn(Optional.of(feedback));

        // When
        FeedbackResponse result = feedbackService.getFeedbackById(feedbackId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(feedbackId);
        assertThat(result.getUserId()).isEqualTo("user123");
        verify(feedbackRepository, times(1)).findById(feedbackId);
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
    }

    @Test
    void getFeedbacksByUserId_ShouldReturnUserFeedbacks() {
        // Given
        List<Feedback> feedbacks = Arrays.asList(feedback);
        when(feedbackRepository.findByUserIdOrderByDateFeedbackDesc("user123")).thenReturn(feedbacks);

        // When
        List<FeedbackResponse> result = feedbackService.getFeedbacksByUserId("user123");

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo("user123");
        verify(feedbackRepository, times(1)).findByUserIdOrderByDateFeedbackDesc("user123");
    }

    @Test
    void getFeedbacksByRecetteId_ShouldReturnRecetteFeedbacks() {
        // Given
        List<Feedback> feedbacks = Arrays.asList(feedback);
        when(feedbackRepository.findByRecetteIdOrderByDateFeedbackDesc("recette456")).thenReturn(feedbacks);

        // When
        List<FeedbackResponse> result = feedbackService.getFeedbacksByRecetteId("recette456");

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRecetteId()).isEqualTo("recette456");
        verify(feedbackRepository, times(1)).findByRecetteIdOrderByDateFeedbackDesc("recette456");
    }

    @Test
    void updateFeedback_ShouldReturnUpdatedFeedback() {
        // Given
        String feedbackId = "507f1f77bcf86cd799439011";
        FeedbackUpdateRequest updateDto = new FeedbackUpdateRequest();
        updateDto.setEvaluation(4);
        updateDto.setCommentaire("Mise à jour du commentaire");

        when(feedbackRepository.findById(feedbackId)).thenReturn(Optional.of(feedback));
        when(feedbackRepository.save(any(Feedback.class))).thenReturn(feedback);

        // When
        FeedbackResponse result = feedbackService.updateFeedback(feedbackId, updateDto);

        // Then
        assertThat(result).isNotNull();
        verify(feedbackRepository, times(1)).findById(feedbackId);
        verify(feedbackRepository, times(1)).save(any(Feedback.class));
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
}