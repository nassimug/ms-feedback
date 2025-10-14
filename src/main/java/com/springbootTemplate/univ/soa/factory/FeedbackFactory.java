package com.springbootTemplate.univ.soa.factory;

import com.springbootTemplate.univ.soa.dto.FeedbackCreateRequest;
import com.springbootTemplate.univ.soa.dto.AverageRatingResponse;
import com.springbootTemplate.univ.soa.dto.FeedbackResponse;
import com.springbootTemplate.univ.soa.model.Feedback;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Factory pour créer les objets Feedback et leurs DTOs
 * Centralise toute la logique de création d'objets
 */
@Component
public class FeedbackFactory {

    /**
     * Crée une nouvelle entité Feedback à partir d'une requête de création
     * Utilise le pattern Builder de Lombok
     */
    public Feedback createFeedback(FeedbackCreateRequest request) {
        return Feedback.builder()
                .userId(request.getUserId())
                .recetteId(request.getRecetteId())
                .evaluation(request.getEvaluation())
                .commentaire(request.getCommentaire())
                .dateFeedback(LocalDateTime.now())
                .dateModification(LocalDateTime.now())
                .build();
    }

    /**
     * Crée une réponse FeedbackResponse à partir d'une entité Feedback
     */
    public FeedbackResponse createResponse(Feedback feedback) {
        return FeedbackResponse.builder()
                .id(feedback.getId())
                .userId(feedback.getUserId())
                .recetteId(feedback.getRecetteId())
                .evaluation(feedback.getEvaluation())
                .commentaire(feedback.getCommentaire())
                .dateFeedback(feedback.getDateFeedback())
                .dateModification(feedback.getDateModification())
                .build();
    }

    /**
     * Crée une liste de FeedbackResponse à partir d'une liste de Feedback
     */
    public List<FeedbackResponse> createResponseList(List<Feedback> feedbacks) {
        return feedbacks.stream()
                .map(this::createResponse)
                .collect(Collectors.toList());
    }

    /**
     * Crée une réponse AverageRatingResponse avec les statistiques d'une recette
     */
    public AverageRatingResponse createAverageRatingResponse(String recetteId, Double averageRating, Long totalFeedbacks) {
        return AverageRatingResponse.builder()
                .recetteId(recetteId)
                .averageRating(averageRating != null ? roundToTwoDecimals(averageRating) : 0.0)
                .totalFeedbacks(totalFeedbacks != null ? totalFeedbacks : 0L)
                .build();
    }

    /**
     * Crée un Feedback mis à jour (copie avec modifications)
     */
    public Feedback createUpdatedFeedback(Feedback original, Integer newEvaluation, String newCommentaire) {
        return Feedback.builder()
                .id(original.getId())
                .userId(original.getUserId())
                .recetteId(original.getRecetteId())
                .evaluation(newEvaluation != null ? newEvaluation : original.getEvaluation())
                .commentaire(newCommentaire != null ? newCommentaire : original.getCommentaire())
                .dateFeedback(original.getDateFeedback())
                .dateModification(LocalDateTime.now())
                .build();
    }

    /**
     * Arrondit un nombre à 2 décimales
     */
    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}