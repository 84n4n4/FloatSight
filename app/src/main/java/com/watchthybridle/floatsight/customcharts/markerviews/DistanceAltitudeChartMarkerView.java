package com.watchthybridle.floatsight.customcharts.markerviews;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.TextView;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.watchthybridle.floatsight.R;
import com.watchthybridle.floatsight.linedatasetcreation.ChartDataSetProperties;

import java.text.DecimalFormat;

public class DistanceAltitudeChartMarkerView extends MarkerView {

    public DistanceAltitudeChartMarkerView(Context context) {
        super(context, R.layout.distance_altitude_marker);
    }

    @Override
    public void refreshContent(Entry entry, Highlight highlight) {
        TextView distanceTextView = findViewById(R.id.distance_marker_text_view);
        TextView altitudeTextView = findViewById(R.id.altitude_marker_text_view);
        distanceTextView.setTextColor(getResources().getColor(R.color.distance));
        altitudeTextView.setTextColor(getResources().getColor(R.color.altitude));
        DecimalFormat decimalFormat = ChartDataSetProperties.DISTANCE_FORMAT;

        distanceTextView.setText(getContext().getString(R.string.m, decimalFormat.format(entry.getX())));
        altitudeTextView.setText(getContext().getString(R.string.m, decimalFormat.format(entry.getY())));

        super.refreshContent(entry, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-getWidth(), 0);
    }

    @Override
    public void draw(Canvas canvas, float posX, float posY)
    {
        MPPointF offset = getOffsetForDrawingAtPoint(posX, posY);

        int saveId = canvas.save();

        canvas.translate(posX + offset.x, 0);
        draw(canvas);
        canvas.restoreToCount(saveId);
    }
}
