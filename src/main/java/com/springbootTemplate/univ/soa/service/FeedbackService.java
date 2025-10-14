package com.springbootTemplate.univ.soa.service;

import com.springbootTemplate.univ.soa.dto.*;

import java.util.List;

public interface FeedbackService {

    FeedbackResponse createFeedback(FeedbackCreateRequest feedbackCreateRequest);

    List<FeedbackResponse> getAllFeedbacks();

    FeedbackResponse getFeedbackById(String id);

    List<FeedbackResponse> getFeedbacksByUserId(String userId);

    List<FeedbackResponse> getFeedbacksByRecetteId(String recetteId);

    AverageRatingResponse getAverageRatingByRecetteId(String recetteId);

    FeedbackResponse updateFeedback(String id, FeedbackUpdateRequest feedbackUpdateRequest);

    void deleteFeedback(String id);

    void sendFeedbacksToRecommendationService();
}