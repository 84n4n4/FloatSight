package com.watchthybridle.floatsight.customcharts;

import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.net.Uri;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.FragmentTransaction;
import com.watchthybridle.floatsight.Actions;
import com.watchthybridle.floatsight.MainActivity;
import com.watchthybridle.floatsight.R;
import com.watchthybridle.floatsight.chartfragments.PlotFragment;
import com.watchthybridle.floatsight.customcharts.markerviews.TouchAbleMarkerView;
import com.watchthybridle.floatsight.viewmodel.FlySightTrackDataRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.watchthybridle.floatsight.MainActivity.TAG_ALL_METRICS_V_TIME_CHART_FRAGMENT;
import static com.watchthybridle.floatsight.MainActivity.TAG_MAIN_MENU_FRAGMENT;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(AndroidJUnit4.class)
public class RangeMarkerViewTest {

    private PlotFragment plotFragment;

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() {
        plotFragment = new PlotFragment();
        FragmentTransaction transaction = rule.getActivity().getSupportFragmentManager().beginTransaction();
        transaction
                .replace(R.id.fragment_container, plotFragment,
                        TAG_ALL_METRICS_V_TIME_CHART_FRAGMENT)
                .addToBackStack(TAG_MAIN_MENU_FRAGMENT)
                .commit();
        rule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        FlySightTrackDataRepository repository = new FlySightTrackDataRepository(rule.getActivity().getContentResolver());
        Uri testTrackUri = Uri.parse("android.resource://com.watchthybridle.floatsight.test/" + com.watchthybridle.floatsight.test.R.raw.sample_track);
        repository.loadFlySightTrackData(testTrackUri, rule.getActivity().getFlySightTrackDataViewModel());
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
                        .findFragmentByTag(MainActivity.TAG_ALL_METRICS_V_TIME_CHART_FRAGMENT);
        return plotFragment.getChart();
    }
}
