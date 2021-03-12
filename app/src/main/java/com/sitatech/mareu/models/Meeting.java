package com.sitatech.mareu.models;

import com.sitatech.mareu.enums.MeetingRoom;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Meeting {

    private List<String> participants;
    private LocalDate date;
    private LocalTime time;
    private MeetingRoom room;
    private String subject;
    private int color;

    public Meeting(List<String> participants, LocalDate date, LocalTime time, MeetingRoom room, String subject, int color) {
        this.participants = participants;
        this.date = date;
        this.time = time;
        this.room = room;
        this.subject = subject;
        this.color = color;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public MeetingRoom getRoom() {
        return room;
    }

    public void setRoom(MeetingRoom room) {
        this.room = room;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
