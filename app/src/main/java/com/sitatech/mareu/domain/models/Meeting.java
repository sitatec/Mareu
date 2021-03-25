package com.sitatech.mareu.domain.models;

import com.sitatech.mareu.R;
import com.sitatech.mareu.domain.enums.MeetingRoomUniqueId;

import androidx.annotation.NonNull;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Meeting {

    private Set<String> participantEmails;
    private TimeSlot timeSlot;
    private MeetingRoomUniqueId roomId;
    private String subject;
    private int color;

    public Meeting(){}

    public Meeting(Set<String> participantEmails, TimeSlot timeSlot, MeetingRoomUniqueId roomId, String subject, int color) {
        setParticipantEmails(participantEmails);
        setTimeSlot(timeSlot);
        setRoomId(roomId);
        setSubject(subject);
        setColor(color);
    }

    public Meeting(Set<String> participantEmails, TimeSlot timeSlot, MeetingRoomUniqueId roomId, String subject) {
        setParticipantEmails(participantEmails);
        setTimeSlot(timeSlot);
        setRoomId(roomId);
        setSubject(subject);
        setColor(0xF8FFBF00);
    }

    public Set<String> getParticipantEmails() {
        return participantEmails;
    }

    public void setParticipantEmails(Set<String> participantEmails) {
        this.participantEmails = participantEmails;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public LocalDateTime getDateTime() {
        return timeSlot.getStartTime();
    }

    public MeetingRoomUniqueId getRoomId() {
        return roomId;
    }

    public void setRoomId(MeetingRoomUniqueId roomId) {
        this.roomId = roomId;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Duration getDuration() {
        return timeSlot.getDuration();
    }

    public String getTitle(){
        return joinStrings(" - ", roomId.toString(), getFormattedTime(), getSubject());
    }

    public String getSubtitle(){
        return joinStrings(", ", getParticipantEmails().toArray(new String[0]));
    }

    public String getFormattedTime(){
        return getDateTime().format(DateTimeFormatter.ofPattern("HH'h'mm"));
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(TimeSlot timeSlot) {
        this.timeSlot = timeSlot;
    }

    @NonNull
    private String joinStrings(String delimiter, String... strings){
        // String.join() require JAVA 8 which require android API level >=26
        final List<String> stringsToJoin = Arrays.asList(strings);
        final StringBuilder stringBuilder = new StringBuilder();
        final int lastStringIndex = stringsToJoin.size() -1;
        final String lastString = stringsToJoin.get(lastStringIndex);

        for (int i = 0; i < lastStringIndex; i++){
            stringBuilder.append(stringsToJoin.get(i)).append(delimiter);
        }
        return stringBuilder.append(lastString).toString();
    }

}
