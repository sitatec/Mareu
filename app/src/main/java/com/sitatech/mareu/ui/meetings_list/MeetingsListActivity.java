package com.sitatech.mareu.ui.meetings_list;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;

import com.google.android.material.snackbar.Snackbar;
import com.sitatech.mareu.R;
import com.sitatech.mareu.databinding.ActivityMeetingListBinding;
import com.sitatech.mareu.domain.enums.MeetingRoomUniqueId;
import com.sitatech.mareu.domain.exceptions.FreeTimeSlotReleaseAttempt;
import com.sitatech.mareu.domain.models.MeetingRoom;
import com.sitatech.mareu.domain.repositories.ScheduledMeetingRepository;
import com.sitatech.mareu.events.DeleteMeetingEvent;
import com.sitatech.mareu.domain.models.Meeting;
import com.sitatech.mareu.ui.schedule_meeting.ScheduleMeetingActivity;
import com.sitatech.mareu.ui.fragments.pickers.DatePickerFragment;
import com.sitatech.mareu.utils.DependencyContainer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MeetingsListActivity extends AppCompatActivity {

    private final ScheduledMeetingRepository scheduledMeetingRepository =
            DependencyContainer.getScheduledMeetingRepository();
    private final List<Meeting> meetings = new ArrayList<>(scheduledMeetingRepository.getAll());
    private ActivityMeetingListBinding viewBinding;
    private MeetingsListAdapter meetingsListAdapter;
    private DatePickerFragment datePickerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivityMeetingListBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());
        setUpRecyclerView();
        viewBinding.scheduleMeetingButton.setOnClickListener(this::startScheduleMeetingActivity);
        datePickerFragment = new DatePickerFragment(this::onMeetingFilteredByDate);
    }

    @Override
    protected void onResume() {
        super.onResume();
        meetingsListAdapter.notifyDataSetChanged();
    }

    private void startScheduleMeetingActivity(View button){
        startActivity(new Intent(this, ScheduleMeetingActivity.class));
    }

    private void onMeetingFilteredByDate(DatePicker view, int year, int month, int day){
        final LocalDate date = LocalDate.of(year, month + 1, day); // Add 1 to month because
        // the callback will be called with month value (0-11) instead of (1-12) for compatibility
        // with Calendar.MONTH. See the `DatePickerDialog.OnDateSetListener` documentation for more info
        meetings.clear();
        meetings.addAll(scheduledMeetingRepository.getByDate(date));
        meetingsListAdapter.notifyDataSetChanged();
    }

    private void setUpRecyclerView(){
        meetingsListAdapter = new MeetingsListAdapter(meetings);
        viewBinding.activityMainMeetingRecyclerview.setAdapter(meetingsListAdapter);
        viewBinding.activityMainMeetingRecyclerview.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void onMeetingFilteredByRoom(MeetingRoomUniqueId roomUniqueId){
        Log.i("Filter_test", roomUniqueId.toString());
        meetings.clear();
        meetings.addAll(scheduledMeetingRepository.getByRoom(roomUniqueId));
        meetingsListAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter_by_date:
                datePickerFragment.show(getSupportFragmentManager(), "filter_date_picker");
                break;
            case R.id.reset_filter:
                resetMeetingList();
                break;
            case R.id.filter_by_room:
                showRoomSelectorDialog();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + item.getItemId());
        }
        return super.onOptionsItemSelected(item);
    }

    private void showRoomSelectorDialog(){
        List<MeetingRoomUniqueId> rooms = Arrays.asList(MeetingRoomUniqueId.values());
        String[] dialogItems = rooms.stream().map(MeetingRoomUniqueId::toString).toArray(String[]::new);
        new AlertDialog.Builder(this)
                .setTitle("Choisissez la salle")
                .setItems(dialogItems, (dialog, selectedItemIndex) -> {
                        dialog.dismiss();
                        final MeetingRoom selectedRoom =
                                DependencyContainer.getMeetingRoomRepository().getAll().get(selectedItemIndex);
                        onMeetingFilteredByRoom(selectedRoom.getUniqueId());
                }).show();
    }

    private void resetMeetingList(){
        meetings.clear();
        meetings.addAll(scheduledMeetingRepository.getAll());
        meetingsListAdapter.notifyDataSetChanged();
    }

    @Subscribe
    public void onDeleteMeetingEvent(DeleteMeetingEvent event){
        try {
            DependencyContainer.getMeetingScheduler().cancel(event.meetingToDelete);
            meetings.remove(event.meetingToDelete);
            meetingsListAdapter.notifyDataSetChanged();
        } catch (FreeTimeSlotReleaseAttempt freeTimeSlotReleaseAttempt) {
            Snackbar.make(viewBinding.getRoot(), R.string.meeting_deletion_failure, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @VisibleForTesting
    public void refreshMeetingListForTesting(){
        resetMeetingList();
    }
}