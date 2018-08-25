package com.watchthybridle.floatsight.customcharts;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.highlight.Highlight;
import com.watchthybridle.floatsight.customcharts.parcelables.ParcelableHighlight;
import com.watchthybridle.floatsight.customcharts.parcelables.ParcelableLimitLine;

import java.util.ArrayList;

public class RestorableChart extends LineChart {
    private static final String KEY_LIMIT_LINES = "PARCELABLE_LIMIT_LINES";
    private static final String KEY_HIGHLIGHTS = "PARCELABLE_HIGHLIGHTS";

    private ArrayList<ParcelableHighlight> restoredHighlights;

    public RestorableChart(Context context) {
        super(context);
        restoredHighlights = new ArrayList<>();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(!isEmpty()) {
            for (ParcelableHighlight parcelableHighlight : restoredHighlights) {
                highlightValue(parcelableHighlight.getHighLight());
            }
        }
        restoredHighlights.clear();
        super.onDraw(canvas);
    }

    public ArrayList<ParcelableHighlight> getParcelableHighlights() {
        ArrayList<ParcelableHighlight> parcelableHighlights = new ArrayList<>();
        if(getHighlighted() != null) {
            for(Highlight highlight : getHighlighted()) {
                parcelableHighlights.add(new ParcelableHighlight(highlight));
            }
        }
        return parcelableHighlights;
    }

    public void setParcelableHighlights(ArrayList<ParcelableHighlight> parcelableHighlights) {
        restoredHighlights = parcelableHighlights;
    }

    public void setParcelableXAxisLimitLines(ArrayList<ParcelableLimitLine> parcelableXAxisLimitLines){
        for (ParcelableLimitLine parcelableLimitLine : parcelableXAxisLimitLines) {
            getXAxis().getLimitLines().add(parcelableLimitLine.getLimitLine());
        }
    }

    public ArrayList<ParcelableLimitLine> getParcelableXAxisLimitLines() {
        ArrayList<ParcelableLimitLine> parcelableLimitLines = new ArrayList<>();
        for(LimitLine limitLine : getXAxis().getLimitLines()) {
            parcelableLimitLines.add(new ParcelableLimitLine(limitLine));
        }
        return parcelableLimitLines;
    }

    public void saveState(@NonNull Bundle outState) {
        outState.putParcelableArrayList(KEY_LIMIT_LINES, getParcelableXAxisLimitLines());
        outState.putSerializable(KEY_HIGHLIGHTS, getParcelableHighlights());
    }

    public void restoreState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            setParcelableXAxisLimitLines(savedInstanceState.getParcelableArrayList(KEY_LIMIT_LINES));
            setParcelableHighlights(savedInstanceState.getParcelableArrayList(KEY_HIGHLIGHTS));
        }
    }
}
