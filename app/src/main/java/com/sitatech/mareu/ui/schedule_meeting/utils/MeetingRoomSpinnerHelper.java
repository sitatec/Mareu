package com.sitatech.mareu.ui.schedule_meeting.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.sitatech.mareu.domain.enums.MeetingRoomUniqueId;
import com.sitatech.mareu.domain.models.MeetingRoom;
import com.sitatech.mareu.domain.repositories.MeetingRoomRepository;
import com.sitatech.mareu.utils.DependencyContainer;

import java.util.ArrayList;
import java.util.List;

public abstract class MeetingRoomSpinnerHelper {

    public interface OnItemSelectedListener {
        void onSelect(MeetingRoomUniqueId chosenId);
    }

    private static final MeetingRoomRepository MEETING_ROOM_REPOSITORY = DependencyContainer.getMeetingRoomRepository();

    public static void setUp(Spinner spinner, OnItemSelectedListener onItemSelected, Runnable onNothingSelected){
        final ArrayAdapter<String> adapter = setUpSpinnerAdapter(spinner.getContext());
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(--position < 0) return; // The first item is a "Hint"
                onItemSelected.onSelect(MEETING_ROOM_REPOSITORY.getAll().get(position).getUniqueId());
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {
                onNothingSelected.run();
            }
        });
    }

    private static ArrayAdapter<String> setUpSpinnerAdapter(Context context){
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item){
            @Override public boolean isEnabled(int position) {
                return position > 0; // The first item must be disabled because it is a "Hint".
            }
        };
        adapter.add("Choisir la salle");
        adapter.addAll(getMeetingRoomNames());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    private static List<String> getMeetingRoomNames(){
        final List<String> meetingRoomNames = new ArrayList<>();
        for (MeetingRoom meetingRoom : MEETING_ROOM_REPOSITORY.getAll()){
            meetingRoomNames.add(meetingRoom.getUniqueId().toString());
        }
        return meetingRoomNames;
    }
}


//    public static void setUp(Spinner spinner, OnItemSelectedListener onItemSelected){
//        final ArrayAdapter<String> adapter = setUpSpinnerAdapter(spinner.getContext());
//        spinner.setAdapter(adapter);
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Log.i("Filter_test", MEETING_ROOM_REPOSITORY.getAll().get(position).getUniqueId().toString() + "_helper");
//                if(--position < 0) return; // The first item is a "Hint"
//                onItemSelected.onSelect(MEETING_ROOM_REPOSITORY.getAll().get(position).getUniqueId());
//            }
//            @Override public void onNothingSelected(AdapterView<?> parent) {}
//        });
//    }