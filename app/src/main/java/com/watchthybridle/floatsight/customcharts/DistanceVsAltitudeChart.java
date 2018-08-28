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

package com.watchthybridle.floatsight.customcharts;

import android.content.Context;
import android.graphics.Rect;
import android.util.Pair;
import android.view.MotionEvent;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.watchthybridle.floatsight.R;
import com.watchthybridle.floatsight.customcharts.markerviews.DistanceAltitudeChartMarkerView;
import com.watchthybridle.floatsight.customcharts.markerviews.DistanceAltitudeRangeMarkerView;
import com.watchthybridle.floatsight.customcharts.markerviews.TouchAbleMarkerView;
import com.watchthybridle.floatsight.linedatasetcreation.ChartDataSetProperties;

import static com.github.mikephil.charting.components.YAxis.YAxisLabelPosition.INSIDE_CHART;

public class DistanceVsAltitudeChart extends RangeMarkerChart {

    private ChartDataSetProperties chartDataSetProperties;

    public DistanceVsAltitudeChart(Context context) {
        super(context);
        restorableCharIdentifier = "DISTANCE_ALTITUDE_CHART";
        setMaxHighlightDistance(5000);
        setAxisLabelPosition();
        setAxisLabelValueFormats();
        setAxisLabelColors(context);
        setLegendPosition();
        setZoomHandling();
        invalidate();
    }

    private void setZoomHandling() {
        setPinchZoom(false);
        setDoubleTapToZoomEnabled(false);
    }

    private void setAxisLabelPosition() {
        getAxisLeft().setPosition(INSIDE_CHART);
        getAxisRight().setPosition(INSIDE_CHART);
        getAxisRight().setSpaceTop(10);
        getAxisLeft().setSpaceTop(10);
    }


    private void setAxisLabelValueFormats() {
        this.getAxisLeft().setValueFormatter(
                new CustomAxisValueFormatter(ChartDataSetProperties.DISTANCE_FORMAT));
        this.getAxisRight().setValueFormatter(
                new CustomAxisValueFormatter(ChartDataSetProperties.DISTANCE_FORMAT));
        this.getXAxis().setValueFormatter(
                new CustomAxisValueFormatter(ChartDataSetProperties.DISTANCE_FORMAT));
    }

    private void setAxisLabelColors(Context context) {
        getAxisLeft().setTextColor(context.getResources().getColor(R.color.altitude));
        getAxisRight().setTextColor(context.getResources().getColor(R.color.altitude));
        getXAxis().setTextColor(context.getResources().getColor(R.color.distance));
    }

    private void setLegendPosition() {
        getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        getDescription().setText("");
    }

    @Override
    @SuppressWarnings("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {
        if (mMarker != null
                && isDrawMarkersEnabled()
                && valuesToHighlight()
                && getMarker() instanceof TouchAbleMarkerView) {

            TouchAbleMarkerView marker = (TouchAbleMarkerView) getMarker();
            Rect markerViewDrawArea = marker.getMarkerViewDrawArea();

            if (markerViewDrawArea.contains((int) event.getX(),(int) event.getY())) {
                return marker.dispatchTouchEvent(event);
            }
        }

        if (isRangeVisible() && getRangeMarkerView() != null
                && getMarker() instanceof TouchAbleMarkerView) {
            Rect markerViewDrawArea = getRangeMarkerView().getMarkerViewDrawArea();

            if (markerViewDrawArea.contains((int) event.getX(),(int) event.getY())) {
                return getRangeMarkerView().dispatchTouchEvent(event);
            }
        }

        return super.onTouchEvent(event);
    }

    public void setChartDataSetProperties(ChartDataSetProperties chartDataSetProperties) {
        this.chartDataSetProperties = chartDataSetProperties;

        setData(new LineData(chartDataSetProperties.iLineDataSet));

        DistanceAltitudeChartMarkerView distanceAltitudeChartMarker =
                new DistanceAltitudeChartMarkerView(getContext());
        distanceAltitudeChartMarker.setChartView(this);
        setMarker(distanceAltitudeChartMarker);

        DistanceAltitudeRangeMarkerView rangeMarkerView = new DistanceAltitudeRangeMarkerView(getContext());
        rangeMarkerView.setChartView(this);
        setRangeMarkerView(rangeMarkerView);
        zoomInOnMinMaxAltitude();
        invalidate();
    }

    private void zoomInOnMinMaxAltitude() {
        /*Pair<Entry, Entry> minMaxAltitude = chartDataSetProperties.getMinMaxYEntries();
        float minX = minMaxAltitude.second.getX();
        float maxX = minMaxAltitude.first.getX();
        float scaleY = 1;
        float scaleX = getLineData().getXMax() / (maxX - minX);
        float centerX = (maxX - minX) / 2 + minX;
        float centerY = 1;
        zoom(scaleX, scaleY, centerX, centerY, YAxis.AxisDependency.LEFT);*/
    }

    public ChartDataSetProperties getChartDataSetProperties() {
        return chartDataSetProperties;
    }
}

