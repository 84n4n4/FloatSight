package com.watchthybridle.floatsight.customcharts.markerviews;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.TextView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.watchthybridle.floatsight.R;
import com.watchthybridle.floatsight.csvparser.FlySightTrackData;
import com.watchthybridle.floatsight.customcharts.GlideOverlayChart;
import com.watchthybridle.floatsight.linedatasetcreation.ChartDataSetProperties;

import java.util.List;

public class AllMetricsTimeChartMarkerView extends TouchAbleMarkerView {
    public AllMetricsTimeChartMarkerView(Context context) {
        super(context, R.layout.all_metrics_v_time_marker);
    }

    void customOnLongClick() {
        ((GlideOverlayChart) AllMetricsTimeChartMarkerView.this.getChartView()).setRangeMarker();
    }

    void customOnClick() {
        getChartView().highlightValues(null);
    }

    @Override
    public void refreshContent(Entry entry, Highlight highlight) {
        GlideOverlayChart chart = (GlideOverlayChart) getChartView();
        FlySightTrackData flySightTrackData = chart.getDataSetHolder().getFlySightTrackData();
        List<ChartDataSetProperties> dataSetPropertiesList = chart.getDataSetHolder().getDataSetPropertiesList();
        for (ChartDataSetProperties chartDataSetProperties : dataSetPropertiesList) {
            setTextView(chartDataSetProperties, entry.getX(), flySightTrackData);
        }
        super.refreshContent(entry, highlight);
    }

    private void setTextView(ChartDataSetProperties dataSetProperties, float xValue, FlySightTrackData flySightTrackData) {
        String formattedValue = dataSetProperties.getFormattedValueForPosition(getContext(), xValue, flySightTrackData);

        TextView textView = findViewById(dataSetProperties.markerTextView);
        textView.setTextColor(getResources().getColor(dataSetProperties.color));
        textView.setText(formattedValue);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-getWidth(), 0);
    }

    @Override
    public void draw(Canvas canvas, float posX, float posY)
    {
        float chartTop = getChartView().getViewPortHandler().contentTop();
        MPPointF offset = getOffsetForDrawingAtPoint(posX, posY);

        int saveId = canvas.save();

        canvas.translate(posX + offset.x, chartTop);
        draw(canvas);
        canvas.restoreToCount(saveId);

        setTouchArea(posX + offset.x, chartTop, posX + offset.x + getWidth(), chartTop + getHeight());
    }
}
