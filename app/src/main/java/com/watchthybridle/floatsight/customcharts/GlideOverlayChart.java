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
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Pair;
import android.view.MotionEvent;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.*;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.watchthybridle.floatsight.R;
import com.watchthybridle.floatsight.chartfragments.AllMetricsTimeChartMarker;
import com.watchthybridle.floatsight.linedatasetcreation.AllMetricsVsTimeChartDataSetHolder;
import com.watchthybridle.floatsight.linedatasetcreation.ChartDataSetProperties;

import java.awt.font.NumericShaper;
import java.text.DecimalFormat;
import java.util.List;

import static com.github.mikephil.charting.components.YAxis.YAxisLabelPosition.INSIDE_CHART;

public class GlideOverlayChart extends RangeMarkerChart implements OnChartValueSelectedListener {

    public LineChart glideChart;

    public GlideOverlayChart(Context context) {
        super(context);
        glideChart = new LineChart(context);
        glideChart.setOnChartValueSelectedListener(this);

        glideChart.setHighlightPerDragEnabled(false);
        glideChart.setHighlightPerTapEnabled(false);
        setMaxHighlightDistance(5000);

        setAxisLabelPosition();
        setAxisLabelCount();
        setAxisLabelValueFormats();
        setAxisLabelColors();
        setLegendPosition();
        setZoomHandling();
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
                new CustomYAxisValueFormatter(ChartDataSetProperties.GLIDE_FORMAT));
        glideChart.getAxisRight().setValueFormatter(
                new CustomYAxisValueFormatter(ChartDataSetProperties.GLIDE_FORMAT));

        this.getAxisLeft().setValueFormatter(
                new CustomYAxisValueFormatter(ChartDataSetProperties.VELOCITY_FORMAT));
        this.getAxisRight().setValueFormatter(
                new CustomYAxisValueFormatter(ChartDataSetProperties.DISTANCE_FORMAT));
    }

    private void setAxisLabelColors() {
        this.getAxisLeft().setTextColor(R.color.vertVelocity);
        this.getAxisRight().setTextColor(R.color.altitude);
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
        boolean success = super.onTouchEvent(event);
        success &= glideChart.onTouchEvent(event);
        return success;
    }

    class CustomYAxisValueFormatter implements IAxisValueFormatter {
        private DecimalFormat mFormat;

        CustomYAxisValueFormatter(DecimalFormat decimalFormat) {
            mFormat = decimalFormat;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mFormat.format(value);
        }
    }

    public void setDataHolder(AllMetricsVsTimeChartDataSetHolder chartDataSetHolder) {
        setData(chartDataSetHolder.getLineDataOuterGraph());
        glideChart.setData(chartDataSetHolder.getLineDataGlideGraph());

        AllMetricsTimeChartMarker markerViewOutsideGraph = new AllMetricsTimeChartMarker(getContext());
        markerViewOutsideGraph.setChartDataSetHolder(chartDataSetHolder.getDataSetPropertiesList());
        markerViewOutsideGraph.setChartView(this);
        setMarker(markerViewOutsideGraph);
    }

    // To get highlights also when clicking on glideChart
    public void onValueSelected(Entry entry, Highlight h) {
        highlightValue(entry.getX(), 0);
    }

    public void onNothingSelected() {
    }
}

