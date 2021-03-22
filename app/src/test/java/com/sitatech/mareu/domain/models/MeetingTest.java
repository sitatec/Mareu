package com.sitatech.mareu.domain.models;

import com.sitatech.mareu.domain.enums.MeetingRoomUniqueId;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RunWith(JUnit4.class)
public class MeetingTest {

    private static Meeting meeting;
    private static final Set<String> MEETING_PARTICIPANTS = new HashSet<>( Arrays.asList(
            "maxim@lamezone.com", "test.t@test.t", "fake@test.com", "last@one.com"
    ));
    private static final TimeSlot DEFAULT_SLOT = new TimeSlot(LocalDateTime.now(), Duration.ofMinutes(45));

    @Before
    public void setUp(){;
        meeting = new Meeting(MEETING_PARTICIPANTS, DEFAULT_SLOT,
                MeetingRoomUniqueId.A, "Subject", 0xFF00AA);
    }

    @Test
    public void meeting_should_have_default_duration_to_45_minutes(){
        final Meeting meeting = new Meeting(MEETING_PARTICIPANTS, DEFAULT_SLOT,
                MeetingRoomUniqueId.A, "Subject", 0xFF00AA
        );
        assertEquals(meeting.getDuration().toMinutes(), 45);
    }

    @Test
    public void meeting_title_should_be_formatted(){
        final String expectedTitle =  String.join(" - ", meeting.getRoomId().toString(),
                meeting.getFormattedTime(), meeting.getSubject());
        assertEquals(meeting.getTitle(), expectedTitle);
    }

    @Test
    public void meeting_subtitle_should_contains_participants_email_addresses(){
        assertEquals(meeting.getSubtitle(), String.join(", ", MEETING_PARTICIPANTS));
    }

    @Test
    public void meeting_time_should_be_formatted(){
        final String hour = String.format("%02d", meeting.getDateTime().getHour());
        final String expectedTime = hour + "h" + meeting.getDateTime().getMinute();
        assertEquals(meeting.getFormattedTime(), expectedTime);
    }

}
