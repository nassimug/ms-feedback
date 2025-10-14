package com.springbootTemplate.univ.soa.controller;

import com.springbootTemplate.univ.soa.dto.*;
import com.springbootTemplate.univ.soa.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@Tag(name = "Feedbacks", description = "API de gestion des feedbacks utilisateurs")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @Operation(
            summary = "Créer un nouveau feedback",
            description = "Permet à un utilisateur de créer un feedback pour une recette avec une note de 1 à 5 étoiles et un commentaire optionnel"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Feedback créé avec succès",
                    content = @Content(schema = @Schema(implementation = FeedbackResponse.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @PostMapping
    public ResponseEntity<FeedbackResponse> createFeedback(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Détails du feedback à créer",
                    required = true,
                    content = @Content(schema = @Schema(implementation = FeedbackCreateRequest.class))
            )
            @Valid @RequestBody FeedbackCreateRequest feedbackCreateRequest) {
        log.info("POST /api/feedbacks - Création d'un feedback");
        FeedbackResponse response = feedbackService.createFeedback(feedbackCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Récupérer tous les feedbacks",
            description = "Retourne la liste complète de tous les feedbacks enregistrés dans le système"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des feedbacks récupérée avec succès",
                    content = @Content(schema = @Schema(implementation = FeedbackResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<FeedbackResponse>> getAllFeedbacks() {
        log.info("GET /api/feedbacks - Récupération de tous les feedbacks");
        List<FeedbackResponse> feedbacks = feedbackService.getAllFeedbacks();
        return ResponseEntity.ok(feedbacks);
    }

    @Operation(
            summary = "Récupérer un feedback par son ID",
            description = "Retourne les détails d'un feedback spécifique en utilisant son identifiant unique"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Feedback trouvé",
                    content = @Content(schema = @Schema(implementation = FeedbackResponse.class))),
            @ApiResponse(responseCode = "404", description = "Feedback non trouvé")
    })
    @GetMapping("/{id}")
    public ResponseEntity<FeedbackResponse> getFeedbackById(
            @Parameter(description = "ID du feedback à récupérer", required = true, example = "507f1f77bcf86cd799439011")
            @PathVariable String id) {
        log.info("GET /api/feedbacks/{} - Récupération du feedback", id);
        FeedbackResponse feedback = feedbackService.getFeedbackById(id);
        return ResponseEntity.ok(feedback);
    }

    @Operation(
            summary = "Récupérer les feedbacks d'un utilisateur",
            description = "Retourne tous les feedbacks créés par un utilisateur spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Feedbacks de l'utilisateur récupérés",
                    content = @Content(schema = @Schema(implementation = FeedbackResponse.class)))
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FeedbackResponse>> getFeedbacksByUserId(
            @Parameter(description = "ID de l'utilisateur", required = true, example = "user1")
            @PathVariable String userId) {
        log.info("GET /api/feedbacks/user/{} - Récupération des feedbacks de l'utilisateur", userId);
        List<FeedbackResponse> feedbacks = feedbackService.getFeedbacksByUserId(userId);
        return ResponseEntity.ok(feedbacks);
    }

    @Operation(
            summary = "Récupérer les feedbacks d'une recette",
            description = "Retourne tous les feedbacks associés à une recette spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Feedbacks de la recette récupérés",
                    content = @Content(schema = @Schema(implementation = FeedbackResponse.class)))
    })
    @GetMapping("/recette/{recetteId}")
    public ResponseEntity<List<FeedbackResponse>> getFeedbacksByRecetteId(
            @Parameter(description = "ID de la recette", required = true, example = "recette1")
            @PathVariable String recetteId) {
        log.info("GET /api/feedbacks/recette/{} - Récupération des feedbacks de la recette", recetteId);
        List<FeedbackResponse> feedbacks = feedbackService.getFeedbacksByRecetteId(recetteId);
        return ResponseEntity.ok(feedbacks);
    }

    @Operation(
            summary = "Calculer la note moyenne d'une recette",
            description = "Retourne la note moyenne et le nombre total de feedbacks pour une recette donnée"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistiques calculées avec succès",
                    content = @Content(schema = @Schema(implementation = AverageRatingResponse.class)))
    })
    @GetMapping("/recette/{recetteId}/average")
    public ResponseEntity<AverageRatingResponse> getAverageRatingByRecetteId(
            @Parameter(description = "ID de la recette", required = true, example = "recette1")
            @PathVariable String recetteId) {
        log.info("GET /api/feedbacks/recette/{}/average - Calcul de la note moyenne", recetteId);
        AverageRatingResponse averageRating = feedbackService.getAverageRatingByRecetteId(recetteId);
        return ResponseEntity.ok(averageRating);
    }

    @Operation(
            summary = "Mettre à jour un feedback",
            description = "Permet de modifier l'évaluation et/ou le commentaire d'un feedback existant"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Feedback mis à jour avec succès",
                    content = @Content(schema = @Schema(implementation = FeedbackResponse.class))),
            @ApiResponse(responseCode = "404", description = "Feedback non trouvé"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PutMapping("/{id}")
    public ResponseEntity<FeedbackResponse> updateFeedback(
            @Parameter(description = "ID du feedback à mettre à jour", required = true, example = "507f1f77bcf86cd799439011")
            @PathVariable String id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nouvelles valeurs pour le feedback",
                    required = true,
                    content = @Content(schema = @Schema(implementation = FeedbackUpdateRequest.class))
            )
            @Valid @RequestBody FeedbackUpdateRequest feedbackUpdateRequest) {
        log.info("PUT /api/feedbacks/{} - Mise à jour du feedback", id);
        FeedbackResponse response = feedbackService.updateFeedback(id, feedbackUpdateRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Supprimer un feedback",
            description = "Supprime définitivement un feedback du système"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Feedback supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Feedback non trouvé")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedback(
            @Parameter(description = "ID du feedback à supprimer", required = true, example = "507f1f77bcf86cd799439011")
            @PathVariable String id) {
        log.info("DELETE /api/feedbacks/{} - Suppression du feedback", id);
        feedbackService.deleteFeedback(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Envoyer les feedbacks au service de recommandation",
            description = "⚠️ En développement - Envoie les feedbacks récents au service d'apprentissage par renforcement (RL) pour améliorer les recommandations"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Feedbacks envoyés avec succès"),
            @ApiResponse(responseCode = "503", description = "Service de recommandation indisponible")
    })
    @PostMapping("/send-to-recommendation")
    public ResponseEntity<String> sendFeedbacksToRecommendation() {
        log.info("POST /api/feedbacks/send-to-recommendation - Envoi des feedbacks au service RL");

        try {
            feedbackService.sendFeedbacksToRecommendationService();
            return ResponseEntity.ok("✅ Feedbacks envoyés avec succès au service de recommandation");
        } catch (Exception e) {
            log.warn("⚠️ Service de recommandation non disponible: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("⚠️ Service de recommandation temporairement indisponible. Les feedbacks sont sauvegardés et seront synchronisés ultérieurement.");
        }
    }

    @Operation(
            summary = "Health check du service",
            description = "Vérifie que le microservice Feedback est opérationnel"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service en bonne santé")
    })
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("✅ Microservice Feedback is healthy");
    }
}