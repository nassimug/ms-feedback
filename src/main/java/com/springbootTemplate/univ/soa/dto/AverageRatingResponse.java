package com.springbootTemplate.univ.soa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Statistiques d'une recette")
public class AverageRatingResponse {

    @Schema(description = "Identifiant de la recette", example = "recette1")
    private String recetteId;

    @Schema(description = "Note moyenne (de 1 à 5)", example = "4.67")
    private Double averageRating;

    @Schema(description = "Nombre total de feedbacks", example = "15")
    private Long totalFeedbacks;
}