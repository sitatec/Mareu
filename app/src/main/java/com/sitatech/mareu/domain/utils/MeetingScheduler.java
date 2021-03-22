package com.sitatech.mareu.domain.utils;

import androidx.annotation.NonNull;

import com.sitatech.mareu.domain.exceptions.FreeTimeSlotReleaseAttempt;
import com.sitatech.mareu.domain.exceptions.TimeSlotOverlapException;
import com.sitatech.mareu.domain.models.Meeting;
import com.sitatech.mareu.domain.models.MeetingRoom;
import com.sitatech.mareu.domain.repositories.MeetingRoomRepository;
import com.sitatech.mareu.domain.repositories.ScheduledMeetingRepository;

public class MeetingScheduler {

    private final ScheduledMeetingRepository scheduledMeetingRepository;
    private final MeetingRoomRepository meetingRoomRepository;

    public MeetingScheduler(ScheduledMeetingRepository meetingRepository, MeetingRoomRepository meetingRoomRepository){
        this.meetingRoomRepository = meetingRoomRepository;
        this.scheduledMeetingRepository = meetingRepository;
    }

    public void schedule(@NonNull Meeting meeting) throws TimeSlotOverlapException {
        final MeetingRoom meetingRoom = meetingRoomRepository.get(meeting.getRoomId());
        meetingRoom.reserve(meeting.getTimeSlot());
        scheduledMeetingRepository.add(meeting);
    }

    public void cancel(@NonNull Meeting meeting) throws FreeTimeSlotReleaseAttempt {
        final MeetingRoom meetingRoom = meetingRoomRepository.get(meeting.getRoomId());
        meetingRoom.release(meeting.getTimeSlot());
        scheduledMeetingRepository.remove(meeting);
    }

}