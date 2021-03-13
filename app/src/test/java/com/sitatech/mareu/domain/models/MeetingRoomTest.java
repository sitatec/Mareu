package com.sitatech.mareu.domain.models;

import com.sitatech.mareu.domain.enums.MeetingRoomUniqueId;
import com.sitatech.mareu.domain.exceptions.TimeSlotOverlapException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@RunWith(JUnit4.class)
public class MeetingRoomTest {
    private MeetingRoom meetingRoom;

    @Before
    public void init(){
        meetingRoom = new MeetingRoom(MeetingRoomUniqueId.D);
    }

    ///////////////////// MeetingRoom.isAvailable() ///////////////////////

    @Test
    public void should_be_available_at_the_given_time_slot(){
        final TimeSlot timeSlot = new TimeSlot(LocalDateTime.now(), Duration.ofHours(1));
        assertTrue(meetingRoom.isAvailableAt(timeSlot));// should be available because no slot as been reserved
    }

    @Test
    public void should_not_be_available_at_the_given_time_slot() throws TimeSlotOverlapException {
        final TimeSlot timeSlot1 = new TimeSlot(LocalDateTime.now(), Duration.ofHours(1));
        final TimeSlot timeSlot2 = new TimeSlot(timeSlot1.getEndTime(), Duration.ofHours(1));
        final TimeSlot timeSlot3 = new TimeSlot(timeSlot2.getEndTime(), Duration.ofHours(1));
        meetingRoom.reserve(timeSlot1, timeSlot2, timeSlot3);
        // Instantiates a TimeSlot which overlaps timeSlot2 and timeSlot3
        final TimeSlot timeSlot = new TimeSlot(timeSlot2.getStartTime().plusMinutes(10), Duration.ofHours(1));
        assertFalse(meetingRoom.isAvailableAt(timeSlot));
    }

    ///////////////////// MeetingRoom.reserve() ///////////////////////

    @Test
    public void should_reserve_a_time_slot() throws TimeSlotOverlapException {
        final TimeSlot timeSlot = new TimeSlot(LocalDateTime.now(), Duration.ofHours(1));
        assertTrue(meetingRoom.getAllReservedSlots().isEmpty());
        meetingRoom.reserve(timeSlot);
        assertEquals(meetingRoom.getAllReservedSlots().size(), 1);
    }

    @Test
    public void should_reserve_multiple_time_slots() throws TimeSlotOverlapException {
        assertTrue(meetingRoom.getAllReservedSlots().isEmpty());
        final TimeSlot timeSlot1 = new TimeSlot(LocalDateTime.now(), Duration.ofHours(1));
        final TimeSlot timeSlot2 = new TimeSlot(timeSlot1.getEndTime(), Duration.ofHours(1));
        final TimeSlot timeSlot3 = new TimeSlot(timeSlot2.getEndTime(), Duration.ofHours(1));
        final TimeSlot timeSlot4 = new TimeSlot(timeSlot3.getEndTime(), Duration.ofHours(1));

        meetingRoom.reserve(timeSlot1, timeSlot2, timeSlot3, timeSlot4);
        assertEquals(meetingRoom.getAllReservedSlots().size(), 4);
    }

    @Test
    public void should_throw_a_TimeSlotOverlapException_when_the_given_slot_overlaps_already_reserved_slot() throws TimeSlotOverlapException {
        final TimeSlot timeSlot2 = new TimeSlot(LocalDateTime.now(), Duration.ofHours(1));
        final TimeSlot timeSlot3 = new TimeSlot(timeSlot2.getEndTime(), Duration.ofHours(1));
        meetingRoom.reserve(timeSlot2, timeSlot3);
        // Instantiates a TimeSlot which overlaps timeSlot2 and timeSlot3
        final TimeSlot timeSlot = new TimeSlot(timeSlot2.getStartTime().plusMinutes(10), Duration.ofHours(1));
        assertThrows(TimeSlotOverlapException.class, () -> meetingRoom.reserve(timeSlot));
    }

    ///////////////////// MeetingRoom.dispose() ///////////////////////
    @Test
    public void should_dispose_the_given_slot() throws TimeSlotOverlapException {
        final TimeSlot timeSlot1 = new TimeSlot(LocalDateTime.now(), Duration.ofHours(1));
        final TimeSlot timeSlot2 = new TimeSlot(timeSlot1.getEndTime(), Duration.ofHours(1));
        meetingRoom.reserve(timeSlot1, timeSlot2);
        assertEquals(meetingRoom.getAllReservedSlots().size(), 2);
        meetingRoom.dispose(timeSlot1);
        assertEquals(meetingRoom.getAllReservedSlots().size(), 1);
    }

    ///////////////////// MeetingRoom.getAllReservedSlots() ///////////////////////

    @Test
    public void should_return_all_reserved_time_slot() throws TimeSlotOverlapException {
        final TimeSlot timeSlot1 = new TimeSlot(LocalDateTime.now(), Duration.ofHours(1));
        final TimeSlot timeSlot2 = new TimeSlot(timeSlot1.getEndTime(), Duration.ofHours(1));
        final TimeSlot timeSlot3 = new TimeSlot(timeSlot2.getEndTime(), Duration.ofHours(1));
        final TimeSlot timeSlot4 = new TimeSlot(timeSlot3.getEndTime(), Duration.ofHours(1));

        meetingRoom.reserve(timeSlot1, timeSlot2, timeSlot3, timeSlot4);
        assertEquals(meetingRoom.getAllReservedSlots().size(), 4);
    }

}
