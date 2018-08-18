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

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.watchthybridle.floatsight.R;
import com.watchthybridle.floatsight.csvparser.FlySightTrackData;
import com.watchthybridle.floatsight.linedatasetcreation.TrackLineDataSetWrapper;

import static com.github.mikephil.charting.components.YAxis.YAxisLabelPosition.INSIDE_CHART;

public class DistanceAltitudeChartFragment extends ChartFragment {

    private LineChart chart;

    public DistanceAltitudeChartFragment() {
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FrameLayout frameLayout = view.findViewById(R.id.root_chart_view);

        setUpChart();

        frameLayout.addView(chart);
    }

    private void setUpChart() {
        chart = new LineChart(getContext());
        chart.getAxisLeft().setPosition(INSIDE_CHART);
        chart.getAxisRight().setPosition(INSIDE_CHART);
        chart.getDescription().setText("");
        chart.invalidate();
        chart.setPinchZoom(false);
    }

    protected void showData(FlySightTrackData flySightTrackData) {
        if (isValidDataAndShowAlerts(flySightTrackData) == DATA_INVALID) {
            return;
        }

        chart.setData(new LineData(TrackLineDataSetWrapper.getLineDataSet(getContext(),
                TrackLineDataSetWrapper.DISTANCE_VS_ALTITUDE,
                flySightTrackData)));

        chart.invalidate();
        chart.setPinchZoom(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
