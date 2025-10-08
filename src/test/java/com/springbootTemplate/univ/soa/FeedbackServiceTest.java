package com.springbootTemplate.univ.soa;

import com.springbootTemplate.univ.soa.dto.FeedbackCreateDto;
import com.springbootTemplate.univ.soa.dto.FeedbackResponseDto;
import com.springbootTemplate.univ.soa.dto.FeedbackUpdateDto;
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
    private FeedbackCreateDto feedbackCreateDto;

    @BeforeEach
    void setUp() {
        feedback = new Feedback();
        feedback.setId(1L);
        feedback.setUserId("user123");
        feedback.setRecetteId("recette456");
        feedback.setEvaluation(5);
        feedback.setCommentaire("Excellente recette !");
        feedback.setDateFeedback(LocalDateTime.now());

        feedbackCreateDto = new FeedbackCreateDto();
        feedbackCreateDto.setUserId("user123");
        feedbackCreateDto.setRecetteId("recette456");
        feedbackCreateDto.setEvaluation(5);
        feedbackCreateDto.setCommentaire("Excellente recette !");
    }

    @Test
    void createFeedback_ShouldReturnCreatedFeedback() {
        // Given
        when(feedbackRepository.save(any(Feedback.class))).thenReturn(feedback);

        // When
        FeedbackResponseDto result = feedbackService.createFeedback(feedbackCreateDto);

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
        List<FeedbackResponseDto> result = feedbackService.getAllFeedbacks();

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo("user123");
        verify(feedbackRepository, times(1)).findAll();
    }

    @Test
    void getFeedbackById_ShouldReturnFeedback_WhenExists() {
        // Given
        when(feedbackRepository.findById(1L)).thenReturn(Optional.of(feedback));

        // When
        FeedbackResponseDto result = feedbackService.getFeedbackById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUserId()).isEqualTo("user123");
        verify(feedbackRepository, times(1)).findById(1L);
    }

    @Test
    void getFeedbackById_ShouldThrowException_WhenNotFound() {
        // Given
        when(feedbackRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> feedbackService.getFeedbackById(1L))
                .isInstanceOf(FeedbackNotFoundException.class)
                .hasMessageContaining("Feedback non trouvé avec l'ID: 1");
        verify(feedbackRepository, times(1)).findById(1L);
    }

    @Test
    void getFeedbacksByUserId_ShouldReturnUserFeedbacks() {
        // Given
        List<Feedback> feedbacks = Arrays.asList(feedback);
        when(feedbackRepository.findByUserIdOrderByDateFeedbackDesc("user123")).thenReturn(feedbacks);

        // When
        List<FeedbackResponseDto> result = feedbackService.getFeedbacksByUserId("user123");

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
        List<FeedbackResponseDto> result = feedbackService.getFeedbacksByRecetteId("recette456");

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRecetteId()).isEqualTo("recette456");
        verify(feedbackRepository, times(1)).findByRecetteIdOrderByDateFeedbackDesc("recette456");
    }

    @Test
    void updateFeedback_ShouldReturnUpdatedFeedback() {
        // Given
        FeedbackUpdateDto updateDto = new FeedbackUpdateDto();
        updateDto.setEvaluation(4);
        updateDto.setCommentaire("Mise à jour du commentaire");

        when(feedbackRepository.findById(1L)).thenReturn(Optional.of(feedback));
        when(feedbackRepository.save(any(Feedback.class))).thenReturn(feedback);

        // When
        FeedbackResponseDto result = feedbackService.updateFeedback(1L, updateDto);

        // Then
        assertThat(result).isNotNull();
        verify(feedbackRepository, times(1)).findById(1L);
        verify(feedbackRepository, times(1)).save(any(Feedback.class));
    }

    @Test
    void deleteFeedback_ShouldDeleteFeedback_WhenExists() {
        // Given
        when(feedbackRepository.existsById(1L)).thenReturn(true);
        doNothing().when(feedbackRepository).deleteById(1L);

        // When
        feedbackService.deleteFeedback(1L);

        // Then
        verify(feedbackRepository, times(1)).existsById(1L);
        verify(feedbackRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteFeedback_ShouldThrowException_WhenNotFound() {
        // Given
        when(feedbackRepository.existsById(1L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> feedbackService.deleteFeedback(1L))
                .isInstanceOf(FeedbackNotFoundException.class)
                .hasMessageContaining("Feedback non trouvé avec l'ID: 1");
        verify(feedbackRepository, times(1)).existsById(1L);
        verify(feedbackRepository, never()).deleteById(1L);
    }
}