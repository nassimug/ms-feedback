package com.springbootTemplate.univ.soa.service;

import com.springbootTemplate.univ.soa.dto.FeedbackCreateRequest;
import com.springbootTemplate.univ.soa.dto.FeedbackUpdateRequest;
import com.springbootTemplate.univ.soa.dto.AverageRatingResponse;
import com.springbootTemplate.univ.soa.dto.FeedbackResponse;

import java.util.List;

public interface FeedbackService {

    FeedbackResponse createFeedback(FeedbackCreateRequest request);

    List<FeedbackResponse> getAllFeedbacks();

    FeedbackResponse getFeedbackById(String id);

    List<FeedbackResponse> getFeedbacksByUtilisateurId(String utilisateurId);

    List<FeedbackResponse> getFeedbacksByRecetteId(String recetteId);

    AverageRatingResponse getAverageRatingByRecetteId(String recetteId);

    FeedbackResponse updateFeedback(String id, FeedbackUpdateRequest request);

    void deleteFeedback(String id);
}