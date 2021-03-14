package com.sitatech.mareu.events;

import com.sitatech.mareu.domain.models.Meeting;

public class DeleteMeetingEvent {
    public final Meeting meetingToDelete;

    public DeleteMeetingEvent(Meeting meetingToDelete){
        this.meetingToDelete = meetingToDelete;
    }
}
