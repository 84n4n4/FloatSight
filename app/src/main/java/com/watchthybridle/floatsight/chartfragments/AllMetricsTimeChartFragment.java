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

package com.watchthybridle.floatsight.chartfragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import com.watchthybridle.floatsight.BuildConfig;
import com.watchthybridle.floatsight.R;
import com.watchthybridle.floatsight.csvparser.FlySightTrackData;
import com.watchthybridle.floatsight.customcharts.GlideOverlayChart;
import com.watchthybridle.floatsight.linedatasetcreation.AllMetricsVsTimeChartDataSetHolder;

public class AllMetricsTimeChartFragment extends ChartFragment {

    private GlideOverlayChart chart;

    public AllMetricsTimeChartFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FrameLayout frameLayout = view.findViewById(R.id.root_chart_view);

        chart = new GlideOverlayChart(getContext());
        chart.invalidate();

        frameLayout.addView(chart.glideChart);
        frameLayout.addView(chart);
    }

    public void actOnDataChanged(FlySightTrackData flySightTrackData) {
        if (isInvalid(flySightTrackData)) {
            return;
        }

        AllMetricsVsTimeChartDataSetHolder chartDataSetHolder = new AllMetricsVsTimeChartDataSetHolder(getContext(), flySightTrackData);

        chart.setDataHolder(chartDataSetHolder);
        chart.invalidate();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_all_metrics_time_chart, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_set_range_marker:
                chart.setRangeMarker();
                return true;
            case R.id.menu_item_clear_range_marker:
                chart.clearRangeMarkers();
                return true;
            case R.id.menu_item_show_range_data:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
