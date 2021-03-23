package com.sitatech.mareu.test_utils;

import android.view.View;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;

import com.sitatech.mareu.R;

import org.hamcrest.Matcher;

public class DeleteMeetingAction {
    public static ViewAction delete() {
        return new ViewAction() {
            @Override public Matcher<View> getConstraints() { return null; }

            @Override public String getDescription() { return "null"; }

            @Override
            public void perform(UiController uiController, View view) {
                view.findViewById(R.id.delete_meeting).performClick();
            }
        };
    }
}
