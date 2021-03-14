package com.sitatech.mareu.domain.models;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeSlot {
    private static final Duration DEFAULT_DURATION = Duration.ofMinutes(45);

    private LocalDateTime startTime;
    private Duration duration;

    public TimeSlot(LocalDateTime startTime, Duration duration) {
        this.startTime = startTime;
        this.duration = duration;
    }

    public TimeSlot(LocalDateTime startTime) {
        this.startTime = startTime;
        this.duration = DEFAULT_DURATION;
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
