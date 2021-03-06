package com.sitatech.mareu.domain.utils;

import com.sitatech.mareu.domain.enums.MeetingRoomUniqueId;
import com.sitatech.mareu.domain.exceptions.FreeTimeSlotReleaseAttempt;
import com.sitatech.mareu.domain.exceptions.TimeSlotOverlapException;
import com.sitatech.mareu.domain.models.Meeting;
import com.sitatech.mareu.domain.models.TimeSlot;
import com.sitatech.mareu.domain.repositories.MeetingRoomRepository;
import com.sitatech.mareu.domain.repositories.ScheduledMeetingRepository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RunWith(JUnit4.class)
public class MeetingSchedulerTest {

    private static final Set<String> MEETING_PARTICIPANTS = new HashSet<>(Arrays.asList(
            "maxim@lamezone.com", "test.t@test.t", "fake@test.com", "last@one.com"
    ));
    private MeetingScheduler meetingScheduler;
    private ScheduledMeetingRepository scheduledMeetingRepository = new ScheduledMeetingRepository();
    private final Meeting defaultMeeting = new Meeting(MEETING_PARTICIPANTS,
            new TimeSlot(LocalDateTime.now()), MeetingRoomUniqueId.D, "SUBJECT", 0xFFAACC);

    @Before
    public void init(){
        meetingScheduler = new MeetingScheduler(scheduledMeetingRepository, new MeetingRoomRepository());
    }

    ///////////////////// MeetingScheduler.schedule() ///////////////////////

    @Test
    public void should_schedule_meeting() throws TimeSlotOverlapException {
        assertTrue(scheduledMeetingRepository.getAll().isEmpty());
        meetingScheduler.schedule(defaultMeeting);
        assertEquals(scheduledMeetingRepository.getAll().size(), 1);
    }

    @Test
    public void should_throw_a_TimeSlotOverlapException_when_the_given_meeting_slot_overlaps_a_scheduled_meeting_slot() throws TimeSlotOverlapException {
        meetingScheduler.schedule(defaultMeeting);
        assertThrows(TimeSlotOverlapException.class, () -> meetingScheduler.schedule(defaultMeeting));
    }

        ///////////////////// MeetingScheduler.cancel() ///////////////////////

    @Test
    public void should_cancel_meeting() throws TimeSlotOverlapException, FreeTimeSlotReleaseAttempt {
        meetingScheduler.schedule(defaultMeeting);
        assertEquals(scheduledMeetingRepository.getAll().size(), 1);
        meetingScheduler.cancel(defaultMeeting);
        assertEquals(scheduledMeetingRepository.getAll().size(), 0);
    }

    @Test
    public void should_throw_a_FreeTimeSlotReleaseAttempt_when_the_given_meeting_was_not_scheduled(){
        final Meeting notScheduledMeeting = new Meeting(MEETING_PARTICIPANTS,
                new TimeSlot(LocalDateTime.now()), MeetingRoomUniqueId.A, "FAKE_SUBJECT", 0xFFAABBCC);
        assertThrows(FreeTimeSlotReleaseAttempt.class, () -> meetingScheduler.cancel(notScheduledMeeting));
    }


    ///////////////////// MeetingScheduler.getAllScheduledMeetings() ///////////////////////

    @Test
    public void should_returns_all_scheduled_meetings() throws TimeSlotOverlapException {
        final Meeting meeting = new Meeting(MEETING_PARTICIPANTS,
                new TimeSlot(LocalDateTime.now()), MeetingRoomUniqueId.A, "FAKE_SUBJECT", 0xFFAABBCC);
        meetingScheduler.schedule(defaultMeeting);
        meetingScheduler.schedule(meeting);
        assertEquals(scheduledMeetingRepository.getAll().size(), 2);
    }

    ///////////////////// MeetingScheduler.getScheduledMeetingsByDate() ///////////////////////

    @Test
    public void should_filter_scheduled_meetings_by_date() throws TimeSlotOverlapException {
        final TimeSlot timeSlot = new TimeSlot(LocalDateTime.now());
        final TimeSlot timeSlotForYesterday = new TimeSlot(LocalDateTime.now().minusDays(1));
        final TimeSlot timeSlotForTomorrow = new TimeSlot(LocalDateTime.now().plusDays(1));

        final Meeting meeting = new Meeting(MEETING_PARTICIPANTS,
                timeSlot, MeetingRoomUniqueId.A, "FAKE_SUBJECT", 0xFFAABBCC);
        final Meeting yesterdayMeeting = new Meeting(MEETING_PARTICIPANTS,
                timeSlotForYesterday, MeetingRoomUniqueId.A, "FAKE_SUBJECT", 0xFFAABBCC);
        final Meeting tomorrowMeeting = new Meeting(MEETING_PARTICIPANTS,
                timeSlotForTomorrow, MeetingRoomUniqueId.A, "FAKE_SUBJECT", 0xFFAABBCC);

        meetingScheduler.schedule(meeting);
        meetingScheduler.schedule(yesterdayMeeting);
        meetingScheduler.schedule(tomorrowMeeting);

        final List<Meeting> filteredMeeting = scheduledMeetingRepository.getByDate(LocalDate.now().minusDays(1));
        assertTrue(filteredMeeting.contains(yesterdayMeeting));
        assertEquals(filteredMeeting.size(), 1);
    }

    ///////////////////// MeetingScheduler.getScheduledMeetingsByRoom() ///////////////////////

    @Test
    public void should_filter_scheduled_meetings_by_meeting_room() throws TimeSlotOverlapException {
        // Initialize meetings
        final Meeting meeting = new Meeting(MEETING_PARTICIPANTS,
                new TimeSlot(LocalDateTime.now().plusDays(1)), MeetingRoomUniqueId.A, "FAKE_SUBJECT", 0xFFAABBCC);
        final Meeting meeting1 = new Meeting(MEETING_PARTICIPANTS,
                new TimeSlot(LocalDateTime.now()), MeetingRoomUniqueId.D, "FAKE_SUBJECT", 0xFFAABBCC);
        final Meeting meeting2 = new Meeting(MEETING_PARTICIPANTS,
                new TimeSlot(LocalDateTime.now().minusDays(2)), MeetingRoomUniqueId.A, "FAKE_SUBJECT", 0xFFAABBCC);

        meetingScheduler.schedule(meeting);
        meetingScheduler.schedule(meeting1);
        meetingScheduler.schedule(meeting2);

        assertEquals(scheduledMeetingRepository.getByRoom(MeetingRoomUniqueId.D).size(), 1);
        assertEquals(scheduledMeetingRepository.getByRoom(MeetingRoomUniqueId.A).size(), 2);
    }

}
