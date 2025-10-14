package com.springbootTemplate.univ.soa.service;

import com.springbootTemplate.univ.soa.dto.*;
import com.springbootTemplate.univ.soa.exception.FeedbackNotFoundException;
import com.springbootTemplate.univ.soa.factory.FeedbackFactory;
import com.springbootTemplate.univ.soa.model.Feedback;
import com.springbootTemplate.univ.soa.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final FeedbackFactory feedbackFactory;
    private final RestTemplate restTemplate;

    @Value("${recommendation.service.url}")
    private String recommendationServiceUrl;

    @Override
    public FeedbackResponse createFeedback(FeedbackCreateRequest feedbackCreateRequest) {
        log.info("Création d'un nouveau feedback pour la recette: {}", feedbackCreateRequest.getRecetteId());

        // Utilisation de la Factory pour créer l'entité
        Feedback feedback = feedbackFactory.createFeedback(feedbackCreateRequest);
        Feedback savedFeedback = feedbackRepository.save(feedback);

        log.info("✅ Feedback créé avec succès - ID: {}", savedFeedback.getId());

        // Utilisation de la Factory pour créer le DTO de réponse
        return feedbackFactory.createResponseDto(savedFeedback);
    }

    @Override
    public List<FeedbackResponse> getAllFeedbacks() {
        log.info("Récupération de tous les feedbacks");
        return feedbackFactory.createResponseDtoList(feedbackRepository.findAll());
    }

    @Override
    public FeedbackResponse getFeedbackById(String id) {
        log.info("Récupération du feedback avec l'ID: {}", id);
        return feedbackFactory.createResponseDto(findFeedbackOrThrow(id));
    }

    @Override
    public List<FeedbackResponse> getFeedbacksByUserId(String userId) {
        log.info("Récupération des feedbacks de l'utilisateur: {}", userId);
        return feedbackFactory.createResponseDtoList(
                feedbackRepository.findByUserIdOrderByDateFeedbackDesc(userId)
        );
    }

    @Override
    public List<FeedbackResponse> getFeedbacksByRecetteId(String recetteId) {
        log.info("Récupération des feedbacks de la recette: {}", recetteId);
        return feedbackFactory.createResponseDtoList(
                feedbackRepository.findByRecetteIdOrderByDateFeedbackDesc(recetteId)
        );
    }

    @Override
    public AverageRatingResponse getAverageRatingByRecetteId(String recetteId) {
        log.info("Calcul de la note moyenne pour la recette: {}", recetteId);

        Double averageRating = feedbackRepository.findAverageRatingByRecetteId(recetteId);
        Long totalFeedbacks = feedbackRepository.countByRecetteId(recetteId);

        // Utilisation de la Factory pour créer le DTO de statistiques
        return feedbackFactory.createAverageRatingDto(recetteId, averageRating, totalFeedbacks);
    }

    @Override
    public FeedbackResponse updateFeedback(String id, FeedbackUpdateRequest feedbackUpdateRequest) {
        log.info("Mise à jour du feedback avec l'ID: {}", id);

        Feedback original = findFeedbackOrThrow(id);

        // Utilisation de la Factory pour créer un feedback mis à jour
        Feedback updatedFeedback = feedbackFactory.createUpdatedFeedback(
                original,
                feedbackUpdateRequest.getEvaluation(),
                feedbackUpdateRequest.getCommentaire()
        );

        Feedback saved = feedbackRepository.save(updatedFeedback);
        log.info("✅ Feedback mis à jour avec succès - ID: {}", saved.getId());

        return feedbackFactory.createResponseDto(saved);
    }

    @Override
    public void deleteFeedback(String id) {
        log.info("Suppression du feedback avec l'ID: {}", id);

        if (!feedbackRepository.existsById(id)) {
            throw new FeedbackNotFoundException("Feedback non trouvé avec l'ID: " + id);
        }

        feedbackRepository.deleteById(id);
        log.info("✅ Feedback supprimé avec succès - ID: {}", id);
    }

    @Override
    public void sendFeedbacksToRecommendationService() {
        log.info("Envoi des feedbacks au service de recommandation");

        try {
            List<Feedback> recentFeedbacks = feedbackRepository.findTop100ByOrderByDateFeedbackDesc();

            if (recentFeedbacks.isEmpty()) {
                log.info("ℹ️ Aucun feedback à envoyer");
                return;
            }

            sendFeedbacksToExternalService(recentFeedbacks);

            log.info("✅ {} feedbacks envoyés au service de recommandation avec succès", recentFeedbacks.size());
        } catch (ResourceAccessException e) {
            handleConnectionError(e);
        } catch (HttpClientErrorException e) {
            handleClientError(e);
        } catch (HttpServerErrorException e) {
            handleServerError(e);
        } catch (Exception e) {
            handleUnexpectedError(e);
        }
    }

    // ========================================
    // MÉTHODES PRIVÉES - UTILITAIRES
    // ========================================

    private Feedback findFeedbackOrThrow(String id) {
        return feedbackRepository.findById(id)
                .orElseThrow(() -> new FeedbackNotFoundException("Feedback non trouvé avec l'ID: " + id));
    }

    private void sendFeedbacksToExternalService(List<Feedback> feedbacks) {
        String url = recommendationServiceUrl + "/api/recommendations/update-model";
        log.info("🔄 Tentative d'envoi de {} feedbacks vers {}", feedbacks.size(), url);
        restTemplate.postForObject(url, feedbacks, String.class);
    }

    // ========================================
    // GESTION DES ERREURS HTTP
    // ========================================

    private void handleConnectionError(ResourceAccessException e) {
        log.warn("⚠️ Service de recommandation non accessible: {}", e.getMessage());
        throw new RuntimeException(
                "Service de recommandation non disponible. Veuillez vérifier que le service est démarré sur " + recommendationServiceUrl,
                e
        );
    }

    private void handleClientError(HttpClientErrorException e) {
        log.error("❌ Erreur client (status: {}): {}", e.getStatusCode(), e.getMessage());
        throw new RuntimeException("Erreur lors de l'envoi des feedbacks: " + e.getStatusCode(), e);
    }

    private void handleServerError(HttpServerErrorException e) {
        log.error("❌ Erreur serveur (status: {}): {}", e.getStatusCode(), e.getMessage());
        throw new RuntimeException("Le service de recommandation a rencontré une erreur: " + e.getStatusCode(), e);
    }

    private void handleUnexpectedError(Exception e) {
        log.error("❌ Erreur inattendue: {}", e.getMessage(), e);
        throw new RuntimeException("Erreur lors de la communication avec le service de recommandation", e);
    }
}