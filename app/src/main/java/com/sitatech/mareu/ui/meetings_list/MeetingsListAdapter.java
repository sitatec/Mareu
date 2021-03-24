package com.sitatech.mareu.ui.meetings_list;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sitatech.mareu.databinding.MeetingRecyclerviewItemBinding;
import com.sitatech.mareu.events.DeleteMeetingEvent;
import com.sitatech.mareu.domain.models.Meeting;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class MeetingsListAdapter extends RecyclerView.Adapter<MeetingsListAdapter.ViewHolder> {

    private final List<Meeting> meetings;

    public MeetingsListAdapter(List<Meeting> meetings){
        this.meetings = meetings;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(MeetingRecyclerviewItemBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.updateView(meetings.get(position));
    }

    @Override
    public int getItemCount() {
        return meetings.size();
    }

    ///////////////////////////   INNER CLASSES   ////////////////////////////////

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final MeetingRecyclerviewItemBinding itemBinding;

        public ViewHolder(@NonNull MeetingRecyclerviewItemBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }

        public void updateView(Meeting currentMeeting){
            itemBinding.meetingTitle.setText(currentMeeting.getTitle());
            itemBinding.meetingSubtitle.setText(currentMeeting.getSubtitle());
            itemBinding.meetingColor.setCardBackgroundColor(currentMeeting.getColor());
            itemBinding.deleteMeeting.setOnClickListener(view -> emitMeetingDeletionEvent(currentMeeting));
        }

        private void emitMeetingDeletionEvent(Meeting meeting){
            EventBus.getDefault().post(new DeleteMeetingEvent(meeting));
        }

    }

}
