package com.watchthybridle.floatsight.customcharts.markerviews;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.TextView;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.watchthybridle.floatsight.R;
import com.watchthybridle.floatsight.csvparser.FlySightTrackData;
import com.watchthybridle.floatsight.customcharts.GlideOverlayChart;
import com.watchthybridle.floatsight.customcharts.RangeMarkerChart;
import com.watchthybridle.floatsight.linedatasetcreation.ChartDataSetProperties;

import java.util.List;

public class RangeMarkerView extends TouchAbleMarkerView {

    public RangeMarkerView(Context context) {
        super(context, R.layout.range_marker);
    }

    @Override
    public void refreshContent(Entry entry, Highlight highlight) {
        GlideOverlayChart chart = (GlideOverlayChart) RangeMarkerView.this.getChartView();

        List<LimitLine> limitLines = chart.getXAxis().getLimitLines();
        int limitStart = (int) limitLines.get(0).getLimit();
        int limitEnd = (int) limitLines.get(1).getLimit();

        List<ChartDataSetProperties> dataSetPropertiesList = chart.getDataSetHolder().getDataSetPropertiesList();
        for (ChartDataSetProperties chartDataSetProperties : dataSetPropertiesList) {
            TextView textView = findViewById(chartDataSetProperties.markerTextView);
            textView.setTextColor(getResources().getColor(chartDataSetProperties.color));
            textView.setText(chartDataSetProperties.getFormattedValueForRange(getContext(), limitStart, limitEnd));
        }
        TextView textView = findViewById(R.id.time_marker_text_view);
        textView.setTextColor(getResources().getColor(R.color.time));
        FlySightTrackData flySightTrackData = chart.getDataSetHolder().getFlySightTrackData();
        float timeDiff = flySightTrackData.calculateTimeDiffSec(limitStart, limitEnd);

        textView.setText(getContext().getString(R.string.seconds, ChartDataSetProperties.TIME_FORMAT.format(timeDiff)));
        super.refreshContent(entry, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(0, 0);
    }

    @Override
    public void draw(Canvas canvas, float posX, float posY)
    {
        float chartBottom = RangeMarkerView.this.getChartView().getViewPortHandler().contentBottom();

        int saveId = canvas.save();

        canvas.translate(posX, chartBottom - getHeight());
        draw(canvas);
        canvas.restoreToCount(saveId);

        setTouchArea(posX, chartBottom - getHeight(), posX + getWidth(), chartBottom);
    }

    @Override
    public void customOnClick() {
        ((RangeMarkerChart) getChartView()).clearRangeMarkers();
    }

    @Override
    public void customOnLongClick() {
    }
}
