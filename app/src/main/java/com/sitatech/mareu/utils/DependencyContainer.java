package com.sitatech.mareu.utils;

import com.sitatech.mareu.domain.repositories.MeetingRoomRepository;
import com.sitatech.mareu.domain.repositories.ScheduledMeetingRepository;
import com.sitatech.mareu.domain.utils.MeetingScheduler;

public abstract class DependencyContainer {

    private static final MeetingRoomRepository meetingRoomRepository = new MeetingRoomRepository();
    private static final ScheduledMeetingRepository scheduledMeetingRepository = new ScheduledMeetingRepository();
    private static final MeetingScheduler meetingScheduler =
            new MeetingScheduler(scheduledMeetingRepository, meetingRoomRepository);

    public static MeetingRoomRepository getMeetingRoomRepository(){
        return meetingRoomRepository;
    }

    public static ScheduledMeetingRepository getScheduledMeetingRepository(){
        return scheduledMeetingRepository;
    }

    public static MeetingScheduler getMeetingScheduler(){
        return meetingScheduler;
    }

}
