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
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.watchthybridle.floatsight.R;
import com.watchthybridle.floatsight.customcharts.markerviews.AllMetricsRangeMarkerView;
import com.watchthybridle.floatsight.customcharts.markerviews.AllMetricsTimeChartMarkerView;
import com.watchthybridle.floatsight.customcharts.markerviews.TouchAbleMarkerView;
import com.watchthybridle.floatsight.linedatasetcreation.AllMetricsVsTimeChartDataSetHolder;
import com.watchthybridle.floatsight.linedatasetcreation.ChartDataSetProperties;

import static com.github.mikephil.charting.components.YAxis.YAxisLabelPosition.INSIDE_CHART;
import static com.watchthybridle.floatsight.linedatasetcreation.AllMetricsVsTimeChartDataSetHolder.ALTITUDE;

public class GlideOverlayChart extends RangeMarkerChart implements OnChartValueSelectedListener {

    public GlideChart glideChart;
    private AllMetricsVsTimeChartDataSetHolder chartDataSetHolder;

    public GlideOverlayChart(Context context) {
        super(context);
        restorableCharIdentifier = "GLIDE_OVERLAY_CHART";
        glideChart = new GlideChart(context);
        glideChart.setOnChartValueSelectedListener(this);

        glideChart.setHighlightPerDragEnabled(false);
        glideChart.setHighlightPerTapEnabled(false);
        setMaxHighlightDistance(5000);

        setAxisLabelPosition();
        setAxisLabelCount();
        setAxisLabelValueFormats();
        setAxisLabelColors(context);
        setLegendPosition();
        setZoomHandling();
        invalidate();
    }

    private void setZoomHandling() {
        setPinchZoom(false);
        setDoubleTapToZoomEnabled(false);

        glideChart.setPinchZoom(false);
        glideChart.setDoubleTapToZoomEnabled(false);
    }

    private void setAxisLabelPosition() {
        this.getAxisLeft().setPosition(INSIDE_CHART);
        this.getAxisRight().setPosition(INSIDE_CHART);
        glideChart.getAxisLeft().setPosition(INSIDE_CHART);
        glideChart.getAxisRight().setPosition(INSIDE_CHART);

        this.getAxisLeft().setYOffset(5);
        this.getAxisRight().setYOffset(5);
        glideChart.getAxisLeft().setYOffset(-5);
        glideChart.getAxisRight().setYOffset(-5);
    }

    private void setAxisLabelCount() {
        this.getXAxis().setDrawLabels(false);
        glideChart.getXAxis().setDrawLabels(false);

        this.getAxisLeft().setLabelCount(6, true);
        this.getAxisRight().setLabelCount(6, true);
        glideChart.getAxisLeft().setLabelCount(6, true);
        glideChart.getAxisRight().setLabelCount(6, true);
    }

    private void setAxisLabelValueFormats() {
        glideChart.getAxisLeft().setValueFormatter(
                new CustomAxisValueFormatter(ChartDataSetProperties.GLIDE_FORMAT));
        glideChart.getAxisRight().setValueFormatter(
                new CustomAxisValueFormatter(ChartDataSetProperties.GLIDE_FORMAT));

        this.getAxisLeft().setValueFormatter(
                new CustomAxisValueFormatter(ChartDataSetProperties.VELOCITY_FORMAT));
        this.getAxisRight().setValueFormatter(
                new CustomAxisValueFormatter(ChartDataSetProperties.DISTANCE_FORMAT));
    }

    private void setAxisLabelColors(Context context) {
        getAxisLeft().setTextColor(context.getResources().getColor(R.color.horVelocity));
        getAxisRight().setTextColor(context.getResources().getColor(R.color.altitude));
    }

    private void setLegendPosition() {
        glideChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        this.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);

        this.getDescription().setText("");
        glideChart.getDescription().setText("");
    }

    @Override
    public void invalidate() {
        super.invalidate();
        glideChart.invalidate();
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

        boolean success = super.onTouchEvent(event);
        success &= glideChart.onTouchEvent(event);
        return success;
    }

    public void setDataSetHolder(AllMetricsVsTimeChartDataSetHolder chartDataSetHolder) {
        this.chartDataSetHolder = chartDataSetHolder;

        setData(chartDataSetHolder.getLineDataOuterGraph());
        glideChart.setData(chartDataSetHolder.getLineDataGlideGraph());

        AllMetricsTimeChartMarkerView markerViewOutsideGraph = new AllMetricsTimeChartMarkerView(getContext());
        markerViewOutsideGraph.setChartView(this);
        setMarker(markerViewOutsideGraph);

        AllMetricsRangeMarkerView rangeMarkerView = new AllMetricsRangeMarkerView(getContext());
        rangeMarkerView.setChartView(this);
        setRangeMarkerView(rangeMarkerView);
        zoomInOnMinMaxAltitude();
    }

    private void zoomInOnMinMaxAltitude() {
        Pair<Entry, Entry> minMaxAltitude = chartDataSetHolder.getDataSetPropertiesList().get(ALTITUDE).getMinMaxYEntries();
        float minX = minMaxAltitude.second.getX();
        float maxX = minMaxAltitude.first.getX();
        float scaleY = 1;
        float scaleX = getLineData().getXMax() / (maxX - minX);
        float centerX = (maxX - minX) / 2 + minX;
        float centerY = 1;
        zoom(scaleX, scaleY, centerX, centerY, YAxis.AxisDependency.RIGHT);
        glideChart.zoom(scaleX, scaleY, centerX, centerY, YAxis.AxisDependency.RIGHT);
    }

    public AllMetricsVsTimeChartDataSetHolder getDataSetHolder() {
        return chartDataSetHolder;
    }

    // To get highlights also when clicking on glideChart
    public void onValueSelected(Entry entry, Highlight h) {
        highlightValue(entry.getX(), 0);
    }

    public void onNothingSelected() {
    }

    public class GlideChart extends RestorableChart {
        public GlideChart(Context context) {
            super(context);
            restorableCharIdentifier = "GLIDE_CHART";
        }
    }
}

