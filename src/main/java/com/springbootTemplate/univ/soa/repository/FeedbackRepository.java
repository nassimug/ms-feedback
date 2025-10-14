package com.springbootTemplate.univ.soa.repository;

import com.springbootTemplate.univ.soa.model.Feedback;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends MongoRepository<Feedback, String> {

    /**
     * Récupérer tous les feedbacks d'un utilisateur
     */
    List<Feedback> findByUserIdOrderByDateFeedbackDesc(String userId);

    /**
     * Récupérer tous les feedbacks d'une recette
     */
    List<Feedback> findByRecetteIdOrderByDateFeedbackDesc(String recetteId);

    /**
     * Calculer la note moyenne d'une recette
     * MongoDB utilise une agrégation différente
     */
    @Aggregation(pipeline = {
            "{ $match: { recetteId: ?0 } }",
            "{ $group: { _id: null, avgRating: { $avg: '$evaluation' } } }"
    })
    Double findAverageRatingByRecetteId(String recetteId);

    /**
     * Compter le nombre de feedbacks pour une recette
     */
    Long countByRecetteId(String recetteId);

    /**
     * Récupérer les feedbacks récents (limite)
     */
    List<Feedback> findTop100ByOrderByDateFeedbackDesc();
}