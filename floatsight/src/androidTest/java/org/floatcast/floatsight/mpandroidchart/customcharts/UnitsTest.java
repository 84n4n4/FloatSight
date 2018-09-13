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

package org.floatcast.floatsight.mpandroidchart.customcharts;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.FragmentTransaction;
import org.floatcast.floatsight.Actions;
import org.floatcast.floatsight.R;
import org.floatcast.floatsight.TrackActivity;
import org.floatcast.floatsight.csvparser.FlySightCsvParser;
import org.floatcast.floatsight.data.FlySightTrackData;
import org.floatcast.floatsight.datarepository.DataRepository;
import org.floatcast.floatsight.fragment.trackfragment.plot.PlotFragment;
import org.floatcast.floatsight.mpandroidchart.linedatasetcreation.ChartDataSetProperties;
import org.floatcast.floatsight.mpandroidchart.linedatasetcreation.TrackPointValueProvider;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.floatcast.floatsight.MainActivity.TAG_MAIN_MENU_FRAGMENT;
import static org.floatcast.floatsight.TrackActivity.TAG_PLOT_FRAGMENT;
import static org.floatcast.floatsight.mpandroidchart.linedatasetcreation.ChartDataSetHolder.*;
import static junit.framework.TestCase.assertEquals;

@RunWith(AndroidJUnit4.class)
public class UnitsTest {

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

        Uri testTrackUri = Uri.parse("android.resource://org.floatcast.floatsight.test/" + org.floatcast.floatsight.test.R.raw.sample_track);
        repository.load(testTrackUri, rule.getActivity().getFlySightTrackDataViewModel());
    }

    @Test
    public void testSwitchUnits() {
        switchXAxisToDistance();
        onView(isRoot()).perform(Actions.wait(500));

        assertMetric();

        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
        onView(withText(R.string.menu_item_units)).perform(click());

        onView(withId(R.id.checkbox_metric)).check(matches(isChecked()));
        onView(withId(R.id.checkbox_imperial)).check(matches(isNotChecked()));
        onView(withId(R.id.checkbox_imperial)).perform(click());
        onView(withId(R.id.checkbox_metric)).check(matches(isNotChecked()));
        onView(withId(R.id.checkbox_imperial)).check(matches(isChecked()));
        pressBack();

        assertImperial();
    }

    @Test
    public void testKeepUnitsOnRotation() {
        switchXAxisToDistance();
        onView(isRoot()).perform(Actions.wait(500));

        assertMetric();

        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
        onView(withText(R.string.menu_item_units)).perform(click());
        onView(withId(R.id.checkbox_imperial)).perform(click());

        pressBack();

        assertImperial();

        rule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        onView(isRoot()).perform(Actions.wait(500));

        assertImperial();
    }

    private void assertImperial() {
        List<ChartDataSetProperties> properties = getChart().getDataSetHolder().getDataSetPropertiesList();
        assertEquals(R.string.mph, properties.get(VERT_VELOCITY).unitStringResource);
        assertEquals(R.string.mph, properties.get(HOR_VELOCITY).unitStringResource);
        assertEquals(R.string.ft, properties.get(DISTANCE).unitStringResource);
        assertEquals(R.string.ft, properties.get(ALTITUDE).unitStringResource);

        assertEquals(TrackPointValueProvider.IMPERIAL_VERT_VELOCITY_VALUE_PROVIDER, properties.get(VERT_VELOCITY).yValueProvider);
        assertEquals(TrackPointValueProvider.IMPERIAL_HOR_VELOCITY_VALUE_PROVIDER, properties.get(HOR_VELOCITY).yValueProvider);
        assertEquals(TrackPointValueProvider.IMPERIAL_DISTANCE_VALUE_PROVIDER, properties.get(DISTANCE).yValueProvider);
        assertEquals(TrackPointValueProvider.IMPERIAL_ALTITUDE_VALUE_PROVIDER, properties.get(ALTITUDE).yValueProvider);

        assertEquals(99775.2f, getChart().getXAxis().getAxisMaximum());
        assertEquals(145.5264f, getChart().getAxisLeft().getAxisMaximum());
        assertEquals(15798.842f, getChart().getAxisRight().getAxisMaximum());
    }

    private void assertMetric() {
        List<ChartDataSetProperties> properties = getChart().getDataSetHolder().getDataSetPropertiesList();
        assertEquals(R.string.kmh, properties.get(VERT_VELOCITY).unitStringResource);
        assertEquals(R.string.kmh, properties.get(HOR_VELOCITY).unitStringResource);
        assertEquals(R.string.m, properties.get(DISTANCE).unitStringResource);
        assertEquals(R.string.m, properties.get(ALTITUDE).unitStringResource);

        assertEquals(TrackPointValueProvider.METRIC_VERT_VELOCITY_VALUE_PROVIDER, properties.get(VERT_VELOCITY).yValueProvider);
        assertEquals(TrackPointValueProvider.METRIC_HOR_VELOCITY_VALUE_PROVIDER, properties.get(HOR_VELOCITY).yValueProvider);
        assertEquals(TrackPointValueProvider.METRIC_DISTANCE_VALUE_PROVIDER, properties.get(DISTANCE).yValueProvider);
        assertEquals(TrackPointValueProvider.METRIC_ALTITUDE_VALUE_PROVIDER, properties.get(ALTITUDE).yValueProvider);

        assertEquals(30411.482f, getChart().getXAxis().getAxisMaximum());
        assertEquals(234.20212f, getChart().getAxisLeft().getAxisMaximum());
        assertEquals(4815.487f, getChart().getAxisRight().getAxisMaximum());
    }

    private void switchXAxisToDistance() {
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
        onView(withText(R.string.menu_item_x_axis)).perform(click());
        onView(withId(R.id.checkbox_distance)).perform(click());
        pressBack();
    }

    private GlideOverlayChart getChart() {
        PlotFragment plotFragment =
                (PlotFragment) rule.getActivity().getSupportFragmentManager()
                        .findFragmentByTag(TAG_PLOT_FRAGMENT);
        return plotFragment.getChart();
    }
}
