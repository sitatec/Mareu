package com.sitatech.mareu.ui.utils;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.sitatech.mareu.domain.enums.MeetingRoomUniqueId;
import com.sitatech.mareu.domain.models.MeetingRoom;
import com.sitatech.mareu.domain.utils.MeetingScheduler;

import java.util.ArrayList;
import java.util.List;

public abstract class MeetingRoomSpinnerHelper {

    public static interface OnItemSelectedListener {
        void onSelect(MeetingRoomUniqueId chosenId);
    }

    private static final MeetingScheduler MEETING_SCHEDULER = MeetingScheduler.getInstance();

    public static void setUp(Context context, Spinner spinner, OnItemSelectedListener onItemSelected, Runnable onNothingSelected){
        final ArrayAdapter<String> adapter = setUpSpinnerAdapter(context);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(--position < 0) return; // The first item is a "Hint"
                onItemSelected.onSelect(MEETING_SCHEDULER.getAllMeetingRooms().get(position).getUniqueId());
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {
                onNothingSelected.run();
            }
        });
    }

    public static void setUp(Context context, Spinner spinner, OnItemSelectedListener onItemSelected){
        final ArrayAdapter<String> adapter = setUpSpinnerAdapter(context);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(--position < 0) return; // The first item is a "Hint"
                onItemSelected.onSelect(MEETING_SCHEDULER.getAllMeetingRooms().get(position).getUniqueId());
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private static ArrayAdapter<String> setUpSpinnerAdapter(Context context){
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item){
            @Override public boolean isEnabled(int position) {
                return position > 0; // The first item mustn't be selected because it is a "Hint".
            }
        };
        adapter.add("Choisir la salle");
        adapter.addAll(getMeetingRoomNames());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    private static List<String> getMeetingRoomNames(){
        final List<String> meetingRoomNames = new ArrayList<>();
        for (MeetingRoom meetingRoom : MEETING_SCHEDULER.getAllMeetingRooms()){
            meetingRoomNames.add(meetingRoom.getUniqueId().toString());
        }
        return meetingRoomNames;
    }
}
