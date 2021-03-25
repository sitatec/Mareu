package com.sitatech.mareu.ui.schedule_meeting;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.android.material.snackbar.Snackbar;
import com.sitatech.mareu.R;
import com.sitatech.mareu.databinding.ActivityScheduleMeetingBinding;
import com.sitatech.mareu.domain.enums.MeetingRoomUniqueId;
import com.sitatech.mareu.domain.exceptions.TimeSlotOverlapException;
import com.sitatech.mareu.domain.models.Meeting;
import com.sitatech.mareu.domain.models.TimeSlot;
import com.sitatech.mareu.domain.utils.MeetingScheduler;
import com.sitatech.mareu.ui.schedule_meeting.fragments.pickers.DatePickerFragment;
import com.sitatech.mareu.ui.schedule_meeting.fragments.pickers.DurationPickerFragment;
import com.sitatech.mareu.ui.schedule_meeting.fragments.pickers.TimePickerFragment;
import com.sitatech.mareu.ui.schedule_meeting.utils.MeetingRoomSpinnerHelper;
import com.sitatech.mareu.utils.DependencyContainer;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import top.defaults.colorpicker.ColorPickerPopup;

public class ScheduleMeetingActivity extends AppCompatActivity {

  private ActivityScheduleMeetingBinding viewBinding;
  private final MeetingScheduler meetingScheduler = DependencyContainer.getMeetingScheduler();
  private LocalDate meetingDate;
  private LocalTime meetingStartTime;
  private Duration meetingDuration;
  private MeetingRoomUniqueId meetingRoomId;
  private DatePickerFragment datePickerFragment;
  private TimePickerFragment timePickerFragment;
  private DurationPickerFragment durationPickerFragment;
  private final Set<String> participantEmails = new HashSet<>();
  private int color = R.color.default_meeting_color;

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

  private void setUpMeetingRoomSelector() {
    MeetingRoomSpinnerHelper.setUp(
        viewBinding.meetingRoomSelector,
        roomId -> meetingRoomId = roomId,
        () -> viewBinding.meetingRoomErrorHint.setVisibility(View.VISIBLE));
  }

  private void showTimePicker(View v) {
    timePickerFragment.show(getSupportFragmentManager(), "meeting_start_time_picker");
  }

  private void onOnMeetingTimeSet(TimePicker view, int hourOfDay, int minute) {
    meetingStartTime = LocalTime.of(hourOfDay, minute);
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH'h'mm");
    viewBinding.timePickerAction.setText(meetingStartTime.format(formatter));
    viewBinding.timePickerAction.setError(null); // Remove the error hint if it was set
  }

  private void showDatePicker(View v) {
    datePickerFragment.show(getSupportFragmentManager(), "meeting_date_picker");
  }

  private void onMeetingDateSet(DatePicker view, int year, int month, int day) {
    meetingDate = LocalDate.of(year, month, day);
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    viewBinding.datePickerAction.setText(meetingDate.format(formatter));
    viewBinding.datePickerAction.setError(null); // Remove the error hint if it was set
  }

  private void showDurationPicker(View v) {
    durationPickerFragment.show(getSupportFragmentManager(), "meeting_duration_picker");
  }

  private void onMeetingDurationSet(Duration duration) {
    meetingDuration = duration;
    viewBinding.durationPickerAction.setText(
        String.format(
            Locale.FRENCH, "%d heures et %d minutes", duration.toHours(), duration.toMinutes()));
    viewBinding.durationPickerAction.setError(null); // Remove the error hint if it was set
  }

  private void addNewParticipant(View v) {
    String participantEmail = viewBinding.participantEmailEdit.getText().toString();
    if (!participantEmail.isEmpty() && participantEmails.add(participantEmail)) {
      if (participantEmails.size() > 1) participantEmail = ", " + participantEmail;
      viewBinding.addedParticipantEmailsContainer.append(participantEmail);
      viewBinding.participantEmailEdit.setText("");
    }
  }

  private void schedule(View v) {
    if (validateForm()) {
      final String meetingSubject = viewBinding.meetingSubjectEdit.getText().toString();
      final LocalDateTime meetingDateTime = LocalDateTime.of(meetingDate, meetingStartTime);
      final TimeSlot meetingSlot = new TimeSlot(meetingDateTime, meetingDuration);
      final Meeting meeting =
          new Meeting(participantEmails, meetingSlot, meetingRoomId, meetingSubject, color);
      try {
        meetingScheduler.schedule(meeting);
        Snackbar.make(
                viewBinding.getRoot(),
                R.string.meeting_scheduled_successfully_msg,
                Snackbar.LENGTH_SHORT)
            .show();
      } catch (TimeSlotOverlapException e) {
        Snackbar.make(viewBinding.getRoot(), R.string.time_slot_overlaps_msg, Snackbar.LENGTH_LONG)
            .show();
      }
    }
  }

  private boolean validateForm() {
    boolean isValid = true;
    if (meetingRoomId == null) {
      isValid = false;
      viewBinding.meetingRoomErrorHint.setVisibility(View.VISIBLE);
    }
    if (viewBinding.meetingSubjectEdit.getText().toString().isEmpty()) {
      isValid = false;
      indicateError(viewBinding.meetingSubjectEdit);
    }
    if (participantEmails.size() < 2) {
      isValid = false;
      viewBinding.participantEmailEdit.setError(
          getString(R.string.participant_emails_field_error_msg));
      viewBinding.participantEmailEdit.setHint(R.string.participant_emails_field_error_msg);
      viewBinding.participantEmailEdit.setHintTextColor(Color.RED);
    }
    return arePickerFieldsValid() && isValid;
  }

  public boolean arePickerFieldsValid() {
    boolean isValid = true;
    if (meetingDate == null) {
      isValid = false;
      indicateError(viewBinding.datePickerAction);
    }
    if (meetingStartTime == null) {
      isValid = false;
      indicateError(viewBinding.timePickerAction);
    }
    if (meetingDuration == null || meetingDuration.isZero()) {
      isValid = false;
      indicateError(viewBinding.durationPickerAction);
    }
    return isValid;
  }

  private void indicateError(EditText editText) {
    final String errorMessage = getString(R.string.required_field_error_msg);
    editText.setHint(errorMessage);
    editText.setHintTextColor(Color.RED);
    editText.setError(errorMessage);
  }

  private void showColorPicker(View v) {
    new ColorPickerPopup.Builder(this)
        .initialColor(getResources().getColor(R.color.default_meeting_color))
        .enableBrightness(false)
        .enableAlpha(true)
        .okTitle(getString(R.string.choose_color))
        .cancelTitle(getString(R.string.cancel_choosing_color))
        .showValue(false)
        .build()
        .show(
            viewBinding.getRoot(),
            new ColorPickerPopup.ColorPickerObserver() {
              @Override
              public void onColorPicked(int color) {
                ScheduleMeetingActivity.this.color = color;
                viewBinding.meetingColorView.setBackgroundColor(color);
                viewBinding.colorPickerAction.setHint("Couleur choisie");
              }
            });
  }
}
