package com.sitatech.mareu.domain.models;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeSlot {
    private static final Duration DEFAULT_DURATION = Duration.ofMinutes(45);

    private LocalDateTime startTime;
    private Duration duration;

    public TimeSlot(LocalDateTime startTime, Duration duration) {
        setStartTime(startTime);
        setDuration(duration);
    }

    public TimeSlot(LocalDateTime startTime) {
        setStartTime(startTime);
        setDuration(DEFAULT_DURATION);
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
        return getStartTime().plus(getDuration());
    }

    public boolean overlapsWith(TimeSlot otherSlot){
        return this.getStartTime().isBefore(otherSlot.getEndTime())
                && otherSlot.getStartTime().isBefore(this.getEndTime());
    }
}
