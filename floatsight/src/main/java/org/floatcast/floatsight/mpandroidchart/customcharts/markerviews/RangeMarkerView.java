package org.floatcast.floatsight.mpandroidchart.customcharts.markerviews;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.TextView;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import org.floatcast.floatsight.R;
import org.floatcast.floatsight.data.FlySightTrackData;
import org.floatcast.floatsight.mpandroidchart.customcharts.GlideOverlayChart;
import org.floatcast.floatsight.mpandroidchart.customcharts.RangeMarkerChart;
import org.floatcast.floatsight.mpandroidchart.linedatasetcreation.ChartDataSetProperties;

import java.util.List;

public class RangeMarkerView extends TouchAbleMarkerView {

    public RangeMarkerView(Context context) {
        super(context, R.layout.plot_range_marker);
    }

    @Override
    public void refreshContent(Entry entry, Highlight highlight) {
        GlideOverlayChart chart = (GlideOverlayChart) getChartView();
        List<LimitLine> limitLines = chart.getXAxis().getLimitLines();
        float limitStart = limitLines.get(0).getLimit();
        float limitEnd = limitLines.get(1).getLimit();
        FlySightTrackData flySightTrackData = chart.getDataSetHolder().getFlySightTrackData();

        List<ChartDataSetProperties> dataSetPropertiesList = chart.getDataSetHolder().getDataSetPropertiesList();
        for (ChartDataSetProperties chartDataSetProperties : dataSetPropertiesList) {
            setTextView(chartDataSetProperties, flySightTrackData, limitStart, limitEnd);
        }
        super.refreshContent(entry, highlight);
    }

    private void setTextView(ChartDataSetProperties dataSetProperties, FlySightTrackData trackData,
                             float limitStart, float limitEnd) {
        String formattedValue = dataSetProperties.getFormattedValueForRange(getContext(), limitStart, limitEnd, trackData);

        TextView textView = findViewById(dataSetProperties.markerTextView);
        textView.setTextColor(getResources().getColor(dataSetProperties.color));
        textView.setText(formattedValue);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(0, 0);
    }

    @Override
    public void draw(Canvas canvas, float posX, float posY)
    {
        float chartBottom = getChartView().getViewPortHandler().contentBottom();

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
