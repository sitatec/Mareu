package com.sitatech.mareu.events;

import com.sitatech.mareu.models.Meeting;

public class DisplayMeetingEvent {
    public final Meeting meetingToDisplay;

    public DisplayMeetingEvent(Meeting meetingToDisplay){
        this.meetingToDisplay = meetingToDisplay;
    }
}
