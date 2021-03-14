package com.sitatech.mareu.domain.exceptions;

public class TimeSlotOverlapException extends Exception {
    public TimeSlotOverlapException(){
        super("You can't reserve a TimeSlot which overlap an already reserved TimeSlot.");
    }
}
