package com.springbootTemplate.univ.soa.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FeedbackNotFoundExceptionTest {

    @Test
    void testExceptionMessage() {
        String msg = "ID introuvable";
        FeedbackNotFoundException ex = new FeedbackNotFoundException(msg);

        assertEquals(msg, ex.getMessage());
        assertTrue(ex instanceof RuntimeException);
    }
}