package com.sitatech.mareu.domain.utils;

import androidx.annotation.VisibleForTesting;

import com.sitatech.mareu.domain.enums.MeetingRoomUniqueId;
import com.sitatech.mareu.domain.exceptions.FreeTimeSlotReleaseAttempt;
import com.sitatech.mareu.domain.exceptions.TimeSlotOverlapException;
import com.sitatech.mareu.domain.models.Meeting;
import com.sitatech.mareu.domain.models.MeetingRoom;
import com.sitatech.mareu.domain.models.TimeSlot;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MeetingScheduler {

    private static final MeetingScheduler INSTANCE = new MeetingScheduler();

    private final List<Meeting> scheduledMeetings;
    private final Map<MeetingRoomUniqueId, MeetingRoom> meetingRooms;

    private MeetingScheduler(){
        scheduledMeetings = new ArrayList<>();
        meetingRooms = new HashMap<>();
        initializeMeetingRooms();
    }

    private void initializeMeetingRooms(){
        for (MeetingRoomUniqueId id : MeetingRoomUniqueId.values()){
            meetingRooms.put(id, new MeetingRoom(id));
        }
    }

    public static MeetingScheduler getInstance(){
        return INSTANCE;
    }

    @VisibleForTesting
    public static MeetingScheduler getInstanceForTests(){
        return new MeetingScheduler();
    }

    public void schedule(@NotNull Meeting meeting) throws TimeSlotOverlapException {
        final MeetingRoom meetingRoom = meetingRooms.get(meeting.getRoomId());
        meetingRoom.reserve(meeting.getTimeSlot());
        scheduledMeetings.add(meeting);
    }

    public void cancel(@NotNull Meeting meeting) throws FreeTimeSlotReleaseAttempt {
        final MeetingRoom meetingRoom = meetingRooms.get(meeting.getRoomId());
        meetingRoom.release(meeting.getTimeSlot());
        scheduledMeetings.remove(meeting);
    }


    public List<MeetingRoom> getAllMeetingRooms() {
        return new ArrayList<>(meetingRooms.values());
    }

    public List<Meeting> getAllScheduledMeetings() {
        return scheduledMeetings;
    }

    public List<Meeting> getScheduledMeetingsByDate(LocalDate date) {
        final Predicate<Meeting> byDate = meeting -> meeting.getDateTime().toLocalDate().isEqual(date);
        return scheduledMeetings.stream().filter(byDate).collect(Collectors.toList());
    }

    public List<Meeting> getScheduledMeetingsByRoom(MeetingRoomUniqueId roomId) {
        final Predicate<Meeting> byRoom = meeting -> meeting.getRoomId().equals(roomId);
        return scheduledMeetings.stream().filter(byRoom).collect(Collectors.toList());
    }

//    public List<MeetingRoom> getAvailableMeetingRoomsAt(TimeSlot timeSlot) {
//        return new ArrayList<>();
//    }

}