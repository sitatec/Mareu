package com.sitatech.mareu.domain.repositories;

import com.sitatech.mareu.domain.enums.MeetingRoomUniqueId;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

@RunWith(JUnit4.class)
public class MeetingRoomRepositoryTest {

    private MeetingRoomRepository meetingRoomRepository;

    @Before
    public void setUp(){
        meetingRoomRepository = new MeetingRoomRepository();
    }

    @Test
    public void should_initialize_meeting_rooms(){
        assertEquals(meetingRoomRepository.getAll().size(), MeetingRoomUniqueId.values().length);
    }

    @Test
    public void should_get_meeting_room(){
        assertSame(meetingRoomRepository.get(MeetingRoomUniqueId.D).getUniqueId(), MeetingRoomUniqueId.D);
    }

    @Test
    public void should_get_all_meeting_rooms(){
        assertEquals(meetingRoomRepository.getAll().size(), MeetingRoomUniqueId.values().length);
    }

}
