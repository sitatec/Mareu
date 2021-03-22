package com.sitatech.mareu.domain.repositories;

import com.sitatech.mareu.domain.enums.MeetingRoomUniqueId;
import com.sitatech.mareu.domain.models.Meeting;
import com.sitatech.mareu.ui.schedule_meeting.ScheduleMeetingActivity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ScheduledMeetingRepository {

    private final List<Meeting> scheduledMeetings;

    public ScheduledMeetingRepository(){
        scheduledMeetings = new ArrayList<>();
    }

    public boolean add(Meeting meeting){
       return scheduledMeetings.add(meeting);
    }

    public boolean remove(Meeting meeting){
        return scheduledMeetings.remove(meeting);
    }

    public List<Meeting> getAll() {
        return scheduledMeetings;
    }

    public List<Meeting> getByDate(LocalDate date){
        final Predicate<Meeting> byDate = meeting -> meeting.getDateTime().toLocalDate().isEqual(date);
        return scheduledMeetings.stream().filter(byDate).collect(Collectors.toList());
    }

    public List<Meeting> getByRoom(MeetingRoomUniqueId roomId){
        final Predicate<Meeting> byRoom = meeting -> meeting.getRoomId().equals(roomId);
        return scheduledMeetings.stream().filter(byRoom).collect(Collectors.toList());
    }

}
