package com.sitatech.mareu.domain.repositories;

import com.sitatech.mareu.domain.enums.MeetingRoomUniqueId;
import com.sitatech.mareu.domain.models.MeetingRoom;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MeetingRoomRepository {

    private final Map<MeetingRoomUniqueId, MeetingRoom> meetingRooms;

    public MeetingRoomRepository(){
        meetingRooms = new TreeMap<>();
        initializeMeetingRooms();
    }

    private void initializeMeetingRooms(){
        for (MeetingRoomUniqueId id : MeetingRoomUniqueId.values()){
            meetingRooms.put(id, new MeetingRoom(id));
        }
    }

    public MeetingRoom get(MeetingRoomUniqueId roomUniqueId){
        return meetingRooms.get(roomUniqueId);
    }

    public List<MeetingRoom> getAll(){
        return new ArrayList<>(meetingRooms.values());
    }
}
