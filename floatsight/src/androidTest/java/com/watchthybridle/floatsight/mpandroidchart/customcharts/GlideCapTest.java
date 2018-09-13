/*
 *
 *     FloatSight
 *     Copyright 2018 Thomas Hirsch
 *     https://github.com/84n4n4/FloatSight
 *
 *     This file is part of FloatSight.
 *     FloatSight is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     FloatSight is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with FloatSight.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package com.watchthybridle.floatsight.mpandroidchart.customcharts;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.SeekBar;
import com.watchthybridle.floatsight.Actions;
import com.watchthybridle.floatsight.R;
import com.watchthybridle.floatsight.TrackActivity;
import com.watchthybridle.floatsight.csvparser.FlySightCsvParser;
import com.watchthybridle.floatsight.data.FlySightTrackData;
import com.watchthybridle.floatsight.datarepository.DataRepository;
import com.watchthybridle.floatsight.fragment.trackfragment.plot.PlotFragment;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static com.watchthybridle.floatsight.MainActivity.TAG_MAIN_MENU_FRAGMENT;
import static com.watchthybridle.floatsight.TrackActivity.TAG_PLOT_FRAGMENT;
import static junit.framework.TestCase.assertEquals;

@RunWith(AndroidJUnit4.class)
public class GlideCapTest {

    private PlotFragment plotFragment;

    @Rule
    public ActivityTestRule<TrackActivity> rule = new ActivityTestRule<>(TrackActivity.class);

    @Before
    public void setUp() {
        plotFragment = new PlotFragment();
        FragmentTransaction transaction = rule.getActivity().getSupportFragmentManager().beginTransaction();
        transaction
                .replace(R.id.fragment_container, plotFragment,
                        TAG_PLOT_FRAGMENT)
                .addToBackStack(TAG_MAIN_MENU_FRAGMENT)
                .commit();
        rule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        DataRepository<FlySightTrackData> repository = new DataRepository<>(
                FlySightTrackData.class, rule.getActivity().getContentResolver(), new FlySightCsvParser());

        Uri testTrackUri = Uri.parse("android.resource://com.watchthybridle.floatsight.test/" + com.watchthybridle.floatsight.test.R.raw.sample_track);
        repository.load(testTrackUri, rule.getActivity().getFlySightTrackDataViewModel());
    }

    @Test
    public void testGlideCap() {
        onView(isRoot()).perform(Actions.wait(500));

        assertEquals(5.5f, getChart().glideChart.getAxisLeft().getAxisMaximum());

        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
        onView(withText(R.string.menu_item_cap_glide)).perform(click());
        onView(withId(R.id.seekbar_glide_cap)).check(matches(hasProgress(5)));
        onView(withId(R.id.seekbar_glide_cap)).perform(click());

        assertEquals(8.8f, getChart().glideChart.getAxisLeft().getAxisMaximum());

        pressBack();
    }

    @Test
    public void testKeepUnitsOnRotation() {
        onView(isRoot()).perform(Actions.wait(500));

        assertEquals(5.5f, getChart().glideChart.getAxisLeft().getAxisMaximum());

        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
        onView(withText(R.string.menu_item_cap_glide)).perform(click());
        onView(withId(R.id.seekbar_glide_cap)).check(matches(hasProgress(5)));
        onView(withId(R.id.seekbar_glide_cap)).perform(click());

        assertEquals(8.8f, getChart().glideChart.getAxisLeft().getAxisMaximum());

        pressBack();

        rule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        onView(isRoot()).perform(Actions.wait(500));

        assertEquals(8.8f, getChart().glideChart.getAxisLeft().getAxisMaximum());
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
        onView(withText(R.string.menu_item_cap_glide)).perform(click());
        onView(withId(R.id.seekbar_glide_cap)).check(matches(hasProgress(8)));
    }

    public static Matcher<View> hasProgress(final int progress) {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                return view instanceof SeekBar
                        && ((SeekBar) view).getProgress() == progress;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("seekbar has progress of " + String.valueOf(progress));
            }
        };
    }

    private GlideOverlayChart getChart() {
        PlotFragment plotFragment =
                (PlotFragment) rule.getActivity().getSupportFragmentManager()
                        .findFragmentByTag(TAG_PLOT_FRAGMENT);
        return plotFragment.getChart();
    }
}
