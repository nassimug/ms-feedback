package com.springbootTemplate.univ.soa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Requête pour créer un nouveau feedback")
public class FeedbackCreateRequest {

    @Schema(description = "Identifiant de l'utilisateur", example = "1", required = true)
    @NotNull(message = "L'ID de l'utilisateur est obligatoire")
    private Long utilisateurId;

    @Schema(description = "Identifiant de la recette", example = "5", required = true)
    @NotNull(message = "L'ID de la recette est obligatoire")
    private Long recetteId;

    @Schema(description = "Note de 1 à 5 étoiles", example = "5", minimum = "1", maximum = "5", required = true)
    @NotNull(message = "L'évaluation est obligatoire")
    @Min(value = 1, message = "L'évaluation doit être entre 1 et 5")
    @Max(value = 5, message = "L'évaluation doit être entre 1 et 5")
    private Integer evaluation;

    @Schema(description = "Commentaire optionnel sur la recette", example = "Délicieuse recette, facile à réaliser !")
    @Size(max = 1000, message = "Le commentaire ne peut pas dépasser 1000 caractères")
    private String commentaire;
}