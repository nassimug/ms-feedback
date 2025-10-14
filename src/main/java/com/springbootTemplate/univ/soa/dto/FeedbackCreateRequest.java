package com.springbootTemplate.univ.soa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.*;

@Data
@Schema(description = "Données pour créer un nouveau feedback")
public class FeedbackCreateRequest {

    @Schema(description = "Identifiant de l'utilisateur", example = "user1", required = true)
    @NotBlank(message = "L'ID de l'utilisateur est obligatoire")
    private String userId;

    @Schema(description = "Identifiant de la recette", example = "recette1", required = true)
    @NotBlank(message = "L'ID de la recette est obligatoire")
    private String recetteId;

    @Schema(description = "Note de 1 à 5 étoiles", example = "5", minimum = "1", maximum = "5", required = true)
    @NotNull(message = "L'évaluation est obligatoire")
    @Min(value = 1, message = "L'évaluation doit être entre 1 et 5")
    @Max(value = 5, message = "L'évaluation doit être entre 1 et 5")
    private Integer evaluation;

    @Schema(description = "Commentaire optionnel sur la recette", example = "Délicieuse recette, facile à réaliser !")
    private String commentaire;
}