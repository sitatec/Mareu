package com.sitatech.mareu.domain.models;

import com.sitatech.mareu.domain.enums.MeetingRoomUniqueId;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class Meeting {

    private static final Duration DEFAULT_DURATION = Duration.ofMinutes(45);

    private List<String> participantEmails;
    private LocalDateTime dateTime;
    private Duration duration;
    private MeetingRoomUniqueId roomId;
    private String subject;
    private int color;

    public Meeting(){

    }

    public Meeting(List<String> participantEmails, LocalDateTime dateTime, Duration duration, MeetingRoomUniqueId roomId, String subject, int color) {
        this.participantEmails = participantEmails;
        this.dateTime = dateTime;
        this.duration = duration;
        this.roomId = roomId;
        this.subject = subject;
        this.color = color;
    }

    public Meeting(List<String> participantEmails, LocalDateTime dateTime, MeetingRoomUniqueId roomId, String subject, int color) {
        this.participantEmails = participantEmails;
        this.dateTime = dateTime;
        this.roomId = roomId;
        this.subject = subject;
        this.color = color;
        this.duration = DEFAULT_DURATION;
    }


    public List<String> getParticipantEmails() {
        return participantEmails;
    }

    public void setParticipantEmails(List<String> participantEmails) {
        this.participantEmails = participantEmails;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
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
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public String getTitle(){
        return joinStrings(" - ", "Réunion " + roomId, getFormattedTime(), subject);
    }

    public String getSubtitle(){
        return joinStrings(", ",(String[]) participantEmails.toArray());
    }

    public String getFormattedTime(){
        return dateTime.format(DateTimeFormatter.ofPattern("HH'h'mm"));
    }

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
