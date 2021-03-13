package com.sitatech.mareu.domain.models;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeSlot {
    private LocalDateTime startTime;
    private Duration duration;

    public TimeSlot(LocalDateTime startTime, Duration duration) {
        this.startTime = startTime;
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getEndTime(){
        return startTime.plus(duration);
    }

    public boolean overlapsWith(TimeSlot otherSlot){
        return this.startTime.isBefore(otherSlot.getEndTime())
                && otherSlot.startTime.isBefore(this.getEndTime());
    }
}
