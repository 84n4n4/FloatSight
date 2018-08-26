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
import com.watchthybridle.floatsight.customcharts.parcelables.ParcelableLineDataVisibility;

import java.util.ArrayList;

public class RestorableChart extends LineChart {
    private static final String KEY_LIMIT_LINES = "PARCELABLE_LIMIT_LINES";
    private static final String KEY_HIGHLIGHTS = "PARCELABLE_HIGHLIGHTS";
    private static final String KEY_LINE_DATA_VISIBILITY = "KEY_LINE_DATA_VISIBILITY";

    protected String restorableCharIdentifier = "";

    private ArrayList<ParcelableHighlight> restoredHighlights;
    private ParcelableLineDataVisibility restoredLineDataVisibility;

    public RestorableChart(Context context) {
        super(context);
        restoredHighlights = new ArrayList<>();
        restoredLineDataVisibility = null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(!isEmpty()) {
            if(restoredLineDataVisibility != null) {
                restoredLineDataVisibility.setLineDataVisibily(getLineData());
                restoredLineDataVisibility = null;
            }
            for (ParcelableHighlight parcelableHighlight : restoredHighlights) {
                highlightValue(parcelableHighlight.getHighLight());
            }
        }
        restoredHighlights.clear();
        super.onDraw(canvas);
    }

    private ArrayList<ParcelableHighlight> getParcelableHighlights() {
        ArrayList<ParcelableHighlight> parcelableHighlights = new ArrayList<>();
        if(getHighlighted() != null) {
            for(Highlight highlight : getHighlighted()) {
                parcelableHighlights.add(new ParcelableHighlight(highlight));
            }
        }
        return parcelableHighlights;
    }

    private void setParcelableHighlights(ArrayList<ParcelableHighlight> parcelableHighlights) {
        restoredHighlights = parcelableHighlights;
    }

    private void setParcelableXAxisLimitLines(ArrayList<ParcelableLimitLine> parcelableXAxisLimitLines){
        for (ParcelableLimitLine parcelableLimitLine : parcelableXAxisLimitLines) {
            getXAxis().getLimitLines().add(parcelableLimitLine.getLimitLine());
        }
    }

    private ArrayList<ParcelableLimitLine> getParcelableXAxisLimitLines() {
        ArrayList<ParcelableLimitLine> parcelableLimitLines = new ArrayList<>();
        for(LimitLine limitLine : getXAxis().getLimitLines()) {
            parcelableLimitLines.add(new ParcelableLimitLine(limitLine));
        }
        return parcelableLimitLines;
    }

    private ParcelableLineDataVisibility getParcelableLineDataVisibility() {
        return new ParcelableLineDataVisibility(getLineData());
    }

    private void setParcelableLineDataVisibility(ParcelableLineDataVisibility parcelableLineDataVisibility) {
        restoredLineDataVisibility = parcelableLineDataVisibility;
    }

    public void saveState(@NonNull Bundle outState) {
        outState.putParcelableArrayList(KEY_LIMIT_LINES + restorableCharIdentifier, getParcelableXAxisLimitLines());
        outState.putSerializable(KEY_HIGHLIGHTS + restorableCharIdentifier, getParcelableHighlights());
        outState.putParcelable(KEY_LINE_DATA_VISIBILITY + restorableCharIdentifier, getParcelableLineDataVisibility());
    }

    public void restoreState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            setParcelableXAxisLimitLines(savedInstanceState.getParcelableArrayList(KEY_LIMIT_LINES + restorableCharIdentifier));
            setParcelableHighlights(savedInstanceState.getParcelableArrayList(KEY_HIGHLIGHTS + restorableCharIdentifier));
            setParcelableLineDataVisibility(savedInstanceState.getParcelable(KEY_LINE_DATA_VISIBILITY + restorableCharIdentifier));
        }
    }
}
