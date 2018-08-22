package com.watchthybridle.floatsight.chartfragments;

import android.content.Context;
import android.widget.TextView;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.watchthybridle.floatsight.R;

import java.text.DecimalFormat;
import java.util.List;

public class CustomYValueMarker extends MarkerView {
    private TextView textView;
    private List<DecimalFormat> decimalFormats;

    public CustomYValueMarker(Context context, int layoutResource, List<DecimalFormat> decimalFormats) {
        super(context, layoutResource);
        textView = findViewById(R.id.marker_text_view);
        this.decimalFormats = decimalFormats;
    }

    @Override
    public void refreshContent(Entry entry, Highlight highlight) {
        DecimalFormat decimalFormat = decimalFormats.get(highlight.getDataSetIndex());
        textView.setText(decimalFormat.format(entry.getY()));
        super.refreshContent(entry, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
