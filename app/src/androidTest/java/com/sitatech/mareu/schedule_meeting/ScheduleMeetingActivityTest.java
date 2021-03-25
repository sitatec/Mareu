package com.sitatech.mareu.schedule_meeting;

import android.content.res.Resources;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.sitatech.mareu.R;
import com.sitatech.mareu.domain.enums.MeetingRoomUniqueId;
import com.sitatech.mareu.ui.schedule_meeting.ScheduleMeetingActivity;
import com.sitatech.mareu.utils.DependencyContainer;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class ScheduleMeetingActivityTest {

    private static Resources resources;

    @Rule
    public final ActivityScenarioRule<ScheduleMeetingActivity> activityScenarioRule =
            new ActivityScenarioRule<>(ScheduleMeetingActivity.class);

    @BeforeClass
    public static void setUp(){
        resources = ApplicationProvider.getApplicationContext().getResources();
    }

    @After
    public void reset(){
        DependencyContainer.getScheduledMeetingRepository().clear();
        DependencyContainer.getMeetingRoomRepository().reset();
    }

    @Test
    public void ScheduleMeetingActivity_should_be_displayed(){
        onView(withId(R.id.schedule_meeting_activity_views_root)).check(matches(isDisplayed()));
    }

    @Test
    public void when_submitting_the_form_required_fields_that_are_empty_should_have_error_indicator(){
        final String fieldsErrorMsg = resources.getString(R.string.required_field_error_msg);
        final String participantEmailFieldErrorMsg = resources.getString(R.string.participant_emails_field_error_msg);
        // Check that error indicators are not displayed
        onView(withId(R.id.meeting_room_error_hint)).check(matches(not(isDisplayed())));
        onView(withId(R.id.date_picker_action)).check(matches(not(withHint(fieldsErrorMsg))));
        onView(withId(R.id.time_picker_action)).check(matches(not(withHint(fieldsErrorMsg))));
        onView(withId(R.id.duration_picker_action)).check(matches(not(withHint(fieldsErrorMsg))));
        onView(withId(R.id.meeting_subject_edit)).check(matches(not(hasErrorText(fieldsErrorMsg))));
        onView(withId(R.id.participant_email_edit)).check(matches(not(hasErrorText(participantEmailFieldErrorMsg))));
        // Try to submit the form
        onView(withId(R.id.submit)).perform(click());
        // Check that error indicators are displayed for pickers
        onView(withId(R.id.meeting_room_error_hint)).check(matches(isDisplayed()));
        onView(withId(R.id.date_picker_action)).check(matches(withHint(fieldsErrorMsg)));
        onView(withId(R.id.time_picker_action)).check(matches(withHint(fieldsErrorMsg)));
        onView(withId(R.id.duration_picker_action)).check(matches(withHint(fieldsErrorMsg)));
        // Check that error indicators are displayed for normal EditTexts
        onView(withId(R.id.meeting_subject_edit)).check(matches(hasErrorText(fieldsErrorMsg)));
        onView(withId(R.id.meeting_subject_edit)).check(matches(withHint(fieldsErrorMsg)));
        onView(withId(R.id.participant_email_edit)).check(matches(hasErrorText(participantEmailFieldErrorMsg)));
        onView(withId(R.id.participant_email_edit)).check(matches(withHint(participantEmailFieldErrorMsg)));
    }

//    @Test
//    public void error_hint_should_be_visible_when_the_meeting_room_spinner_is_closed_without_choosing_a_room(){
//        onView(withId(R.id.meeting_room_error_hint)).check(matches(not(isDisplayed())));
//
//        // open de dropdown
//        onView(withId(R.id.meeting_room_selector)).perform(click());
//        // click on an other view to close the spinner without selecting a item
//        onView(withId(R.id.participant_email_edit)).perform(click());
//
//        onView(withId(R.id.meeting_room_error_hint)).check(matches(isDisplayed()));
//    }


    @Test
    public void participant_email_edit_text_should_have_an_error_hint_if_the_participants_are_less_than_2(){
        final String participantEmailFieldErrorMsg = resources.getString(R.string.participant_emails_field_error_msg);
        // add a participant
        onView(withId(R.id.participant_email_edit)).perform(typeText("espresso@test.ts"), closeSoftKeyboard());
        onView(withId(R.id.add_participant_button)).perform(click());
        onView(withId(R.id.submit)).perform(click());
        onView(withId(R.id.participant_email_edit)).check(matches(hasErrorText(participantEmailFieldErrorMsg)));
    }

    @Test
    public void added_participant_emails_should_be_visible_in_the_appropriate_TextView(){
        final String firstEmail = "espresso@test.ts";
        final String secondParticipants = "espresso_2_@test.ts";
        final String addedParticipantEmailsContainerText = resources.getString(R.string.added_participant_emails);

        onView(withId(R.id.added_participant_emails_container)).check(matches(withText(addedParticipantEmailsContainerText)));
        // add a participant
        onView(withId(R.id.participant_email_edit)).perform(typeText(firstEmail));
        onView(withId(R.id.add_participant_button)).perform(click());
        onView(withId(R.id.added_participant_emails_container))
                .check(matches(withText(addedParticipantEmailsContainerText + firstEmail)));
        // add an other participant
        onView(withId(R.id.participant_email_edit)).perform(typeText(secondParticipants));
        onView(withId(R.id.add_participant_button)).perform(click());
        onView(withId(R.id.added_participant_emails_container))
                .check(matches(withText(addedParticipantEmailsContainerText + firstEmail + ", " + secondParticipants)));
    }

    @Test
    public void should_schedule_a_meeting(){
        assertThat(DependencyContainer.getScheduledMeetingRepository().getAll(), empty());
        fillFormFields();
        onView(withId(R.id.submit)).perform(click());
        assertThat(DependencyContainer.getScheduledMeetingRepository().getAll().size(), equalTo(1));
    }

    @Test
    public void should_show_success_message_in_a_snack_bar(){
        final String message = resources.getString(R.string.meeting_scheduled_successfully_msg);
        fillFormFields();
        onView(withId(R.id.submit)).perform(click());
        onView(allOf(withId(com.google.android.material.R.id.snackbar_text), withText(message)))
                .check(matches(isDisplayed()));
    }

    @Test
    public void should_show_time_slot_overlap_message_in_a_snack_bar(){
        final String message = resources.getString(R.string.time_slot_overlaps_msg);
        fillFormFields();
        onView(withId(R.id.submit)).perform(click());
        onView(withId(R.id.submit)).perform(click());// trying to schedule existing meeting.
        onView(allOf(withId(com.google.android.material.R.id.snackbar_text), withText(message)))
                .check(matches(isDisplayed()));
    }

    private void fillFormFields(){
        onView(withId(R.id.meeting_room_selector)).perform(click());
        onView(withText(MeetingRoomUniqueId.A.toString())).perform(click());
        // select the default values of the date, time and color pickers
        onView(withId(R.id.date_picker_action)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.time_picker_action)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.color_picker_action)).perform(click());
        onView(withText(R.string.choose_color)).perform(click());
        // select the duration
        onView(withId(R.id.duration_picker_action)).perform(click());
        onView(withId(R.id.minutes_picker)).perform(swipeUp());
        onView(withId(android.R.id.button1)).perform(click());
        // type meeting subject
        onView(withId(R.id.meeting_subject_edit)).perform(typeText("Subject"), closeSoftKeyboard());
        // add a participant
        onView(withId(R.id.participant_email_edit)).perform(typeText("firstEmail"));
        onView(withId(R.id.add_participant_button)).perform(click());
        // add an other participant because a meeting require at least two participants.
        onView(withId(R.id.participant_email_edit))
                .perform(typeText("secondParticipants"), closeSoftKeyboard());
        onView(withId(R.id.add_participant_button)).perform(click());
    }

}
