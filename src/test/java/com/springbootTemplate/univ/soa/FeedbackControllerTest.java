package com.springbootTemplate.univ.soa;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbootTemplate.univ.soa.controller.FeedbackController;
import com.springbootTemplate.univ.soa.dto.FeedbackCreateDto;
import com.springbootTemplate.univ.soa.dto.FeedbackResponseDto;
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

    private FeedbackResponseDto feedbackResponseDto;
    private FeedbackCreateDto feedbackCreateDto;

    @BeforeEach
    void setUp() {
        feedbackResponseDto = new FeedbackResponseDto();
        feedbackResponseDto.setId(1L);
        feedbackResponseDto.setUserId("user123");
        feedbackResponseDto.setRecetteId("recette456");
        feedbackResponseDto.setEvaluation(5);
        feedbackResponseDto.setCommentaire("Excellente recette !");
        feedbackResponseDto.setDateFeedback(LocalDateTime.now());

        feedbackCreateDto = new FeedbackCreateDto();
        feedbackCreateDto.setUserId("user123");
        feedbackCreateDto.setRecetteId("recette456");
        feedbackCreateDto.setEvaluation(5);
        feedbackCreateDto.setCommentaire("Excellente recette !");
    }

    @Test
    void createFeedback_ShouldReturnCreated() throws Exception {
        // Given
        when(feedbackService.createFeedback(any(FeedbackCreateDto.class))).thenReturn(feedbackResponseDto);

        // When & Then
        mockMvc.perform(post("/api/feedbacks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(feedbackCreateDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userId").value("user123"))
                .andExpect(jsonPath("$.recetteId").value("recette456"))
                .andExpect(jsonPath("$.evaluation").value(5));
    }

    @Test
    void createFeedback_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
        // Given - Invalid evaluation (0)
        feedbackCreateDto.setEvaluation(0);

        // When & Then
        mockMvc.perform(post("/api/feedbacks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(feedbackCreateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllFeedbacks_ShouldReturnListOfFeedbacks() throws Exception {
        // Given
        when(feedbackService.getAllFeedbacks()).thenReturn(Arrays.asList(feedbackResponseDto));

        // When & Then
        mockMvc.perform(get("/api/feedbacks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].userId").value("user123"));
    }

    @Test
    void getFeedbackById_ShouldReturnFeedback() throws Exception {
        // Given
        when(feedbackService.getFeedbackById(1L)).thenReturn(feedbackResponseDto);

        // When & Then
        mockMvc.perform(get("/api/feedbacks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userId").value("user123"));
    }

    @Test
    void deleteFeedback_ShouldReturnNoContent() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/feedbacks/1"))
                .andExpect(status().isNoContent());
    }
}