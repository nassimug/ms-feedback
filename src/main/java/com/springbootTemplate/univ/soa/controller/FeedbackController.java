package com.springbootTemplate.univ.soa.controller;

import com.springbootTemplate.univ.soa.dto.*;
import com.springbootTemplate.univ.soa.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedbacks")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<FeedbackResponse> createFeedback(@RequestBody FeedbackCreateRequest feedbackCreateRequest) {
        log.info("POST /api/feedbacks - Création d'un feedback");
        FeedbackResponse response = feedbackService.createFeedback(feedbackCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<FeedbackResponse>> getAllFeedbacks() {
        log.info("GET /api/feedbacks - Récupération de tous les feedbacks");
        List<FeedbackResponse> feedbacks = feedbackService.getAllFeedbacks();
        return ResponseEntity.ok(feedbacks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeedbackResponse> getFeedbackById(@PathVariable String id) {
        log.info("GET /api/feedbacks/{} - Récupération du feedback", id);
        FeedbackResponse feedback = feedbackService.getFeedbackById(id);
        return ResponseEntity.ok(feedback);
    }

    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<List<FeedbackResponse>> getFeedbacksByUtilisateurId(@PathVariable String utilisateurId) {
        log.info("GET /api/feedbacks/utilisateur/{} - Récupération des feedbacks de l'utilisateur", utilisateurId);
        List<FeedbackResponse> feedbacks = feedbackService.getFeedbacksByUtilisateurId(utilisateurId);
        return ResponseEntity.ok(feedbacks);
    }

    @GetMapping("/recette/{recetteId}")
    public ResponseEntity<List<FeedbackResponse>> getFeedbacksByRecetteId(@PathVariable String recetteId) {
        log.info("GET /api/feedbacks/recette/{} - Récupération des feedbacks de la recette", recetteId);
        List<FeedbackResponse> feedbacks = feedbackService.getFeedbacksByRecetteId(recetteId);
        return ResponseEntity.ok(feedbacks);
    }

    @GetMapping("/recette/{recetteId}/average")
    public ResponseEntity<AverageRatingResponse> getAverageRatingByRecetteId(@PathVariable String recetteId) {
        log.info("GET /api/feedbacks/recette/{}/average - Calcul de la note moyenne", recetteId);
        AverageRatingResponse averageRating = feedbackService.getAverageRatingByRecetteId(recetteId);
        return ResponseEntity.ok(averageRating);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FeedbackResponse> updateFeedback(
            @PathVariable String id,
            @RequestBody FeedbackUpdateRequest feedbackUpdateRequest) {
        log.info("PUT /api/feedbacks/{} - Mise à jour du feedback", id);
        FeedbackResponse response = feedbackService.updateFeedback(id, feedbackUpdateRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable String id) {
        log.info("DELETE /api/feedbacks/{} - Suppression du feedback", id);
        feedbackService.deleteFeedback(id);
        return ResponseEntity.noContent().build();
    }
}