package com.sitatech.mareu.domain.exceptions;

public class FreeTimeSlotReleaseAttempt extends Exception {
    public FreeTimeSlotReleaseAttempt(){
        super("Not reserved TimeSlot can't be released.");
    }
}
