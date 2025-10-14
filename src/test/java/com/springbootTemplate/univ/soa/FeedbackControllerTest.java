package com.springbootTemplate.univ.soa;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbootTemplate.univ.soa.controller.FeedbackController;
import com.springbootTemplate.univ.soa.dto.FeedbackCreateRequest;
import com.springbootTemplate.univ.soa.dto.FeedbackResponse;
import com.springbootTemplate.univ.soa.dto.FeedbackUpdateRequest;
import com.springbootTemplate.univ.soa.dto.AverageRatingResponse;
import com.springbootTemplate.univ.soa.service.FeedbackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FeedbackController.class)
class FeedbackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FeedbackService feedbackService;

    private FeedbackResponse feedbackResponse;
    private FeedbackCreateRequest feedbackCreateRequest;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        // Utilisation du Builder pour FeedbackResponse
        feedbackResponse = FeedbackResponse.builder()
                .id("507f1f77bcf86cd799439011")
                .userId("user123")
                .recetteId("recette456")
                .evaluation(5)
                .commentaire("Excellente recette !")
                .dateFeedback(now)
                .dateModification(now)
                .build();

        // Utilisation du Builder pour FeedbackCreateRequest
        feedbackCreateRequest = FeedbackCreateRequest.builder()
                .userId("user123")
                .recetteId("recette456")
                .evaluation(5)
                .commentaire("Excellente recette !")
                .build();
    }

    @Test
    void createFeedback_ShouldReturnCreated() throws Exception {
        // Given
        when(feedbackService.createFeedback(any(FeedbackCreateRequest.class)))
                .thenReturn(feedbackResponse);

        // When & Then
        mockMvc.perform(post("/api/feedbacks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(feedbackCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("507f1f77bcf86cd799439011"))
                .andExpect(jsonPath("$.userId").value("user123"))
                .andExpect(jsonPath("$.recetteId").value("recette456"))
                .andExpect(jsonPath("$.evaluation").value(5))
                .andExpect(jsonPath("$.commentaire").value("Excellente recette !"));
    }

    @Test
    void createFeedback_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
        // Given - Invalid evaluation (0)
        FeedbackCreateRequest invalidRequest = FeedbackCreateRequest.builder()
                .userId("user123")
                .recetteId("recette456")
                .evaluation(0) // Invalid: must be between 1 and 5
                .commentaire("Test")
                .build();

        // When & Then
        mockMvc.perform(post("/api/feedbacks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createFeedback_ShouldReturnBadRequest_WhenMissingRequiredFields() throws Exception {
        // Given - Missing userId
        FeedbackCreateRequest invalidRequest = FeedbackCreateRequest.builder()
                .recetteId("recette456")
                .evaluation(5)
                .build();

        // When & Then
        mockMvc.perform(post("/api/feedbacks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllFeedbacks_ShouldReturnListOfFeedbacks() throws Exception {
        // Given
        List<FeedbackResponse> feedbacks = Arrays.asList(feedbackResponse);
        when(feedbackService.getAllFeedbacks()).thenReturn(feedbacks);

        // When & Then
        mockMvc.perform(get("/api/feedbacks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("507f1f77bcf86cd799439011"))
                .andExpect(jsonPath("$[0].userId").value("user123"))
                .andExpect(jsonPath("$[0].recetteId").value("recette456"));
    }

    @Test
    void getFeedbackById_ShouldReturnFeedback() throws Exception {
        // Given
        String feedbackId = "507f1f77bcf86cd799439011";
        when(feedbackService.getFeedbackById(feedbackId)).thenReturn(feedbackResponse);

        // When & Then
        mockMvc.perform(get("/api/feedbacks/" + feedbackId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(feedbackId))
                .andExpect(jsonPath("$.userId").value("user123"));
    }

    @Test
    void getFeedbacksByUserId_ShouldReturnUserFeedbacks() throws Exception {
        // Given
        String userId = "user123";
        List<FeedbackResponse> feedbacks = Arrays.asList(feedbackResponse);
        when(feedbackService.getFeedbacksByUserId(userId)).thenReturn(feedbacks);

        // When & Then
        mockMvc.perform(get("/api/feedbacks/user/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(userId));
    }

    @Test
    void getFeedbacksByRecetteId_ShouldReturnRecetteFeedbacks() throws Exception {
        // Given
        String recetteId = "recette456";
        List<FeedbackResponse> feedbacks = Arrays.asList(feedbackResponse);
        when(feedbackService.getFeedbacksByRecetteId(recetteId)).thenReturn(feedbacks);

        // When & Then
        mockMvc.perform(get("/api/feedbacks/recette/" + recetteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].recetteId").value(recetteId));
    }

    @Test
    void getAverageRatingByRecetteId_ShouldReturnStatistics() throws Exception {
        // Given
        String recetteId = "recette456";
        AverageRatingResponse avgResponse = AverageRatingResponse.builder()
                .recetteId(recetteId)
                .averageRating(4.5)
                .totalFeedbacks(10L)
                .build();

        when(feedbackService.getAverageRatingByRecetteId(recetteId)).thenReturn(avgResponse);

        // When & Then
        mockMvc.perform(get("/api/feedbacks/recette/" + recetteId + "/average"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recetteId").value(recetteId))
                .andExpect(jsonPath("$.averageRating").value(4.5))
                .andExpect(jsonPath("$.totalFeedbacks").value(10));
    }

    @Test
    void updateFeedback_ShouldReturnUpdatedFeedback() throws Exception {
        // Given
        String feedbackId = "507f1f77bcf86cd799439011";
        FeedbackUpdateRequest updateRequest = FeedbackUpdateRequest.builder()
                .evaluation(4)
                .commentaire("Mise à jour du commentaire")
                .build();

        FeedbackResponse updatedResponse = FeedbackResponse.builder()
                .id(feedbackId)
                .userId("user123")
                .recetteId("recette456")
                .evaluation(4)
                .commentaire("Mise à jour du commentaire")
                .dateFeedback(now)
                .dateModification(LocalDateTime.now())
                .build();

        when(feedbackService.updateFeedback(eq(feedbackId), any(FeedbackUpdateRequest.class)))
                .thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/api/feedbacks/" + feedbackId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(feedbackId))
                .andExpect(jsonPath("$.evaluation").value(4))
                .andExpect(jsonPath("$.commentaire").value("Mise à jour du commentaire"));
    }

    @Test
    void deleteFeedback_ShouldReturnNoContent() throws Exception {
        // Given
        String feedbackId = "507f1f77bcf86cd799439011";
        doNothing().when(feedbackService).deleteFeedback(feedbackId);

        // When & Then
        mockMvc.perform(delete("/api/feedbacks/" + feedbackId))
                .andExpect(status().isNoContent());
    }

    @Test
    void sendFeedbacksToRecommendation_ShouldReturnSuccess() throws Exception {
        // Given
        doNothing().when(feedbackService).sendFeedbacksToRecommendationService();

        // When & Then
        mockMvc.perform(post("/api/feedbacks/send-to-recommendation"))
                .andExpect(status().isOk())
                .andExpect(content().string("✅ Feedbacks envoyés avec succès au service de recommandation"));
    }

    @Test
    void healthCheck_ShouldReturnHealthyStatus() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/feedbacks/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("✅ Microservice Feedback is healthy"));
    }
}