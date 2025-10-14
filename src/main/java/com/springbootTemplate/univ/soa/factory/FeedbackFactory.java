package com.springbootTemplate.univ.soa.factory;

import com.springbootTemplate.univ.soa.dto.AverageRatingResponse;
import com.springbootTemplate.univ.soa.dto.FeedbackCreateRequest;
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
     * Crée une nouvelle entité Feedback à partir d'un DTO de création
     * Utilise le pattern Builder de Lombok
     */
    public Feedback createFeedback(FeedbackCreateRequest dto) {
        return Feedback.builder()
                .userId(dto.getUserId())
                .recetteId(dto.getRecetteId())
                .evaluation(dto.getEvaluation())
                .commentaire(dto.getCommentaire())
                .dateFeedback(LocalDateTime.now())
                .dateModification(LocalDateTime.now())
                .build();
    }

    /**
     * Crée un FeedbackResponseDto à partir d'une entité Feedback
     * Utilise le pattern Builder si le DTO l'a (optionnel)
     */
    public FeedbackResponse createResponseDto(Feedback feedback) {
        FeedbackResponse dto = new FeedbackResponse();
        dto.setId(feedback.getId());
        dto.setUserId(feedback.getUserId());
        dto.setRecetteId(feedback.getRecetteId());
        dto.setEvaluation(feedback.getEvaluation());
        dto.setCommentaire(feedback.getCommentaire());
        dto.setDateFeedback(feedback.getDateFeedback());
        dto.setDateModification(feedback.getDateModification());
        return dto;
    }

    /**
     * Crée une liste de FeedbackResponseDto à partir d'une liste de Feedback
     */
    public List<FeedbackResponse> createResponseDtoList(List<Feedback> feedbacks) {
        return feedbacks.stream()
                .map(this::createResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Crée un AverageRatingDto avec les statistiques d'une recette
     */
    public AverageRatingResponse createAverageRatingDto(String recetteId, Double averageRating, Long totalFeedbacks) {
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