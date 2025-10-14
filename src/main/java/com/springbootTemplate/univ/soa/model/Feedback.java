package com.springbootTemplate.univ.soa.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;

@Document(collection = "feedbacks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback {

    @Id
    private String id;

    @Indexed
    private String userId;

    @Indexed
    private String recetteId;

    private Integer evaluation;

    private String commentaire;

    @CreatedDate
    @Builder.Default
    private LocalDateTime dateFeedback = LocalDateTime.now();

    @LastModifiedDate
    @Builder.Default
    private LocalDateTime dateModification = LocalDateTime.now();
}