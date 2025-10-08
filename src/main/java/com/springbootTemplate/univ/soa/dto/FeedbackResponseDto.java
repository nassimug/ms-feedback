package com.springbootTemplate.univ.soa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Représentation d'un feedback complet")
public class FeedbackResponseDto {

    @Schema(description = "Identifiant unique du feedback", example = "1")
    private Long id;

    @Schema(description = "Identifiant de l'utilisateur", example = "user1")
    private String userId;

    @Schema(description = "Identifiant de la recette", example = "recette1")
    private String recetteId;

    @Schema(description = "Note donnée (1 à 5 étoiles)", example = "5")
    private Integer evaluation;

    @Schema(description = "Commentaire de l'utilisateur", example = "Excellente recette !")
    private String commentaire;

    @Schema(description = "Date de création du feedback", example = "2025-10-08T14:30:00")
    private LocalDateTime dateFeedback;

    @Schema(description = "Date de dernière modification", example = "2025-10-08T15:00:00")
    private LocalDateTime dateModification;
}