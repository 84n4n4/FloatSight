/*
 *
 *     FloatSight
 *     Copyright 2018 Thomas Hirsch
 *     https://github.com/84n4n4/FloatSight
 *
 *     This file is part of FloatSight.
 *     FloatSight is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     FloatSight is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with FloatSight.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package org.floatcast.floatsight.mpandroidchart.customcharts;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import org.floatcast.floatsight.mpandroidchart.customcharts.parcelables.ParcelableHighlight;
import org.floatcast.floatsight.mpandroidchart.customcharts.parcelables.ParcelableLimitLine;
import org.floatcast.floatsight.mpandroidchart.customcharts.parcelables.ParcelableLineDataSetVisibility;

import java.util.ArrayList;

public class RestorableChart extends LineChart {
    private static final String KEY_LIMIT_LINES = "PARCELABLE_LIMIT_LINES";
    private static final String KEY_HIGHLIGHTS = "PARCELABLE_HIGHLIGHTS";
    private static final String KEY_LINE_DATA_VISIBILITY = "KEY_LINE_DATA_VISIBILITY";

    protected String restorableCharIdentifier = "";

    private ArrayList<ParcelableHighlight> restoredHighlights;
    private ArrayList<ParcelableLineDataSetVisibility> restoredLineDataVisibility;

    public RestorableChart(Context context) {
        super(context);
        restoredHighlights = new ArrayList<>();
        restoredLineDataVisibility = new ArrayList<>();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(!isEmpty()) {
            int index = 0;
            for (ParcelableLineDataSetVisibility parcelableLineDataSetVisibility : restoredLineDataVisibility) {
                parcelableLineDataSetVisibility.setLineDataSetVisibily(getLineData().getDataSetByIndex(index));
                index++;
            }
            for (ParcelableHighlight parcelableHighlight : restoredHighlights) {
                highlightValue(parcelableHighlight.getHighLight());
            }
        }
        restoredHighlights.clear();
        restoredLineDataVisibility.clear();
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

    private ArrayList<ParcelableLineDataSetVisibility> getParcelableLineDataVisibility() {
        ArrayList<ParcelableLineDataSetVisibility> parcelableLineVisibilities = new ArrayList<>();
        if(getLineData() != null) {
            for(ILineDataSet lineDataSet : getLineData().getDataSets()) {
                parcelableLineVisibilities.add(new ParcelableLineDataSetVisibility(lineDataSet));
            }
        }
        return parcelableLineVisibilities;
    }

    private void setParcelableLineDataSetVisibility(ArrayList<ParcelableLineDataSetVisibility> parcelableLineDataSetVisibility) {
        restoredLineDataVisibility = parcelableLineDataSetVisibility;
    }

    public void saveState(@NonNull Bundle outState) {
        outState.putParcelableArrayList(KEY_LIMIT_LINES + restorableCharIdentifier, getParcelableXAxisLimitLines());
        outState.putParcelableArrayList(KEY_HIGHLIGHTS + restorableCharIdentifier, getParcelableHighlights());
        outState.putParcelableArrayList(KEY_LINE_DATA_VISIBILITY + restorableCharIdentifier, getParcelableLineDataVisibility());
    }

    public void restoreState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            setParcelableXAxisLimitLines(savedInstanceState.getParcelableArrayList(KEY_LIMIT_LINES + restorableCharIdentifier));
            setParcelableHighlights(savedInstanceState.getParcelableArrayList(KEY_HIGHLIGHTS + restorableCharIdentifier));
            setParcelableLineDataSetVisibility(savedInstanceState.getParcelableArrayList(KEY_LINE_DATA_VISIBILITY + restorableCharIdentifier));
        }
    }
}
