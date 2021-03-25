package com.sitatech.mareu.meetings_list;


import android.widget.DatePicker;

import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.sitatech.mareu.R;
import com.sitatech.mareu.domain.enums.MeetingRoomUniqueId;
import com.sitatech.mareu.domain.exceptions.TimeSlotOverlapException;
import com.sitatech.mareu.domain.models.Meeting;
import com.sitatech.mareu.domain.models.TimeSlot;
import com.sitatech.mareu.domain.repositories.ScheduledMeetingRepository;
import com.sitatech.mareu.domain.utils.MeetingScheduler;
import com.sitatech.mareu.ui.meetings_list.MeetingsListActivity;
import com.sitatech.mareu.ui.schedule_meeting.ScheduleMeetingActivity;
import com.sitatech.mareu.utils.DependencyContainer;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openContextualActionModeOverflowMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.sitatech.mareu.test_utils.DeleteMeetingAction.delete;
import static com.sitatech.mareu.test_utils.RecyclerViewItemCountAssertion.isEmpty;
import static com.sitatech.mareu.test_utils.RecyclerViewItemCountAssertion.withItemCount;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class MeetingListActivityTest {

    private final MeetingScheduler meetingScheduler = DependencyContainer.getMeetingScheduler();;
    private final ScheduledMeetingRepository scheduledMeetingRepository = DependencyContainer.getScheduledMeetingRepository();

    @Rule
    public final ActivityScenarioRule<MeetingsListActivity> activityScenarioRule = new ActivityScenarioRule<>(MeetingsListActivity.class);

    @After
    public void reset(){
        scheduledMeetingRepository.clear();
        DependencyContainer.getMeetingRoomRepository().reset();
    }

    @Test
    public void the_recyclerview_should_be_empty(){
        onView(withId(R.id.activity_main_meeting_recyclerview)).check(isEmpty());
    }

    @Test
    public void should_contains_all_scheduled_meetings() throws TimeSlotOverlapException {
        onView(withId(R.id.activity_main_meeting_recyclerview)).check(isEmpty());
        scheduleMeetings();
        final int numberOfScheduledMeetings = scheduledMeetingRepository.getAll().size();
        refreshActivity();
        onView(withId(R.id.activity_main_meeting_recyclerview))
                .check(withItemCount(numberOfScheduledMeetings));
    }

    @Test
    public void should_start_ScheduleMeetingActivity(){
        Intents.init();
        onView(withId(R.id.schedule_meeting_button)).perform(click());
        intended(hasComponent(ScheduleMeetingActivity.class.getName()));
        Intents.release();
        onView(withId(R.id.schedule_meeting_activity_views_root)).check(matches(isDisplayed()));
    }

    @Test
    public void should_remove_meeting() throws TimeSlotOverlapException {
        scheduleMeetings();
        final int numberOfScheduledMeetings = scheduledMeetingRepository.getAll().size();
        refreshActivity();
        onView(withId(R.id.activity_main_meeting_recyclerview))
                .check(withItemCount(numberOfScheduledMeetings));
        // remove one item
        onView(withId(R.id.activity_main_meeting_recyclerview))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, delete()));
        onView(withId(R.id.activity_main_meeting_recyclerview))
                .check(withItemCount(numberOfScheduledMeetings - 1));
    }

    @Test
    public void should_filter_meetings_by_date() throws TimeSlotOverlapException, InterruptedException {
        final LocalDate currentDate = LocalDate.now();
        scheduleMeetings();
        refreshActivity();
        onView(withId(R.id.activity_main_meeting_recyclerview))
                .check(withItemCount(scheduledMeetingRepository.getAll().size()));
        openContextualActionModeOverflowMenu();
        Thread.sleep(1000);
        onView(withText(R.string.menu_action_filter_by_date_txt)).perform(click()); // click on the filter by date button (in the toolbar menu)
        Thread.sleep(1000);
        onView(withClassName(equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(currentDate.getYear(), currentDate.getMonthValue(), currentDate.getDayOfMonth()));
        Thread.sleep(1000);
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.activity_main_meeting_recyclerview))
                .check(withItemCount(scheduledMeetingRepository.getByDate(currentDate).size()));
    }

    @Test
    public void should_filter_meetings_by_room() throws TimeSlotOverlapException, InterruptedException {
        scheduleMeetings();
        refreshActivity();
        onView(withId(R.id.activity_main_meeting_recyclerview))
                .check(withItemCount(scheduledMeetingRepository.getAll().size()));
        openContextualActionModeOverflowMenu();
        Thread.sleep(1000);
        onView(withText(R.string.menu_action_filter_by_room_txt)).perform(click()); // click on the filter by room button (in the toolbar menu)
        Thread.sleep(1000);
        onData(allOf(is(instanceOf(String.class)), is(MeetingRoomUniqueId.A.toString()))).perform(click());
        onView(withId(R.id.activity_main_meeting_recyclerview))
                .check(withItemCount(scheduledMeetingRepository.getByRoom(MeetingRoomUniqueId.A).size()));
    }


    ////////////////////// ::::::::: Utils methods :::::::::: ////////////////////////

    private void scheduleMeetings() throws TimeSlotOverlapException {
        final LocalDateTime currentDateTime = LocalDateTime.now();
        final LocalDateTime dateTime = currentDateTime.minusHours(2);// The same date with `currentDateTime` & `datetime3`
        final LocalDateTime dateTime1 = currentDateTime.minusDays(1);
        final LocalDateTime dateTime2 = currentDateTime.plusDays(2);
        final LocalDateTime dateTime3 = currentDateTime.minusHours(4);// The same date with `currentDateTime` & `datetime`

        final Set<String> participants = new HashSet<>(Arrays.asList("test@test.ts", "espresso@test.ts"));

        final Meeting meeting = new Meeting(participants, new TimeSlot(currentDateTime), MeetingRoomUniqueId.A, "subject");
        final Meeting meeting1 = new Meeting(participants, new TimeSlot(dateTime), MeetingRoomUniqueId.A, "subject");
        final Meeting meeting2 = new Meeting(participants, new TimeSlot(dateTime1), MeetingRoomUniqueId.A, "subject");
        final Meeting meeting3 = new Meeting(participants, new TimeSlot(dateTime2), MeetingRoomUniqueId.D, "sub");
        final Meeting meeting4 = new Meeting(participants, new TimeSlot(dateTime3), MeetingRoomUniqueId.D, "sub");

        meetingScheduler.schedule(meeting);
        meetingScheduler.schedule(meeting1);
        meetingScheduler.schedule(meeting2);
        meetingScheduler.schedule(meeting3);
        meetingScheduler.schedule(meeting4);
    }

    private void refreshActivity(){
        activityScenarioRule.getScenario().onActivity(MeetingsListActivity::refreshMeetingListForTesting);
    }
}
