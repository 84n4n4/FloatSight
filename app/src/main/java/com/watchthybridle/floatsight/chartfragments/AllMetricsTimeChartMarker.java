package com.watchthybridle.floatsight.chartfragments;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.watchthybridle.floatsight.R;
import com.watchthybridle.floatsight.customcharts.GlideOverlayChart;
import com.watchthybridle.floatsight.linedatasetcreation.AllMetricsVsTimeChartDataSetHolder;
import com.watchthybridle.floatsight.linedatasetcreation.ChartDataSetProperties;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AllMetricsTimeChartMarker extends MarkerView {
    private Rect markerViewDrawArea;
    private boolean isTouched = false;

    public AllMetricsTimeChartMarker(Context context) {
        super(context, R.layout.all_metrics_v_time_marker);
        markerViewDrawArea = new Rect(0,0,0,0);
    }

    @Override
    @SuppressWarnings("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {
        int longPressTimeout = ViewConfiguration.getLongPressTimeout();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            isTouched = true;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isTouched) {
                        GlideOverlayChart chart = (GlideOverlayChart) AllMetricsTimeChartMarker.this.getChartView();
                        chart.setRangeMarker();
                    }
                }
            }, longPressTimeout);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            isTouched = false;
        }
        return true;
    }

    @Override
    public void refreshContent(Entry entry, Highlight highlight) {
        GlideOverlayChart chart = (GlideOverlayChart) AllMetricsTimeChartMarker.this.getChartView();
        List<ChartDataSetProperties> dataSetPropertiesList = chart.getDataSetHolder().getDataSetPropertiesList();
        for (ChartDataSetProperties chartDataSetProperties : dataSetPropertiesList) {
            TextView textView = findViewById(chartDataSetProperties.markerTextView);
            textView.setTextColor(getResources().getColor(chartDataSetProperties.color));
            textView.setText(chartDataSetProperties.getFormattedValueForPosition(getContext(), entry.getX()));
        }
        super.refreshContent(entry, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-getWidth(), 0);
    }

    public Rect getMarkerViewDrawArea() {
        return markerViewDrawArea;
    }

    @Override
    public void draw(Canvas canvas, float posX, float posY)
    {
        float chartTop = AllMetricsTimeChartMarker.this.getChartView().getViewPortHandler().contentTop();
        MPPointF offset = getOffsetForDrawingAtPoint(posX, posY);

        int saveId = canvas.save();

        canvas.translate(posX + offset.x, chartTop);
        draw(canvas);
        canvas.restoreToCount(saveId);

        markerViewDrawArea.top = (int) chartTop;
        markerViewDrawArea.left = (int) (posX + offset.x);
        markerViewDrawArea.right = (int) (posX + offset.x + getWidth());
        markerViewDrawArea.bottom = (int) (chartTop + getHeight());
    }
}
