package com.springbootTemplate.univ.soa.service;

import com.springbootTemplate.univ.soa.dto.*;
import com.springbootTemplate.univ.soa.model.Feedback;

import java.util.List;

public interface FeedbackService {

    FeedbackResponseDto createFeedback(FeedbackCreateDto feedbackCreateDto);

    List<FeedbackResponseDto> getAllFeedbacks();

    FeedbackResponseDto getFeedbackById(Long id);

    List<FeedbackResponseDto> getFeedbacksByUserId(String userId);

    List<FeedbackResponseDto> getFeedbacksByRecetteId(String recetteId);

    AverageRatingDto getAverageRatingByRecetteId(String recetteId);

    FeedbackResponseDto updateFeedback(Long id, FeedbackUpdateDto feedbackUpdateDto);

    void deleteFeedback(Long id);

    void sendFeedbacksToRecommendationService();
}