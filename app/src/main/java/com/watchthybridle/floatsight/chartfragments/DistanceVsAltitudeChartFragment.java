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
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.FrameLayout;
import com.watchthybridle.floatsight.R;
import com.watchthybridle.floatsight.csvparser.FlySightTrackData;
import com.watchthybridle.floatsight.customcharts.DistanceVsAltitudeChart;
import com.watchthybridle.floatsight.linedatasetcreation.ChartDataSetProperties;

public class DistanceVsAltitudeChartFragment extends ChartFragment {

    private DistanceVsAltitudeChart chart;

    public DistanceVsAltitudeChartFragment() {
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FrameLayout frameLayout = view.findViewById(R.id.root_chart_view);

        setUpChart();

        frameLayout.addView(chart);
    }

    private void setUpChart() {
        chart = new DistanceVsAltitudeChart(getContext());
    }

    protected void actOnDataChanged(FlySightTrackData flySightTrackData) {
        if (isInvalid(flySightTrackData)) {
            return;
        }

        ChartDataSetProperties chartDataSetProperties =
                new ChartDataSetProperties.DistanceVsAltitudeDataSetProperties();
        chartDataSetProperties.initLineData(getContext(), flySightTrackData);

        chart.setChartDataSetProperties(chartDataSetProperties);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        chart.saveState(outState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        chart.restoreState(savedInstanceState);
    }
}
