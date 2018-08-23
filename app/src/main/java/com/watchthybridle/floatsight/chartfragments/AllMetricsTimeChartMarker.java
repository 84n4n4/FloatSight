package com.watchthybridle.floatsight.chartfragments;

import android.content.Context;
import android.widget.TextView;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.watchthybridle.floatsight.R;
import com.watchthybridle.floatsight.linedatasetcreation.AllMetricsVsTimeChartDataSetHolder;
import com.watchthybridle.floatsight.linedatasetcreation.ChartDataSetProperties;

public class AllMetricsTimeChartMarker extends MarkerView {
    private AllMetricsVsTimeChartDataSetHolder chartDataSetHolder;

    public AllMetricsTimeChartMarker(Context context, AllMetricsVsTimeChartDataSetHolder chartDataSetHolder) {
        super(context, R.layout.all_metrics_v_time_marker);
        this.chartDataSetHolder = chartDataSetHolder;
    }

    @Override
    public void refreshContent(Entry entry, Highlight highlight) {
        for (ChartDataSetProperties chartDataSetProperties : chartDataSetHolder.getDataSetPropertiesList()) {
            TextView textView = findViewById(chartDataSetProperties.markerTextView);
            textView.setTextColor(getResources().getColor(chartDataSetProperties.color));
            textView.setText(chartDataSetProperties.getFormattedValueForPosition(getContext(), entry.getX()));
        }
        super.refreshContent(entry, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-getWidth(), -getHeight());
    }
}
