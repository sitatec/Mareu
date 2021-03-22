package com.sitatech.mareu.domain.models;

import androidx.annotation.NonNull;

import com.sitatech.mareu.domain.enums.MeetingRoomUniqueId;
import com.sitatech.mareu.domain.exceptions.FreeTimeSlotReleaseAttempt;
import com.sitatech.mareu.domain.exceptions.TimeSlotOverlapException;

import java.util.ArrayList;
import java.util.List;

public class MeetingRoom {
    private final MeetingRoomUniqueId uniqueId;
    private final List<TimeSlot> reservedSlots;

    public MeetingRoom(MeetingRoomUniqueId uniqueId) {
        this.uniqueId = uniqueId;
        reservedSlots = new ArrayList<>();
    }

    public boolean isAvailableAt(TimeSlot timeSlot){
        boolean isAvailable = true;
        for (TimeSlot currentSlot : reservedSlots){
            if (currentSlot.overlapsWith(timeSlot)){
                isAvailable = false;
                break;
            }
        }
        return isAvailable;
    }

    public void reserve(TimeSlot timeSlot) throws TimeSlotOverlapException {
        if(isAvailableAt(timeSlot)) {
            reservedSlots.add(timeSlot);
        } else
            throw new TimeSlotOverlapException();
    }

    public void reserve(@NonNull TimeSlot ...timeSlots) throws TimeSlotOverlapException{
        for (TimeSlot currentSlot : timeSlots) reserve(currentSlot);
    }

    public void release(TimeSlot timeSlot) throws FreeTimeSlotReleaseAttempt {
        if(!reservedSlots.remove(timeSlot)) throw new FreeTimeSlotReleaseAttempt();
    }

    public MeetingRoomUniqueId getUniqueId() {
        return uniqueId;
    }

    public List<TimeSlot> getAllReservedSlots() {
        return reservedSlots;
    }
}
