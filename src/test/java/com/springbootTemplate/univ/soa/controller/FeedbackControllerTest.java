package com.springbootTemplate.univ.soa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbootTemplate.univ.soa.dto.*;
import com.springbootTemplate.univ.soa.service.FeedbackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FeedbackController.class)
public class FeedbackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FeedbackService feedbackService;

    @Autowired
    private ObjectMapper objectMapper;

    private FeedbackResponse feedbackResponse;
    private FeedbackCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        feedbackResponse = FeedbackResponse.builder()
                .id(1L)
                .utilisateurId(10L)
                .recetteId(20L)
                .evaluation(5)
                .commentaire("Super recette !")
                .dateFeedback(LocalDateTime.now())
                .build();

        createRequest = new FeedbackCreateRequest();
        createRequest.setUtilisateurId(10L);
        createRequest.setRecetteId(20L);
        createRequest.setEvaluation(5);
        createRequest.setCommentaire("Super recette !");
    }

    @Test
    void createFeedback_ShouldReturnCreated() throws Exception {
        when(feedbackService.createFeedback(any(FeedbackCreateRequest.class))).thenReturn(feedbackResponse);

        mockMvc.perform(post("/api/feedbacks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.commentaire").value("Super recette !"));

        verify(feedbackService, times(1)).createFeedback(any(FeedbackCreateRequest.class));
    }

    @Test
    void getAllFeedbacks_ShouldReturnList() throws Exception {
        List<FeedbackResponse> list = Arrays.asList(feedbackResponse);
        when(feedbackService.getAllFeedbacks()).thenReturn(list);

        mockMvc.perform(get("/api/feedbacks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void getFeedbackById_ShouldReturnFeedback() throws Exception {
        when(feedbackService.getFeedbackById("1")).thenReturn(feedbackResponse);

        mockMvc.perform(get("/api/feedbacks/{id}", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getFeedbacksByRecetteId_ShouldReturnList() throws Exception {
        List<FeedbackResponse> list = Arrays.asList(feedbackResponse);
        when(feedbackService.getFeedbacksByRecetteId("20")).thenReturn(list);

        mockMvc.perform(get("/api/feedbacks/recette/{recetteId}", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void getAverageRatingByRecetteId_ShouldReturnAverage() throws Exception {
        AverageRatingResponse avgResponse = AverageRatingResponse.builder()
                .recetteId(20L)
                .averageRating(4.5)
                .totalFeedbacks(10L)
                .build();

        when(feedbackService.getAverageRatingByRecetteId("20")).thenReturn(avgResponse);

        mockMvc.perform(get("/api/feedbacks/recette/{recetteId}/average", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageRating").value(4.5));
    }

    @Test
    void updateFeedback_ShouldReturnUpdated() throws Exception {
        FeedbackUpdateRequest updateRequest = new FeedbackUpdateRequest();
        updateRequest.setCommentaire("Updated");

        FeedbackResponse updatedResponse = FeedbackResponse.builder().id(1L).commentaire("Updated").build();

        when(feedbackService.updateFeedback(eq("1"), any(FeedbackUpdateRequest.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/api/feedbacks/{id}", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentaire").value("Updated"));
    }

    @Test
    void deleteFeedback_ShouldReturnNoContent() throws Exception {
        doNothing().when(feedbackService).deleteFeedback("1");

        mockMvc.perform(delete("/api/feedbacks/{id}", "1"))
                .andExpect(status().isNoContent());

        verify(feedbackService, times(1)).deleteFeedback("1");
    }
}