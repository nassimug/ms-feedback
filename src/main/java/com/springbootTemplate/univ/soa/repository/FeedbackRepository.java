package com.springbootTemplate.univ.soa.repository;

import com.springbootTemplate.univ.soa.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

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
     */
    @Query("SELECT AVG(f.evaluation) FROM Feedback f WHERE f.recetteId = :recetteId")
    Double findAverageRatingByRecetteId(@Param("recetteId") String recetteId);

    /**
     * Compter le nombre de feedbacks pour une recette
     */
    Long countByRecetteId(String recetteId);

    /**
     * Récupérer les feedbacks récents (limite)
     */
    List<Feedback> findTop100ByOrderByDateFeedbackDesc();
}