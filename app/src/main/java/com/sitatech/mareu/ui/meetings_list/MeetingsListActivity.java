package com.sitatech.mareu.ui.meetings_list;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.sitatech.mareu.R;
import com.sitatech.mareu.databinding.ActivityMeetingListBinding;
import com.sitatech.mareu.domain.utils.MeetingScheduler;
import com.sitatech.mareu.events.DeleteMeetingEvent;
import com.sitatech.mareu.domain.models.Meeting;
import com.sitatech.mareu.ui.schedule_meeting.ScheduleMeetingActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class MeetingsListActivity extends AppCompatActivity {

    private final MeetingScheduler meetingScheduler = MeetingScheduler.getInstance();
    private List<Meeting> meetings = meetingScheduler.getAllScheduledMeetings();
    private ActivityMeetingListBinding viewBinding;
    private MeetingsListAdapter meetingsListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivityMeetingListBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());
        setUpRecyclerView();
        viewBinding.scheduleMeetingButton.setOnClickListener(this::startScheduleMeetingActivity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        meetingsListAdapter.notifyDataSetChanged();
    }

    private void startScheduleMeetingActivity(View button){
        startActivity(new Intent(this, ScheduleMeetingActivity.class));
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // There are only one action so it's not necessary to check the id.
        showFiltersDialog();
        return super.onOptionsItemSelected(item);
    }

    public void showFiltersDialog() {

    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onDisplayMeetingEvent(Meeting meeting){ }

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