package de.reactivejukebox.model;

import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class IndirectFeedbackPlainTest {
    @Test
    public void testIsValid2() {
        IndirectFeedbackPlain feedback = new IndirectFeedbackPlain();
        assertFalse(feedback.isValid());
        feedback.setUserId(1);
        assertFalse(feedback.isValid());
        feedback.setRadioId(1);
        assertFalse(feedback.isValid());
        feedback.setTrackId(1);
        assertFalse(feedback.isValid());
        feedback.setPosition(0);
        assertFalse(feedback.isValid());
        feedback.setFeedbackName("SKIP");
        assertTrue(feedback.isValid());
        feedback.setFeedbackName("MULTI_SKIP");
        assertFalse(feedback.isValid());
        feedback.setToTrackId(1);
        assertTrue(feedback.isValid());

        feedback.setPosition(-1);
        assertFalse(feedback.isValid());
        feedback.setPosition(2^8);
        assertTrue(feedback.isValid());

        feedback.setUserId(-2);
        assertFalse(feedback.isValid());
        feedback.setUserId(2);
        assertTrue(feedback.isValid());

        feedback.setRadioId(-2);
        assertFalse(feedback.isValid());
        feedback.setRadioId(2);
        assertTrue(feedback.isValid());

        feedback.setTrackId(-2);
        assertFalse(feedback.isValid());
        feedback.setTrackId(2);
        assertTrue(feedback.isValid());

        feedback.setToTrackId(-2);
        assertFalse(feedback.isValid());
        feedback.setToTrackId(2);
        assertTrue(feedback.isValid());

        feedback.setFeedbackName("DELETE");
        assertTrue(feedback.isValid());
    }

}
