package com.sitatech.mareu.domain.enums;

import androidx.annotation.NonNull;

public enum MeetingRoomUniqueId {
    A,
    B,
    C,
    D,
    E,
    F,
    G,
    H,
    I,
    J;

    @NonNull
    @Override
    public String toString() {
        return "Réunion " + this.name();
    }
}
