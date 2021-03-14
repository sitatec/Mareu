package com.sitatech.mareu.domain.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@RunWith(JUnit4.class)
public class TimeSlotTest {

    private TimeSlot timeSlot;

    @Before
    public void init(){
        timeSlot = new TimeSlot(LocalDateTime.now(), Duration.ofMinutes(45));
    }

    @Test
    public void default_slot_duration_must_be_45_minutes(){
        final TimeSlot slot = new TimeSlot(LocalDateTime.now());
        assertEquals(slot.getDuration().toMinutes(), 45);
    }

    ///////////////////// TimeSlot.getEndTime() ///////////////////////

    @Test
    public void should_return_the_end_time_of_time_slot(){
        final LocalDateTime expectedResult = timeSlot.getStartTime().plus(timeSlot.getDuration());
        assertEquals(timeSlot.getEndTime(), expectedResult);
    }

    ///////////////////// TimeSlot.overlapsWith() ///////////////////////

    @Test
    public void should_wrap_the_given_time_slot(){
        final TimeSlot secondSlot = new TimeSlot(
                timeSlot.getStartTime().plusMinutes(5),
                timeSlot.getDuration().minusMinutes(5)
        );
        assertTrue(timeSlot.overlapsWith(secondSlot));
        // vice-versa
        assertTrue(secondSlot.overlapsWith(timeSlot));
    }

    @Test
    public void should_overlaps_the_given_time_slot(){
        final TimeSlot secondSlot = new TimeSlot(
                timeSlot.getStartTime().plusMinutes(5),
                timeSlot.getDuration().plusMinutes(5)
        );

        assertTrue(timeSlot.overlapsWith(secondSlot));
        // vice-versa
        assertTrue(secondSlot.overlapsWith(timeSlot));
    }

    @Test
    public void overlap_test_should_be_false_when_a_slot_begin_at_the_and_time_of_another(){
        final TimeSlot secondSlot = new TimeSlot(
                timeSlot.getEndTime(),
                timeSlot.getDuration().plusMinutes(5)
        );
        assertFalse(timeSlot.overlapsWith(secondSlot));
        // vice-versa
        assertFalse(secondSlot.overlapsWith(timeSlot));
    }

    @Test
    public void should_overlaps_a_time_slot_which_start_at_same_time(){
        final TimeSlot secondSlot = new TimeSlot(
                timeSlot.getStartTime(),
                timeSlot.getDuration().plusMinutes(5)
        );

        assertTrue(timeSlot.overlapsWith(secondSlot));
    }

    @Test
    public void should_overlaps_a_time_slot_which_end_at_same_time(){
        final TimeSlot secondSlot = new TimeSlot(
                timeSlot.getStartTime().plusMinutes(5),
                timeSlot.getDuration()
        );

        assertTrue(timeSlot.overlapsWith(secondSlot));
    }

    @Test
    public void overlap_test_on_itself_must_be_true(){
        assertTrue(timeSlot.overlapsWith(timeSlot));
    }

}
