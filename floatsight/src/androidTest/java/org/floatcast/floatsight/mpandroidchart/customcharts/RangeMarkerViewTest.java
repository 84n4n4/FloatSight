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
import android.graphics.Rect;
import android.net.Uri;
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
import org.floatcast.floatsight.mpandroidchart.customcharts.markerviews.TouchAbleMarkerView;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static junit.framework.TestCase.assertTrue;
import static org.floatcast.floatsight.MainActivity.TAG_MAIN_MENU_FRAGMENT;
import static org.floatcast.floatsight.TrackActivity.TAG_PLOT_FRAGMENT;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertFalse;

@RunWith(AndroidJUnit4.class)
public class RangeMarkerViewTest {

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
    public void testRangeShown() {
        onView(isRoot()).perform(Actions.wait(500));

        setRangeMarkerAt(getChart().getRight() / 4);
        setRangeMarkerAt((getChart().getRight() / 4) * 3);

        assertTrue(getChart().isRangeVisible());
    }

    @Test
    public void testRangeDismiss() {
        onView(isRoot()).perform(Actions.wait(500));

        setRangeMarkerAt(getChart().getRight() / 4);
        setRangeMarkerAt((getChart().getRight() / 4) * 3);

        assertTrue(getChart().isRangeVisible());

        Rect markerArea = getChart().getRangeMarkerView().getMarkerViewDrawArea();
        onView(withId(R.id.root_chart_view))
                .perform(Actions.clickOnPosition(markerArea.centerX(), markerArea.centerY()));

        assertFalse(getChart().isRangeVisible());
    }

    @Test
    public void testKeepRangeOnOrientationChange() {
        onView(isRoot()).perform(Actions.wait(500));

        setRangeMarkerAt(getChart().getRight() / 4);
        setRangeMarkerAt((getChart().getRight() / 4) * 3);

        assertTrue(getChart().isRangeVisible());

        rule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        onView(isRoot()).perform(Actions.wait(500));

        assertTrue(getChart().isRangeVisible());
    }

    @Test
    public void testRangeStaysDismissedAfterRotation() {
        onView(isRoot()).perform(Actions.wait(500));

        setRangeMarkerAt(getChart().getRight() / 4);
        setRangeMarkerAt((getChart().getRight() / 4) * 3);

        assertTrue(getChart().isRangeVisible());

        Rect markerArea = getChart().getRangeMarkerView().getMarkerViewDrawArea();
        onView(withId(R.id.root_chart_view))
                .perform(Actions.clickOnPosition(markerArea.centerX(), markerArea.centerY()));

        assertFalse(getChart().isRangeVisible());

        rule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        onView(isRoot()).perform(Actions.wait(500));

        assertFalse(getChart().isRangeVisible());
    }

    @Test
    public void testMenuIconsEnabledOrientationChange() {
        onView(isRoot()).perform(Actions.wait(500));

        onView(withId(R.id.menu_item_set_range_marker)).check(matches(not(isEnabled())));
        onView(withId(R.id.menu_item_crop_range)).check(matches(not(isEnabled())));
        onView(withId(R.id.menu_item_discard_markers)).check(matches(not(isEnabled())));

        setRangeMarkerAt(getChart().getRight() / 4);
        onView(withId(R.id.menu_item_set_range_marker)).check(matches(isEnabled()));
        onView(withId(R.id.menu_item_crop_range)).check(matches(not(isEnabled())));
        onView(withId(R.id.menu_item_discard_markers)).check(matches(isEnabled()));

        setRangeMarkerAt((getChart().getRight() / 4) * 3);
        onView(withId(R.id.menu_item_set_range_marker)).check(matches(isEnabled()));
        onView(withId(R.id.menu_item_crop_range)).check(matches(isEnabled()));
        onView(withId(R.id.menu_item_discard_markers)).check(matches(isEnabled()));

        assertTrue(getChart().isRangeVisible());

        rule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        onView(isRoot()).perform(Actions.wait(500));

        assertTrue(getChart().isRangeVisible());
        onView(withId(R.id.menu_item_set_range_marker)).check(matches(isEnabled()));
        onView(withId(R.id.menu_item_crop_range)).check(matches(isEnabled()));
        onView(withId(R.id.menu_item_discard_markers)).check(matches(isEnabled()));
    }

