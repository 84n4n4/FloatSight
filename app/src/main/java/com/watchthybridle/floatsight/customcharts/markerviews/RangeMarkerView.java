package com.watchthybridle.floatsight.customcharts.markerviews;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.LayoutRes;
import com.github.mikephil.charting.utils.MPPointF;
import com.watchthybridle.floatsight.customcharts.RangeMarkerChart;

public class RangeMarkerView extends TouchAbleMarkerView {

    public RangeMarkerView(Context context, @LayoutRes int layoutRes) {
        super(context, layoutRes);
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
