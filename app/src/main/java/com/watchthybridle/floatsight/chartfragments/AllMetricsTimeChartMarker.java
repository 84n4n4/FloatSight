package com.watchthybridle.floatsight.chartfragments;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.TextView;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.watchthybridle.floatsight.R;
import com.watchthybridle.floatsight.linedatasetcreation.AllMetricsVsTimeChartDataSetHolder;
import com.watchthybridle.floatsight.linedatasetcreation.ChartDataSetProperties;

import java.util.ArrayList;
import java.util.List;

public class AllMetricsTimeChartMarker extends MarkerView {
    private List<ChartDataSetProperties> dataSetPropertiesList;

    public AllMetricsTimeChartMarker(Context context) {
        super(context, R.layout.all_metrics_v_time_marker);
        dataSetPropertiesList = new ArrayList<>();
    }

    public void setChartDataSetHolder(List<ChartDataSetProperties> dataSetPropertiesList) {
        this.dataSetPropertiesList = dataSetPropertiesList;
    }

    @Override
    public void refreshContent(Entry entry, Highlight highlight) {
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

    @Override
    public void draw(Canvas canvas, float posX, float posY)
    {
        float chartTop = AllMetricsTimeChartMarker.this.getChartView().getViewPortHandler().contentTop();
        MPPointF offset = getOffsetForDrawingAtPoint(posX, posY);

        int saveId = canvas.save();

        canvas.translate(posX + offset.x, chartTop);
        draw(canvas);
        canvas.restoreToCount(saveId);
    }
}
