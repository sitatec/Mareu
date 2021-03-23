package com.sitatech.mareu.test_utils;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;

import org.hamcrest.Matcher;
import org.junit.Assert;

import static org.hamcrest.Matchers.is;

public class RecyclerViewItemCountAssertion implements ViewAssertion {

    private final Matcher<Integer> matcher;

    public RecyclerViewItemCountAssertion(Matcher<Integer> matcher){
        this.matcher = matcher;
    }

    public static RecyclerViewItemCountAssertion withItemCount(int itemCount){
        return new RecyclerViewItemCountAssertion(is(itemCount));
    }

    public static RecyclerViewItemCountAssertion isEmpty(){
        return withItemCount(0);
    }

    @Override
    public void check(View view, NoMatchingViewException noViewFoundException) {
        if(noViewFoundException != null) throw  noViewFoundException;
        final RecyclerView.Adapter adapter = ((RecyclerView)view).getAdapter();
        Assert.assertThat(adapter.getItemCount(), matcher);
    }
}
