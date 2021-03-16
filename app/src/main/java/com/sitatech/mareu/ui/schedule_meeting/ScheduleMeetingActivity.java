package com.sitatech.mareu.ui.schedule_meeting;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.sitatech.mareu.R;
import com.sitatech.mareu.databinding.ActivityScheduleMeetingBinding;
import com.sitatech.mareu.domain.exceptions.TimeSlotOverlapException;
import com.sitatech.mareu.domain.models.Meeting;
import com.sitatech.mareu.domain.models.MeetingRoom;
import com.sitatech.mareu.domain.models.TimeSlot;
import com.sitatech.mareu.domain.utils.MeetingScheduler;
import com.sitatech.mareu.ui.schedule_meeting.fragments.pickers.DatePickerFragment;
import com.sitatech.mareu.ui.schedule_meeting.fragments.pickers.DurationPickerFragment;
import com.sitatech.mareu.ui.schedule_meeting.fragments.pickers.TimePickerFragment;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import top.defaults.colorpicker.ColorPickerPopup;

public class ScheduleMeetingActivity extends AppCompatActivity {

    private ActivityScheduleMeetingBinding viewBinding;
    private final MeetingScheduler meetingScheduler = MeetingScheduler.getInstance();
    private LocalDate meetingDate;
    private LocalTime meetingStartTime;
    private Duration meetingDuration;
    private MeetingRoom meetingRoom;
    private DatePickerFragment datePickerFragment;
    private TimePickerFragment timePickerFragment;
    private DurationPickerFragment durationPickerFragment;
    private Set<String> participantEmails = new HashSet<>();
    private int color = R.color.default_meeting_color;