    @Test
    public void testMenuIconsEnabledDismissedByMarkerClick() {
        onView(isRoot()).perform(Actions.wait(500));

        onView(withId(R.id.menu_item_set_range_marker)).check(matches(not(isEnabled())));
        onView(withId(R.id.menu_item_crop_range)).check(matches(not(isEnabled())));
        onView(withId(R.id.menu_item_discard_markers)).check(matches(not(isEnabled())));

        setRangeMarkerAt(getChart().getRight() / 4);
        onView(withId(R.id.menu_item_set_range_marker)).check(matches(isEnabled()));
        onView(withId(R.id.menu_item_crop_range)).check(matches(not(isEnabled())));
        onView(withId(R.id.menu_item_discard_markers)).check(matches(isEnabled()));

        setRangeMarkerAt((getChart().getRight() / 4) * 3);
        onView(withId(R.id.menu_item_set_range_marker)).check(matches(isEnabled()));
        onView(withId(R.id.menu_item_crop_range)).check(matches(isEnabled()));
        onView(withId(R.id.menu_item_discard_markers)).check(matches(isEnabled()));

        assertTrue(getChart().isRangeVisible());

        Rect markerArea = getChart().getRangeMarkerView().getMarkerViewDrawArea();
        onView(withId(R.id.root_chart_view))
                .perform(Actions.clickOnPosition(markerArea.centerX(), markerArea.centerY()));

        assertFalse(getChart().isRangeVisible());
        onView(withId(R.id.menu_item_set_range_marker)).check(matches(isEnabled()));
        onView(withId(R.id.menu_item_crop_range)).check(matches(not(isEnabled())));
        onView(withId(R.id.menu_item_discard_markers)).check(matches(isEnabled()));
    }

    @Test
    public void testMenuIconsEnabledDismissedByMenuClick() {
        onView(isRoot()).perform(Actions.wait(500));

        onView(withId(R.id.menu_item_set_range_marker)).check(matches(not(isEnabled())));
        onView(withId(R.id.menu_item_crop_range)).check(matches(not(isEnabled())));
        onView(withId(R.id.menu_item_discard_markers)).check(matches(not(isEnabled())));

        setRangeMarkerAt(getChart().getRight() / 4);
        onView(withId(R.id.menu_item_set_range_marker)).check(matches(isEnabled()));
        onView(withId(R.id.menu_item_crop_range)).check(matches(not(isEnabled())));
        onView(withId(R.id.menu_item_discard_markers)).check(matches(isEnabled()));

        setRangeMarkerAt((getChart().getRight() / 4) * 3);
        onView(withId(R.id.menu_item_set_range_marker)).check(matches(isEnabled()));
        onView(withId(R.id.menu_item_crop_range)).check(matches(isEnabled()));
        onView(withId(R.id.menu_item_discard_markers)).check(matches(isEnabled()));

        assertTrue(getChart().isRangeVisible());

        onView(withId(R.id.menu_item_discard_markers))
                .perform(click());

        assertFalse(getChart().isRangeVisible());
        onView(withId(R.id.menu_item_set_range_marker)).check(matches(not(isEnabled())));
        onView(withId(R.id.menu_item_crop_range)).check(matches(not(isEnabled())));
        onView(withId(R.id.menu_item_discard_markers)).check(matches(not(isEnabled())));
    }

    private void setRangeMarkerAt(int xPosition) {
        onView(withId(R.id.root_chart_view))
                .perform(Actions.clickOnPosition(xPosition, getChart().getBottom() / 2));

        Rect markerArea = ((TouchAbleMarkerView) getChart().getMarker()).getMarkerViewDrawArea();
        onView(withId(R.id.root_chart_view))
                .perform(Actions.longClickOnPosition(markerArea.centerX(), markerArea.centerY()));
    }

    private GlideOverlayChart getChart() {
        PlotFragment plotFragment =
                (PlotFragment) rule.getActivity().getSupportFragmentManager()
                        .findFragmentByTag(TAG_PLOT_FRAGMENT);
        return plotFragment.getChart();
    }
}
