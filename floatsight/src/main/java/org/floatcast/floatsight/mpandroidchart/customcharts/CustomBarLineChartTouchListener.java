package org.floatcast.floatsight.mpandroidchart.customcharts;

import android.graphics.Matrix;
import android.view.MotionEvent;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.BarLineChartTouchListener;

public class CustomBarLineChartTouchListener extends BarLineChartTouchListener {

    private final RangeMarkerChart rangeMarkerChart;

    public CustomBarLineChartTouchListener(RangeMarkerChart chart, Matrix touchMatrix, float dragTriggerDistance) {
        super(chart, touchMatrix, dragTriggerDistance);
        this.rangeMarkerChart = chart;
    }

    protected void performHighlight(Highlight highlight, MotionEvent event) {
        super.performHighlight(highlight, event);
        rangeMarkerChart.invalidateOptionsMenu();
    }
}