    private final AdapterView.OnItemSelectedListener onMeetingRoomSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if(--position < 0) return; // The first item is a "Hint"
            viewBinding.meetingRoomErrorHint.setVisibility(View.INVISIBLE);// The error hint might be
            // visible if the user tried to submit the form without selecting the meeting room, so we hide it
            meetingRoom = meetingScheduler.getAllMeetingRooms().get(position);
        }
        @Override public void onNothingSelected(AdapterView<?> parent) {
            viewBinding.meetingRoomErrorHint.setVisibility(View.VISIBLE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivityScheduleMeetingBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());
        setUpPickers();
        setUpClickListeners();
        setUpMeetingRoomSelector();
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void setUpPickers() {
        datePickerFragment = new DatePickerFragment(this::onMeetingDateSet);
        timePickerFragment = new TimePickerFragment(this::onOnMeetingTimeSet);
        durationPickerFragment = new DurationPickerFragment(this::onMeetingDurationSet);
    }

    private void setUpClickListeners() {
        viewBinding.datePickerAction.setOnClickListener(this::showDatePicker);
        viewBinding.timePickerAction.setOnClickListener(this::showTimePicker);
        viewBinding.durationPickerAction.setOnClickListener(this::showDurationPicker);
        viewBinding.colorPickerAction.setOnClickListener(this::showColorPicker);
        viewBinding.meetingColorView.setOnClickListener(this::showColorPicker);
        viewBinding.addParticipantButton.setOnClickListener(this::addNewParticipant);
        viewBinding.submit.setOnClickListener(this::schedule);
    }

    private void setUpMeetingRoomSelector(){
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item){
            @Override public boolean isEnabled(int position) {
                return position > 0; // The first item mustn't be selected because it is a "Hint".
            }
        };
        adapter.add("Choisir la salle");
        adapter.addAll(getMeetingRoomNames());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        viewBinding.meetingRoomSelector.setAdapter(adapter);
        viewBinding.meetingRoomSelector.setOnItemSelectedListener(onMeetingRoomSelectedListener);
    }

    private List<String> getMeetingRoomNames(){
        final List<String> meetingRoomNames = new ArrayList<>();
        for (MeetingRoom meetingRoom : meetingScheduler.getAllMeetingRooms()){
            meetingRoomNames.add(meetingRoom.getUniqueId().toString());
        }
        return meetingRoomNames;
    }

    private void showTimePicker(View v){
        timePickerFragment.show(getSupportFragmentManager(), "meeting_start_time_picker");
    }

    private void onOnMeetingTimeSet(TimePicker view, int hourOfDay, int minute){
        meetingStartTime = LocalTime.of(hourOfDay,minute);
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH'h'mm");
        viewBinding.timePickerAction.setText(meetingStartTime.format(formatter));
        viewBinding.timePickerAction.setError(null);// Remove the error hint if it was set
    }

    private void showDatePicker(View v){
        datePickerFragment.show(getSupportFragmentManager(), "meeting_date_time_picker");
    }

    private void onMeetingDateSet(DatePicker view, int year, int month, int day){
        meetingDate = LocalDate.of(year, month, day);
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        viewBinding.datePickerAction.setText(meetingDate.format(formatter));
        viewBinding.datePickerAction.setError(null);// Remove the error hint if it was set
    }

    private void showDurationPicker(View v){
        durationPickerFragment.show(getSupportFragmentManager(), "meeting_duration_picker");
    }

    private void onMeetingDurationSet(Duration duration){
        meetingDuration = duration;
        viewBinding.durationPickerAction.setText(duration.toHours() + " heures et " + duration.toMinutes() + " minutes");
        viewBinding.durationPickerAction.setError(null);// Remove the error hint if it was set
    }

    private void addNewParticipant(View v){
        String participantEmail = viewBinding.participantEmailEdit.getText().toString();
        if(!participantEmail.isEmpty() && participantEmails.add(participantEmail)) {
            if (participantEmails.size() > 1)
                participantEmail = ", " + participantEmail;
            viewBinding.addedParticipantEmailsContainer.append(participantEmail);
            viewBinding.participantEmailEdit.setText("");
        }
    }

    private void schedule(View v){
        if(validateForm()) {
            final String meetingSubject = viewBinding.meetingSubjectEdit.getText().toString();
            final LocalDateTime meetingDateTime = LocalDateTime.of(meetingDate, meetingStartTime);
            final TimeSlot meetingSlot = new TimeSlot(meetingDateTime, meetingDuration);
            final Meeting meeting = new Meeting(participantEmails, meetingSlot, meetingRoom.getUniqueId(), meetingSubject, color);
            try {
                meetingScheduler.schedule(meeting);
                Snackbar.make(viewBinding.getRoot(), R.string.meeting_scheduled_successefully_msg, Snackbar.LENGTH_SHORT);
            } catch (TimeSlotOverlapException e) {
                Snackbar.make(viewBinding.getRoot(), R.string.time_slot_overlaps_msg, Snackbar.LENGTH_LONG);
            }
        }
    }

    private boolean validateForm(){
        boolean isValid = true;
        if(meetingRoom == null) {
            isValid = false;
            viewBinding.meetingRoomErrorHint.setVisibility(View.VISIBLE);
        }if( meetingDate == null ){
            isValid = false;
            indicateError(viewBinding.datePickerAction);
        } if(meetingStartTime == null) {
            isValid = false;
            indicateError(viewBinding.timePickerAction);
        }if(meetingDuration == null || meetingDuration.isZero()){
            isValid = false;
            indicateError(viewBinding.durationPickerAction);
        }if(viewBinding.meetingSubjectEdit.getText().toString().isEmpty()){
            isValid = false;
            indicateError(viewBinding.meetingSubjectEdit);
        }if(participantEmails.size() < 2){
            isValid = false;
            viewBinding.participantEmailEdit.setError(getString(R.string.required_field_error_msg));
            viewBinding.participantEmailEdit.setHint("Minimum 2 participants");
            viewBinding.participantEmailEdit.setHintTextColor(Color.RED);
        }
        return isValid;
    }

    private void indicateError(EditText editText){
        final String errorMessage = getString(R.string.required_field_error_msg);
        editText.setError(errorMessage);
        editText.setHint(errorMessage);
        editText.setHintTextColor(Color.RED);
//        editText.setTextSize(12);
    }

    private void showColorPicker(View v){
        new ColorPickerPopup.Builder(this)
                .initialColor(R.color.default_meeting_color).enableBrightness(false)
                .enableAlpha(true).okTitle("Choisir")
                .cancelTitle("Annuler").showValue(false)
                .build()
                .show(viewBinding.getRoot(), new ColorPickerPopup.ColorPickerObserver() {
                    @Override
                    public void onColorPicked(int color) {
                        ScheduleMeetingActivity.this.color = color;
                        viewBinding.meetingColorView.setBackgroundColor(color);
                        viewBinding.colorPickerAction.setHint("Couleur choisie");
                    }
                });
    }
}