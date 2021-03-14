package com.sitatech.mareu.ui.meetings_list;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.sitatech.mareu.R;
import com.sitatech.mareu.databinding.ActivityMeetingListBinding;
import com.sitatech.mareu.events.DeleteMeetingEvent;
import com.sitatech.mareu.domain.models.Meeting;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MeetingsListActivity extends AppCompatActivity {

    private final List<Meeting> meetings = new ArrayList<>();
    private ActivityMeetingListBinding viewBinding;
    private MeetingsListAdapter meetingsListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivityMeetingListBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());
        setUpRecyclerView();
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
        // It's not necessary to check the item id because there is only one menu item.

        return super.onOptionsItemSelected(item);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDisplayMeetingEvent(Meeting meeting){

    }

    @Subscribe
    public void onDeleteMeetingEvent(DeleteMeetingEvent event){

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