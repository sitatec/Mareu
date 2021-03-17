package com.sitatech.mareu.ui.meetings_list;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Spinner;

import com.sitatech.mareu.R;
import com.sitatech.mareu.databinding.ActivityMeetingListBinding;
import com.sitatech.mareu.domain.enums.MeetingRoomUniqueId;
import com.sitatech.mareu.domain.utils.MeetingScheduler;
import com.sitatech.mareu.events.DeleteMeetingEvent;
import com.sitatech.mareu.domain.models.Meeting;
import com.sitatech.mareu.ui.schedule_meeting.ScheduleMeetingActivity;
import com.sitatech.mareu.ui.schedule_meeting.fragments.pickers.DatePickerFragment;
import com.sitatech.mareu.ui.utils.MeetingRoomSpinnerHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MeetingsListActivity extends AppCompatActivity {

    private final MeetingScheduler meetingScheduler = MeetingScheduler.getInstance();
    private final List<Meeting> meetings = new ArrayList<>(meetingScheduler.getAllScheduledMeetings());
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
        final LocalDate date = LocalDate.of(year, month, day);
        meetings.clear();
        meetings.addAll(meetingScheduler.getScheduledMeetingsByDate(date));
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
        final Spinner roomSelector = (Spinner) menu.findItem(R.id.filter_by_room).getActionView();
        MeetingRoomSpinnerHelper.setUp(this, roomSelector, this::onMeetingFilteredByRoom);
        return super.onCreateOptionsMenu(menu);
    }

    private void onMeetingFilteredByRoom(MeetingRoomUniqueId roomUniqueId){
        meetings.clear();
        meetings.addAll(meetingScheduler.getScheduledMeetingsByRoom(roomUniqueId));
        meetingsListAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.filter_by_date) {
            datePickerFragment.show(getSupportFragmentManager(), "filter_date_picker");
        }else if(item.getItemId() == R.id.reset_filter){
            meetings.clear();
            meetings.addAll(meetingScheduler.getAllScheduledMeetings());
            meetingsListAdapter.notifyDataSetChanged();
        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void onDeleteMeetingEvent(DeleteMeetingEvent event){
        meetings.remove(event.meetingToDelete);
        meetingsListAdapter.notifyDataSetChanged();
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
}