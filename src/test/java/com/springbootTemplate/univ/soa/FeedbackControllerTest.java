package com.springbootTemplate.univ.soa;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbootTemplate.univ.soa.controller.FeedbackController;
import com.springbootTemplate.univ.soa.dto.FeedbackCreateRequest;
import com.springbootTemplate.univ.soa.dto.FeedbackResponse;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
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

    @BeforeEach
    void setUp() {
        feedbackResponse = new FeedbackResponse();
        feedbackResponse.setId("507f1f77bcf86cd799439011"); // MongoDB ObjectId
        feedbackResponse.setUserId("user123");
        feedbackResponse.setRecetteId("recette456");
        feedbackResponse.setEvaluation(5);
        feedbackResponse.setCommentaire("Excellente recette !");
        feedbackResponse.setDateFeedback(LocalDateTime.now());

        feedbackCreateRequest = new FeedbackCreateRequest();
        feedbackCreateRequest.setUserId("user123");
        feedbackCreateRequest.setRecetteId("recette456");
        feedbackCreateRequest.setEvaluation(5);
        feedbackCreateRequest.setCommentaire("Excellente recette !");
    }

    @Test
    void createFeedback_ShouldReturnCreated() throws Exception {
        // Given
        when(feedbackService.createFeedback(any(FeedbackCreateRequest.class))).thenReturn(feedbackResponse);

        // When & Then
        mockMvc.perform(post("/api/feedbacks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(feedbackCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("507f1f77bcf86cd799439011"))
                .andExpect(jsonPath("$.userId").value("user123"))
                .andExpect(jsonPath("$.recetteId").value("recette456"))
                .andExpect(jsonPath("$.evaluation").value(5));
    }

    @Test
    void createFeedback_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
        // Given - Invalid evaluation (0)
        feedbackCreateRequest.setEvaluation(0);

        // When & Then
        mockMvc.perform(post("/api/feedbacks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(feedbackCreateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllFeedbacks_ShouldReturnListOfFeedbacks() throws Exception {
        // Given
        when(feedbackService.getAllFeedbacks()).thenReturn(Arrays.asList(feedbackResponse));

        // When & Then
        mockMvc.perform(get("/api/feedbacks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("507f1f77bcf86cd799439011"))
                .andExpect(jsonPath("$[0].userId").value("user123"));
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
    void deleteFeedback_ShouldReturnNoContent() throws Exception {
        // Given
        String feedbackId = "507f1f77bcf86cd799439011";

        // When & Then
        mockMvc.perform(delete("/api/feedbacks/" + feedbackId))
                .andExpect(status().isNoContent());
    }
}