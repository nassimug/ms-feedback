package com.springbootTemplate.univ.soa.service;

import com.springbootTemplate.univ.soa.client.PersistanceClient;
import com.springbootTemplate.univ.soa.dto.*;
import com.springbootTemplate.univ.soa.exception.FeedbackNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedbackServiceImpl implements FeedbackService {

    private final PersistanceClient persistanceClient;
    private final RestTemplate restTemplate;

    @Value("${recommendation.service.url}")
    private String recommendationServiceUrl;

    @Override
    public FeedbackResponse createFeedback(FeedbackCreateRequest request) {
        log.info("Création d'un nouveau feedback pour la recette: {}", request.getRecetteId());

        // Validation : vérifier que l'utilisateur existe
        if (!persistanceClient.utilisateurExists(request.getUtilisateurId())) {
            throw new IllegalArgumentException("Utilisateur non trouvé avec l'ID: " + request.getUtilisateurId());
        }

        // Validation : vérifier que la recette existe
        if (!persistanceClient.recetteExists(request.getRecetteId())) {
            throw new IllegalArgumentException("Recette non trouvée avec l'ID: " + request.getRecetteId());
        }

        // Créer le DTO pour le microservice Persistance
        FeedbackDTO feedbackDTO = FeedbackDTO.builder()
                .utilisateurId(request.getUtilisateurId())
                .recetteId(request.getRecetteId())
                .evaluation(request.getEvaluation())
                .commentaire(request.getCommentaire())
                .dateFeedback(LocalDateTime.now())
                .dateModification(LocalDateTime.now())
                .build();

        // Appel au microservice Persistance
        FeedbackDTO savedFeedback = persistanceClient.createFeedback(feedbackDTO);

        log.info("Feedback créé avec succès - ID: {}", savedFeedback.getId());
        return mapToResponse(savedFeedback);
    }

    @Override
    public List<FeedbackResponse> getAllFeedbacks() {
        log.info("Récupération de tous les feedbacks");
        List<FeedbackDTO> feedbacks = persistanceClient.getAllFeedbacks();
        return feedbacks.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public FeedbackResponse getFeedbackById(String id) {
        log.info("Récupération du feedback avec l'ID: {}", id);

        try {
            Long feedbackId = Long.parseLong(id);
            FeedbackDTO feedback = persistanceClient.getFeedbackById(feedbackId);
            return mapToResponse(feedback);
        } catch (NumberFormatException e) {
            throw new FeedbackNotFoundException("Format d'ID invalide: " + id);
        } catch (RuntimeException e) {
            throw new FeedbackNotFoundException("Feedback non trouvé avec l'ID: " + id);
        }
    }

    @Override
    public List<FeedbackResponse> getFeedbacksByUserId(String userId) {
        log.info("Récupération des feedbacks de l'utilisateur: {}", userId);

        try {
            Long utilisateurId = Long.parseLong(userId);
            List<FeedbackDTO> feedbacks = persistanceClient.getFeedbacksByUtilisateurId(utilisateurId);
            return feedbacks.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Format d'ID utilisateur invalide: " + userId);
        }
    }

    @Override
    public List<FeedbackResponse> getFeedbacksByRecetteId(String recetteId) {
        log.info("Récupération des feedbacks de la recette: {}", recetteId);

        try {
            Long recetteIdLong = Long.parseLong(recetteId);
            List<FeedbackDTO> feedbacks = persistanceClient.getFeedbacksByRecetteId(recetteIdLong);
            return feedbacks.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Format d'ID recette invalide: " + recetteId);
        }
    }

    @Override
    public AverageRatingResponse getAverageRatingByRecetteId(String recetteId) {
        log.info("Calcul de la note moyenne pour la recette: {}", recetteId);

        try {
            Long recetteIdLong = Long.parseLong(recetteId);
            List<FeedbackDTO> feedbacks = persistanceClient.getFeedbacksByRecetteId(recetteIdLong);

            if (feedbacks.isEmpty()) {
                return AverageRatingResponse.builder()
                        .recetteId(recetteIdLong)
                        .averageRating(0.0)
                        .totalFeedbacks(0L)
                        .build();
            }

            double average = feedbacks.stream()
                    .mapToInt(FeedbackDTO::getEvaluation)
                    .average()
                    .orElse(0.0);

            return AverageRatingResponse.builder()
                    .recetteId(recetteIdLong)
                    .averageRating(Math.round(average * 100.0) / 100.0)
                    .totalFeedbacks((long) feedbacks.size())
                    .build();

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Format d'ID recette invalide: " + recetteId);
        }
    }

    @Override
    public FeedbackResponse updateFeedback(String id, FeedbackUpdateRequest request) {
        log.info("Mise à jour du feedback avec l'ID: {}", id);

        try {
            Long feedbackId = Long.parseLong(id);

            // Récupérer le feedback existant
            FeedbackDTO existingFeedback = persistanceClient.getFeedbackById(feedbackId);

            // Créer le DTO mis à jour
            FeedbackDTO updatedFeedback = FeedbackDTO.builder()
                    .id(existingFeedback.getId())
                    .utilisateurId(existingFeedback.getUtilisateurId())
                    .recetteId(existingFeedback.getRecetteId())
                    .evaluation(request.getEvaluation() != null ? request.getEvaluation() : existingFeedback.getEvaluation())
                    .commentaire(request.getCommentaire() != null ? request.getCommentaire() : existingFeedback.getCommentaire())
                    .dateFeedback(existingFeedback.getDateFeedback())
                    .dateModification(LocalDateTime.now())
                    .build();

            FeedbackDTO saved = persistanceClient.updateFeedback(feedbackId, updatedFeedback);
            log.info("Feedback mis à jour avec succès - ID: {}", saved.getId());
            return mapToResponse(saved);

        } catch (NumberFormatException e) {
            throw new FeedbackNotFoundException("Format d'ID invalide: " + id);
        } catch (RuntimeException e) {
            throw new FeedbackNotFoundException("Feedback non trouvé avec l'ID: " + id);
        }
    }

    @Override
    public void deleteFeedback(String id) {
        log.info("Suppression du feedback avec l'ID: {}", id);

        try {
            Long feedbackId = Long.parseLong(id);
            persistanceClient.deleteFeedback(feedbackId);
            log.info("Feedback supprimé avec succès - ID: {}", feedbackId);

        } catch (NumberFormatException e) {
            throw new FeedbackNotFoundException("Format d'ID invalide: " + id);
        } catch (RuntimeException e) {
            throw new FeedbackNotFoundException("Feedback non trouvé avec l'ID: " + id);
        }
    }

    @Override
    public void sendFeedbacksToRecommendationService() {
        log.info("Envoi des feedbacks au service de recommandation");

        try {
            // Récupérer tous les feedbacks (ou une partie selon vos besoins)
            List<FeedbackDTO> allFeedbacks = persistanceClient.getAllFeedbacks();

            if (allFeedbacks.isEmpty()) {
                log.info("Aucun feedback à envoyer");
                return;
            }

            // Limiter à 100 feedbacks les plus récents
            List<FeedbackDTO> recentFeedbacks = allFeedbacks.stream()
                    .sorted((f1, f2) -> f2.getDateFeedback().compareTo(f1.getDateFeedback()))
                    .limit(100)
                    .collect(Collectors.toList());

            String url = recommendationServiceUrl + "/api/recommendations/update-model";
            log.info("Tentative d'envoi de {} feedbacks vers {}", recentFeedbacks.size(), url);

            restTemplate.postForObject(url, recentFeedbacks, String.class);
            log.info("{} feedbacks envoyés au service de recommandation avec succès", recentFeedbacks.size());

        } catch (Exception e) {
            log.warn("Service de recommandation non accessible: {}", e.getMessage());
            throw new RuntimeException(
                    "Service de recommandation non disponible. Veuillez vérifier que le service est démarré sur " + recommendationServiceUrl,
                    e
            );
        }
    }

    // ========================================
    // MÉTHODES PRIVÉES - MAPPING
    // ========================================

    private FeedbackResponse mapToResponse(FeedbackDTO dto) {
        return FeedbackResponse.builder()
                .id(dto.getId())
                .utilisateurId(dto.getUtilisateurId())
                .recetteId(dto.getRecetteId())
                .evaluation(dto.getEvaluation())
                .commentaire(dto.getCommentaire())
                .dateFeedback(dto.getDateFeedback())
                .dateModification(dto.getDateModification())
                .build();
    }
}