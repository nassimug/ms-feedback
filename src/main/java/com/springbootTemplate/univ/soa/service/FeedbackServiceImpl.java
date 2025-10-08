package com.springbootTemplate.univ.soa.service;

import com.springbootTemplate.univ.soa.dto.*;
import com.springbootTemplate.univ.soa.exception.FeedbackNotFoundException;
import com.springbootTemplate.univ.soa.model.Feedback;
import com.springbootTemplate.univ.soa.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final RestTemplate restTemplate;

    @Value("${recommendation.service.url}")
    private String recommendationServiceUrl;

    @Override
    public FeedbackResponseDto createFeedback(FeedbackCreateDto feedbackCreateDto) {
        log.info("Création d'un nouveau feedback pour la recette: {}", feedbackCreateDto.getRecetteId());

        Feedback feedback = new Feedback();
        feedback.setUserId(feedbackCreateDto.getUserId());
        feedback.setRecetteId(feedbackCreateDto.getRecetteId());
        feedback.setEvaluation(feedbackCreateDto.getEvaluation());
        feedback.setCommentaire(feedbackCreateDto.getCommentaire());

        Feedback savedFeedback = feedbackRepository.save(feedback);
        log.info("✅ Feedback créé avec succès - ID: {}", savedFeedback.getId());

        return convertToDto(savedFeedback);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedbackResponseDto> getAllFeedbacks() {
        log.info("Récupération de tous les feedbacks");
        return feedbackRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FeedbackResponseDto getFeedbackById(Long id) {
        log.info("Récupération du feedback avec l'ID: {}", id);
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new FeedbackNotFoundException("Feedback non trouvé avec l'ID: " + id));
        return convertToDto(feedback);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedbackResponseDto> getFeedbacksByUserId(String userId) {
        log.info("Récupération des feedbacks de l'utilisateur: {}", userId);
        return feedbackRepository.findByUserIdOrderByDateFeedbackDesc(userId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedbackResponseDto> getFeedbacksByRecetteId(String recetteId) {
        log.info("Récupération des feedbacks de la recette: {}", recetteId);
        return feedbackRepository.findByRecetteIdOrderByDateFeedbackDesc(recetteId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AverageRatingDto getAverageRatingByRecetteId(String recetteId) {
        log.info("Calcul de la note moyenne pour la recette: {}", recetteId);

        Double averageRating = feedbackRepository.findAverageRatingByRecetteId(recetteId);
        Long totalFeedbacks = feedbackRepository.countByRecetteId(recetteId);

        AverageRatingDto dto = new AverageRatingDto();
        dto.setRecetteId(recetteId);
        dto.setAverageRating(averageRating != null ? Math.round(averageRating * 100.0) / 100.0 : 0.0);
        dto.setTotalFeedbacks(totalFeedbacks != null ? totalFeedbacks : 0L);

        return dto;
    }

    @Override
    public FeedbackResponseDto updateFeedback(Long id, FeedbackUpdateDto feedbackUpdateDto) {
        log.info("Mise à jour du feedback avec l'ID: {}", id);

        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new FeedbackNotFoundException("Feedback non trouvé avec l'ID: " + id));

        if (feedbackUpdateDto.getEvaluation() != null) {
            feedback.setEvaluation(feedbackUpdateDto.getEvaluation());
        }
        if (feedbackUpdateDto.getCommentaire() != null) {
            feedback.setCommentaire(feedbackUpdateDto.getCommentaire());
        }

        Feedback updatedFeedback = feedbackRepository.save(feedback);
        log.info("✅ Feedback mis à jour avec succès - ID: {}", updatedFeedback.getId());

        return convertToDto(updatedFeedback);
    }

    @Override
    public void deleteFeedback(Long id) {
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

            String url = recommendationServiceUrl + "/api/recommendations/update-model";
            restTemplate.postForObject(url, recentFeedbacks, String.class);

            log.info("✅ {} feedbacks envoyés au service de recommandation", recentFeedbacks.size());
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'envoi des feedbacks au service de recommandation: {}", e.getMessage());
            throw new RuntimeException("Erreur lors de la communication avec le service de recommandation", e);
        }
    }

    private FeedbackResponseDto convertToDto(Feedback feedback) {
        FeedbackResponseDto dto = new FeedbackResponseDto();
        dto.setId(feedback.getId());
        dto.setUserId(feedback.getUserId());
        dto.setRecetteId(feedback.getRecetteId());
        dto.setEvaluation(feedback.getEvaluation());
        dto.setCommentaire(feedback.getCommentaire());
        dto.setDateFeedback(feedback.getDateFeedback());
        dto.setDateModification(feedback.getDateModification());
        return dto;
    }
}