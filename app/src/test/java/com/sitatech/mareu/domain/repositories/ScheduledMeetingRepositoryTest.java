package com.sitatech.mareu.domain.repositories;

import android.icu.util.LocaleData;

import com.sitatech.mareu.domain.enums.MeetingRoomUniqueId;
import com.sitatech.mareu.domain.models.Meeting;
import com.sitatech.mareu.domain.models.TimeSlot;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.LocalDateTime;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class ScheduledMeetingRepositoryTest {

    private ScheduledMeetingRepository scheduledMeetingRepository;

    @Before
    public void init(){
        scheduledMeetingRepository = new ScheduledMeetingRepository();
    }

    @Test
    public void should_add_meeting(){
        assertTrue(scheduledMeetingRepository.getAll().isEmpty());
        scheduledMeetingRepository.add(new Meeting());
        assertEquals(scheduledMeetingRepository.getAll().size(), 1);
    }

    @Test
    public void should_remove_meeting(){
        final Meeting meeting = new Meeting();
        scheduledMeetingRepository.add(meeting);
        assertEquals(scheduledMeetingRepository.getAll().size(), 1);
        scheduledMeetingRepository.remove(meeting);
        assertTrue(scheduledMeetingRepository.getAll().isEmpty());
    }

    @Test
    public void should_get_all_scheduled_meeting(){
        scheduledMeetingRepository.add(new Meeting());
        scheduledMeetingRepository.add(new Meeting());
        scheduledMeetingRepository.add(new Meeting());
        scheduledMeetingRepository.add(new Meeting());
        assertEquals(scheduledMeetingRepository.getAll().size(), 4);
    }

    @Test
    public void should_get_scheduled_meeting_by_date(){
        final LocalDateTime dateTime = LocalDateTime.now();
        scheduledMeetingRepository.add(new Meeting(null, new TimeSlot(dateTime),null,null));
        scheduledMeetingRepository.add(new Meeting(null, new TimeSlot(dateTime),null,null));
        scheduledMeetingRepository.add(new Meeting(null, new TimeSlot(dateTime),null,null));
        scheduledMeetingRepository.add(new Meeting(null, new TimeSlot(dateTime.plusDays(1)),null,null));
        scheduledMeetingRepository.add(new Meeting(null, new TimeSlot(dateTime.plusDays(2)),null,null));
        assertEquals(scheduledMeetingRepository.getAll().size(), 5);
        assertEquals(scheduledMeetingRepository.getByDate(dateTime.toLocalDate()).size(), 3);
    }

    @Test
    public void should_get_scheduled_meeting_by_room(){
        scheduledMeetingRepository.add(new Meeting(null, null, MeetingRoomUniqueId.A,null));
        scheduledMeetingRepository.add(new Meeting(null, null, MeetingRoomUniqueId.A,null));
        scheduledMeetingRepository.add(new Meeting(null, null, MeetingRoomUniqueId.A,null));
        scheduledMeetingRepository.add(new Meeting(null, null, MeetingRoomUniqueId.D,null));
        scheduledMeetingRepository.add(new Meeting(null, null, MeetingRoomUniqueId.D,null));
        assertEquals(scheduledMeetingRepository.getAll().size(), 5);
        assertEquals(scheduledMeetingRepository.getByRoom(MeetingRoomUniqueId.A).size(), 3);
    }


}
