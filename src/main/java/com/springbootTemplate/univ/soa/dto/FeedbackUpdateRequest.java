package com.springbootTemplate.univ.soa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.*;

@Data
@Schema(description = "Données pour mettre à jour un feedback existant")
public class FeedbackUpdateRequest {

    @Schema(description = "Nouvelle note de 1 à 5 étoiles", example = "4", minimum = "1", maximum = "5")
    @Min(value = 1, message = "L'évaluation doit être entre 1 et 5")
    @Max(value = 5, message = "L'évaluation doit être entre 1 et 5")
    private Integer evaluation;

    @Schema(description = "Nouveau commentaire", example = "Finalement, un peu trop sucrée à mon goût")
    private String commentaire;
}