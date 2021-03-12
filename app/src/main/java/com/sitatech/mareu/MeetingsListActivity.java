package com.sitatech.mareu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.sitatech.mareu.databinding.ActivityMeetingListBinding;

public class MeetingsListActivity extends AppCompatActivity {

    private ActivityMeetingListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMeetingListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}