package com.springbootTemplate.univ.soa.client;

import com.springbootTemplate.univ.soa.dto.FeedbackDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PersistanceClient {

    private final RestTemplate restTemplate;

    @Value("${persistance.service.url}")
    private String persistanceServiceUrl;

    /**
     * Créer un nouveau feedback dans le microservice Persistance
     */
    public FeedbackDTO createFeedback(FeedbackDTO feedbackDTO) {
        String url = persistanceServiceUrl + "/api/persistance/feedbacks";
        log.info("POST {} - Création d'un feedback", url);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<FeedbackDTO> request = new HttpEntity<>(feedbackDTO, headers);

            ResponseEntity<FeedbackDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    FeedbackDTO.class
            );

            log.info("Feedback créé avec succès - ID: {}", response.getBody().getId());
            return response.getBody();

        } catch (HttpClientErrorException.Conflict e) {
            log.warn("Conflit lors de la création du feedback: feedback déjà existant");
            throw e; // Propager l'exception 409 pour gestion au niveau service
        } catch (HttpClientErrorException.NotFound e) {
            log.error("Ressource non trouvée lors de la création du feedback");
            throw new RuntimeException("Ressource non trouvée: " + e.getStatusCode(), e);
        } catch (HttpClientErrorException.BadRequest e) {
            log.error("Requête invalide lors de la création du feedback: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Requête invalide: " + e.getResponseBodyAsString(), e);
        } catch (HttpClientErrorException e) {
            log.error("Erreur client lors de la création du feedback: {}", e.getMessage());
            throw new RuntimeException("Erreur lors de la création du feedback: " + e.getStatusCode(), e);
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la création du feedback: {}", e.getMessage());
            throw new RuntimeException("Erreur lors de la création du feedback", e);
        }
    }

    /**
     * Récupérer tous les feedbacks
     */
    public List<FeedbackDTO> getAllFeedbacks() {
        String url = persistanceServiceUrl + "/api/persistance/feedbacks";
        log.info("GET {} - Récupération de tous les feedbacks", url);

        try {
            ResponseEntity<List<FeedbackDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<FeedbackDTO>>() {}
            );

            log.info("{} feedbacks récupérés", response.getBody().size());
            return response.getBody();

        } catch (Exception e) {
            log.error("Erreur lors de la récupération des feedbacks: {}", e.getMessage());
            throw new RuntimeException("Erreur lors de la récupération des feedbacks", e);
        }
    }

    /**
     * Récupérer un feedback par son ID
     */
    public FeedbackDTO getFeedbackById(Long id) {
        String url = persistanceServiceUrl + "/api/persistance/feedbacks/" + id;
        log.info("GET {} - Récupération du feedback", url);

        try {
            ResponseEntity<FeedbackDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    FeedbackDTO.class
            );

            log.info("Feedback récupéré - ID: {}", id);
            return response.getBody();

        } catch (HttpClientErrorException.NotFound e) {
            log.error("Feedback non trouvé - ID: {}", id);
            throw new RuntimeException("Feedback non trouvé avec l'ID: " + id);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération du feedback: {}", e.getMessage());
            throw new RuntimeException("Erreur lors de la récupération du feedback", e);
        }
    }

    /**
     * Récupérer les feedbacks d'un utilisateur
     */
    public List<FeedbackDTO> getFeedbacksByUtilisateurId(Long utilisateurId) {
        String url = persistanceServiceUrl + "/api/persistance/feedbacks/utilisateur/" + utilisateurId;
        log.info("GET {} - Récupération des feedbacks de l'utilisateur", url);

        try {
            ResponseEntity<List<FeedbackDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<FeedbackDTO>>() {}
            );

            log.info("{} feedbacks récupérés pour l'utilisateur {}", response.getBody().size(), utilisateurId);
            return response.getBody();

        } catch (Exception e) {
            log.error("Erreur lors de la récupération des feedbacks de l'utilisateur: {}", e.getMessage());
            throw new RuntimeException("Erreur lors de la récupération des feedbacks de l'utilisateur", e);
        }
    }

    /**
     * Récupérer les feedbacks d'une recette
     */
    public List<FeedbackDTO> getFeedbacksByRecetteId(Long recetteId) {
        String url = persistanceServiceUrl + "/api/persistance/feedbacks/recette/" + recetteId;
        log.info("GET {} - Récupération des feedbacks de la recette", url);

        try {
            ResponseEntity<List<FeedbackDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<FeedbackDTO>>() {}
            );

            log.info("{} feedbacks récupérés pour la recette {}", response.getBody().size(), recetteId);
            return response.getBody();

        } catch (Exception e) {
            log.error("Erreur lors de la récupération des feedbacks de la recette: {}", e.getMessage());
            throw new RuntimeException("Erreur lors de la récupération des feedbacks de la recette", e);
        }
    }

    /**
     * Mettre à jour un feedback
     */
    public FeedbackDTO updateFeedback(Long id, FeedbackDTO feedbackDTO) {
        String url = persistanceServiceUrl + "/api/persistance/feedbacks/" + id;
        log.info("PUT {} - Mise à jour du feedback", url);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<FeedbackDTO> request = new HttpEntity<>(feedbackDTO, headers);

            ResponseEntity<FeedbackDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    request,
                    FeedbackDTO.class
            );

            log.info("Feedback mis à jour - ID: {}", id);
            return response.getBody();

        } catch (HttpClientErrorException.NotFound e) {
            log.error("Feedback non trouvé - ID: {}", id);
            throw new RuntimeException("Feedback non trouvé avec l'ID: " + id);
        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour du feedback: {}", e.getMessage());
            throw new RuntimeException("Erreur lors de la mise à jour du feedback", e);
        }
    }

    /**
     * Supprimer un feedback
     */
    public void deleteFeedback(Long id) {
        String url = persistanceServiceUrl + "/api/persistance/feedbacks/" + id;
        log.info("DELETE {} - Suppression du feedback", url);

        try {
            restTemplate.delete(url);
            log.info("Feedback supprimé - ID: {}", id);

        } catch (HttpClientErrorException.NotFound e) {
            log.error("Feedback non trouvé - ID: {}", id);
            throw new RuntimeException("Feedback non trouvé avec l'ID: " + id);
        } catch (Exception e) {
            log.error("Erreur lors de la suppression du feedback: {}", e.getMessage());
            throw new RuntimeException("Erreur lors de la suppression du feedback", e);
        }
    }

    /**
     * Vérifier si l'utilisateur existe (appel au microservice Persistance)
     */
    public boolean utilisateurExists(Long utilisateurId) {
        String url = persistanceServiceUrl + "/api/persistance/utilisateurs/" + utilisateurId;
        log.info("GET {} - Vérification de l'existence de l'utilisateur", url);

        try {
            restTemplate.getForEntity(url, Object.class);
            return true;
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        } catch (Exception e) {
            log.warn("Erreur lors de la vérification de l'utilisateur: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Vérifier si la recette existe (appel au microservice Persistance)
     */
    public boolean recetteExists(Long recetteId) {
        String url = persistanceServiceUrl + "/api/persistance/recettes/" + recetteId;
        log.info("GET {} - Vérification de l'existence de la recette", url);

        try {
            restTemplate.getForEntity(url, Object.class);
            return true;
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        } catch (Exception e) {
            log.warn("Erreur lors de la vérification de la recette: {}", e.getMessage());
            return false;
        }
    }
}