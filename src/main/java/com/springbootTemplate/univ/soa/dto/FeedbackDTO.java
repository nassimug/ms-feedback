package com.springbootTemplate.univ.soa.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackDTO {

    private Long id;
    private Long utilisateurId;
    private Long recetteId;
    private Integer evaluation;
    private String commentaire;
    private LocalDateTime dateFeedback;
    private LocalDateTime dateModification;
}