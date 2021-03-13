package com.sitatech.mareu.domain.models;

import com.sitatech.mareu.domain.enums.MeetingRoomUniqueId;
import com.sitatech.mareu.domain.exceptions.TimeSlotOverlapException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    public void reserve(TimeSlot ...timeSlots) throws TimeSlotOverlapException{
        for (TimeSlot currentSlot : timeSlots) reserve(currentSlot);
    }

    public void dispose(TimeSlot timeSlot){
        reservedSlots.remove(timeSlot);
    }

    public MeetingRoomUniqueId getUniqueId() {
        return uniqueId;
    }

    public List<TimeSlot> getAllReservedSlots() {
        return reservedSlots;
    }
}
