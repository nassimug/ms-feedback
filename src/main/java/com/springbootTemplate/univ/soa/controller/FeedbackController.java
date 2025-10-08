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
                    content = @Content(schema = @Schema(implementation = FeedbackResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @PostMapping
    public ResponseEntity<FeedbackResponseDto> createFeedback(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Détails du feedback à créer",
                    required = true,
                    content = @Content(schema = @Schema(implementation = FeedbackCreateDto.class))
            )
            @Valid @RequestBody FeedbackCreateDto feedbackCreateDto) {
        log.info("POST /api/feedbacks - Création d'un feedback");
        FeedbackResponseDto response = feedbackService.createFeedback(feedbackCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Récupérer tous les feedbacks",
            description = "Retourne la liste complète de tous les feedbacks enregistrés dans le système"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des feedbacks récupérée avec succès",
                    content = @Content(schema = @Schema(implementation = FeedbackResponseDto.class)))
    })
    @GetMapping
    public ResponseEntity<List<FeedbackResponseDto>> getAllFeedbacks() {
        log.info("GET /api/feedbacks - Récupération de tous les feedbacks");
        List<FeedbackResponseDto> feedbacks = feedbackService.getAllFeedbacks();
        return ResponseEntity.ok(feedbacks);
    }

    @Operation(
            summary = "Récupérer un feedback par son ID",
            description = "Retourne les détails d'un feedback spécifique en utilisant son identifiant unique"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Feedback trouvé",
                    content = @Content(schema = @Schema(implementation = FeedbackResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Feedback non trouvé")
    })
    @GetMapping("/{id}")
    public ResponseEntity<FeedbackResponseDto> getFeedbackById(
            @Parameter(description = "ID du feedback à récupérer", required = true, example = "1")
            @PathVariable Long id) {
        log.info("GET /api/feedbacks/{} - Récupération du feedback", id);
        FeedbackResponseDto feedback = feedbackService.getFeedbackById(id);
        return ResponseEntity.ok(feedback);
    }

    @Operation(
            summary = "Récupérer les feedbacks d'un utilisateur",
            description = "Retourne tous les feedbacks créés par un utilisateur spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Feedbacks de l'utilisateur récupérés",
                    content = @Content(schema = @Schema(implementation = FeedbackResponseDto.class)))
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FeedbackResponseDto>> getFeedbacksByUserId(
            @Parameter(description = "ID de l'utilisateur", required = true, example = "user1")
            @PathVariable String userId) {
        log.info("GET /api/feedbacks/user/{} - Récupération des feedbacks de l'utilisateur", userId);
        List<FeedbackResponseDto> feedbacks = feedbackService.getFeedbacksByUserId(userId);
        return ResponseEntity.ok(feedbacks);
    }

    @Operation(
            summary = "Récupérer les feedbacks d'une recette",
            description = "Retourne tous les feedbacks associés à une recette spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Feedbacks de la recette récupérés",
                    content = @Content(schema = @Schema(implementation = FeedbackResponseDto.class)))
    })
    @GetMapping("/recette/{recetteId}")
    public ResponseEntity<List<FeedbackResponseDto>> getFeedbacksByRecetteId(
            @Parameter(description = "ID de la recette", required = true, example = "recette1")
            @PathVariable String recetteId) {
        log.info("GET /api/feedbacks/recette/{} - Récupération des feedbacks de la recette", recetteId);
        List<FeedbackResponseDto> feedbacks = feedbackService.getFeedbacksByRecetteId(recetteId);
        return ResponseEntity.ok(feedbacks);
    }

    @Operation(
            summary = "Calculer la note moyenne d'une recette",
            description = "Retourne la note moyenne et le nombre total de feedbacks pour une recette donnée"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistiques calculées avec succès",
                    content = @Content(schema = @Schema(implementation = AverageRatingDto.class)))
    })
    @GetMapping("/recette/{recetteId}/average")
    public ResponseEntity<AverageRatingDto> getAverageRatingByRecetteId(
            @Parameter(description = "ID de la recette", required = true, example = "recette1")
            @PathVariable String recetteId) {
        log.info("GET /api/feedbacks/recette/{}/average - Calcul de la note moyenne", recetteId);
        AverageRatingDto averageRating = feedbackService.getAverageRatingByRecetteId(recetteId);
        return ResponseEntity.ok(averageRating);
    }

    @Operation(
            summary = "Mettre à jour un feedback",
            description = "Permet de modifier l'évaluation et/ou le commentaire d'un feedback existant"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Feedback mis à jour avec succès",
                    content = @Content(schema = @Schema(implementation = FeedbackResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Feedback non trouvé"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PutMapping("/{id}")
    public ResponseEntity<FeedbackResponseDto> updateFeedback(
            @Parameter(description = "ID du feedback à mettre à jour", required = true, example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nouvelles valeurs pour le feedback",
                    required = true,
                    content = @Content(schema = @Schema(implementation = FeedbackUpdateDto.class))
            )
            @Valid @RequestBody FeedbackUpdateDto feedbackUpdateDto) {
        log.info("PUT /api/feedbacks/{} - Mise à jour du feedback", id);
        FeedbackResponseDto response = feedbackService.updateFeedback(id, feedbackUpdateDto);
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
            @Parameter(description = "ID du feedback à supprimer", required = true, example = "1")
            @PathVariable Long id) {
        log.info("DELETE /api/feedbacks/{} - Suppression du feedback", id);
        feedbackService.deleteFeedback(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Envoyer les feedbacks au service de recommandation",
            description = "Envoie les feedbacks récents au service d'apprentissage par renforcement (RL) pour améliorer les recommandations"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Feedbacks envoyés avec succès"),
            @ApiResponse(responseCode = "503", description = "Service de recommandation indisponible")
    })
    @PostMapping("/send-to-recommendation")
    public ResponseEntity<String> sendFeedbacksToRecommendation() {
        log.info("POST /api/feedbacks/send-to-recommendation - Envoi des feedbacks au service RL");
        feedbackService.sendFeedbacksToRecommendationService();
        return ResponseEntity.ok("Feedbacks envoyés avec succès au service de recommandation");
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