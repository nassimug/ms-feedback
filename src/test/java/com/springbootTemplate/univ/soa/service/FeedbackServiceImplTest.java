package com.springbootTemplate.univ.soa.service;

import com.springbootTemplate.univ.soa.client.PersistanceClient;
import com.springbootTemplate.univ.soa.dto.*;
import com.springbootTemplate.univ.soa.exception.FeedbackNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceImplTest {

    @Mock
    private PersistanceClient persistanceClient;

    @InjectMocks
    private FeedbackServiceImpl feedbackService;

    private FeedbackDTO feedbackDTO;
    private FeedbackCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        feedbackDTO = FeedbackDTO.builder()
                .id(1L)
                .utilisateurId(10L)
                .recetteId(20L)
                .evaluation(4)
                .commentaire("Test Comment")
                .dateFeedback(LocalDateTime.now())
                .build();

        createRequest = new FeedbackCreateRequest();
        createRequest.setUtilisateurId(10L);
        createRequest.setRecetteId(20L);
        createRequest.setEvaluation(4);
        createRequest.setCommentaire("Test Comment");
    }

    // --- TESTS CREATE ---

    @Test
    void createFeedback_Success() {
        when(persistanceClient.utilisateurExists(10L)).thenReturn(true);
        when(persistanceClient.recetteExists(20L)).thenReturn(true);
        when(persistanceClient.createFeedback(any(FeedbackDTO.class))).thenReturn(feedbackDTO);

        FeedbackResponse response = feedbackService.createFeedback(createRequest);

        assertNotNull(response);
        assertEquals(4, response.getEvaluation());
        verify(persistanceClient).createFeedback(any(FeedbackDTO.class));
    }

    @Test
    void createFeedback_UtilisateurNotFound_ThrowsException() {
        when(persistanceClient.utilisateurExists(10L)).thenReturn(false);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            feedbackService.createFeedback(createRequest);
        });

        assertTrue(exception.getMessage().contains("Utilisateur non trouvé"));
        verify(persistanceClient, never()).createFeedback(any());
    }

    @Test
    void createFeedback_RecetteNotFound_ThrowsException() {
        when(persistanceClient.utilisateurExists(10L)).thenReturn(true);
        when(persistanceClient.recetteExists(20L)).thenReturn(false);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            feedbackService.createFeedback(createRequest);
        });

        assertTrue(exception.getMessage().contains("Recette non trouvée"));
    }

    // --- TESTS GET BY ID ---

    @Test
    void getFeedbackById_Success() {
        when(persistanceClient.getFeedbackById(1L)).thenReturn(feedbackDTO);

        FeedbackResponse response = feedbackService.getFeedbackById("1");

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getFeedbackById_InvalidFormat_ThrowsException() {
        assertThrows(FeedbackNotFoundException.class, () -> {
            feedbackService.getFeedbackById("invalid-id");
        });
    }

    @Test
    void getFeedbackById_NotFound_ThrowsException() {
        when(persistanceClient.getFeedbackById(99L)).thenThrow(new RuntimeException("Not found"));

        assertThrows(FeedbackNotFoundException.class, () -> {
            feedbackService.getFeedbackById("99");
        });
    }

    // --- TESTS AVERAGE RATING ---

    @Test
    void getAverageRatingByRecetteId_Success() {
        FeedbackDTO f1 = FeedbackDTO.builder().evaluation(4).build();
        FeedbackDTO f2 = FeedbackDTO.builder().evaluation(5).build();

        when(persistanceClient.getFeedbacksByRecetteId(20L)).thenReturn(Arrays.asList(f1, f2));

        AverageRatingResponse response = feedbackService.getAverageRatingByRecetteId("20");

        // (4+5)/2 = 4.5
        assertEquals(4.5, response.getAverageRating());
        assertEquals(2L, response.getTotalFeedbacks());
    }

    @Test
    void getAverageRatingByRecetteId_NoFeedbacks() {
        when(persistanceClient.getFeedbacksByRecetteId(20L)).thenReturn(Collections.emptyList());

        AverageRatingResponse response = feedbackService.getAverageRatingByRecetteId("20");

        assertEquals(0.0, response.getAverageRating());
        assertEquals(0L, response.getTotalFeedbacks());
    }

    @Test
    void getAverageRatingByRecetteId_RoundingCheck() {
        // (4 + 4 + 5) / 3 = 4.33333 -> arrondi à 4.33
        FeedbackDTO f1 = FeedbackDTO.builder().evaluation(4).build();
        FeedbackDTO f2 = FeedbackDTO.builder().evaluation(4).build();
        FeedbackDTO f3 = FeedbackDTO.builder().evaluation(5).build();

        when(persistanceClient.getFeedbacksByRecetteId(20L)).thenReturn(Arrays.asList(f1, f2, f3));

        AverageRatingResponse response = feedbackService.getAverageRatingByRecetteId("20");

        assertEquals(4.33, response.getAverageRating());
    }

    // --- TESTS UPDATE ---

    @Test
    void updateFeedback_Success() {
        FeedbackUpdateRequest updateRequest = new FeedbackUpdateRequest();
        updateRequest.setCommentaire("New Comment");
        // Evaluation null, so it should keep old value

        when(persistanceClient.getFeedbackById(1L)).thenReturn(feedbackDTO); // existing has eval=4, comm="Test"

        // Mock retour du client save
        FeedbackDTO updatedDTO = FeedbackDTO.builder()
                .id(1L)
                .evaluation(4)
                .commentaire("New Comment")
                .build();
        when(persistanceClient.updateFeedback(eq(1L), any(FeedbackDTO.class))).thenReturn(updatedDTO);

        FeedbackResponse response = feedbackService.updateFeedback("1", updateRequest);

        assertEquals("New Comment", response.getCommentaire());
        assertEquals(4, response.getEvaluation()); // Vérifie que l'ancienne valeur est conservée
        verify(persistanceClient).updateFeedback(eq(1L), any(FeedbackDTO.class));
    }

    // --- TESTS DELETE ---

    @Test
    void deleteFeedback_Success() {
        doNothing().when(persistanceClient).deleteFeedback(1L);

        feedbackService.deleteFeedback("1");

        verify(persistanceClient, times(1)).deleteFeedback(1L);
    }

    @Test
    void deleteFeedback_InvalidId() {
        assertThrows(FeedbackNotFoundException.class, () -> feedbackService.deleteFeedback("abc"));
    }
}