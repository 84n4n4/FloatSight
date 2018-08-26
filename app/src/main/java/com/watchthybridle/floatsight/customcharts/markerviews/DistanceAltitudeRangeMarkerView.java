package com.watchthybridle.floatsight.customcharts.markerviews;

import android.content.Context;
import android.widget.TextView;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.watchthybridle.floatsight.R;
import com.watchthybridle.floatsight.customcharts.DistanceVsAltitudeChart;
import com.watchthybridle.floatsight.linedatasetcreation.ChartDataSetProperties;

import java.text.DecimalFormat;
import java.util.List;

public class DistanceAltitudeRangeMarkerView extends RangeMarkerView {

    public DistanceAltitudeRangeMarkerView(Context context) {
        super(context, R.layout.distance_altitude_range_marker);
    }

    @Override
    public void refreshContent(Entry entry, Highlight highlight) {
        DistanceVsAltitudeChart chart = (DistanceVsAltitudeChart) getChartView();
        ChartDataSetProperties chartDataSetProperties = chart.getChartDataSetProperties();

        List<LimitLine> limitLines = chart.getXAxis().getLimitLines();
        float limitStart = limitLines.get(0).getLimit();
        float limitEnd = limitLines.get(1).getLimit();
        Entry entryStart = chartDataSetProperties.iLineDataSet.getEntriesForXValue(limitStart).get(0);
        Entry entryEnd = chartDataSetProperties.iLineDataSet.getEntriesForXValue(limitEnd).get(0);

        float altitudeDiff = entryEnd.getY() - entryStart.getY();
        float distanceDiff = entryEnd.getX() - entryStart.getX();

        TextView distanceTextView = findViewById(R.id.distance_marker_text_view);
        TextView altitudeTextView = findViewById(R.id.altitude_marker_text_view);
        distanceTextView.setTextColor(getResources().getColor(R.color.distance));
        altitudeTextView.setTextColor(getResources().getColor(R.color.altitude));
        DecimalFormat decimalFormat = ChartDataSetProperties.DISTANCE_FORMAT;

        distanceTextView.setText(getContext().getString(R.string.m, decimalFormat.format(distanceDiff)));
        altitudeTextView.setText(getContext().getString(R.string.m, decimalFormat.format(altitudeDiff)));

        super.refreshContent(entry, highlight);
    }
}
