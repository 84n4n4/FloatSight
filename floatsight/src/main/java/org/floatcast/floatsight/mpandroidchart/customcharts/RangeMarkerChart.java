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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;

import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

import org.floatcast.floatsight.R;
import org.floatcast.floatsight.mpandroidchart.customcharts.markerviews.RangeMarkerView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RangeMarkerChart extends RestorableChart {

    private final Paint rangePaint;
    private final int limitLineColor;
    private RangeMarkerView rangeMarkerView = null;
    private boolean rangeVisible = false;

    // preallocation for draw
    private float[] pointValues = new float[4];
    private final Entry entry = new Entry(0, 0);

    public RangeMarkerChart(Context context) {
        super(context);
        rangePaint = new Paint();
        rangePaint.setStyle(Paint.Style.FILL);
        rangePaint.setColor(ContextCompat.getColor(context, R.color.rangeColor));
        limitLineColor = ContextCompat.getColor(context, R.color.limitLineColor);
    }

    @Override
    protected void init() {
        super.init();
        mChartTouchListener = new CustomBarLineChartTouchListener(this, mViewPortHandler.getMatrixTouch(), 3f);
    }

    @Override
    public void highlightValues(Highlight[] highs) {
        super.highlightValues(highs);
        invalidateOptionsMenu();
    }

    @Override
    public void highlightValue(Highlight highlight) {
        super.highlightValue(highlight, false);
        invalidateOptionsMenu();
    }

    public void setRangeMarkerView(RangeMarkerView markerView) {
        rangeMarkerView = markerView;
    }

    public void setRangeMarker() {
        List<LimitLine> limitLines = getXAxis().getLimitLines();

        Highlight[] highlights = getHighlighted();
        if (highlights != null && highlights.length > 0) {
            float xPos = highlights[0].getX();
            LimitLine limitLine = createLimitLine(xPos);
            limitLines.add(limitLine);
        }
        Collections.sort(limitLines, new Comparator<LimitLine>() {
            @Override
            public int compare(LimitLine o1, LimitLine o2) {
                return (int) (o1.getLimit() - o2.getLimit());
            }
        });
        if (limitLines.size() > 2) {
            limitLines.remove(1);
        }
        if (limitLines.size() == 2 && limitLines.get(0).getLimit() == limitLines.get(1).getLimit()) {
            limitLines.remove(1);
        }
        invalidate();
        invalidateOptionsMenu();
    }

    public void invalidateOptionsMenu() {
        Context context = getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                ((Activity)context).invalidateOptionsMenu();
                break;
            }
            context = ((ContextWrapper)context).getBaseContext();
        }
    }

    private LimitLine createLimitLine(float position) {
        LimitLine limitLine = new LimitLine(position);
        limitLine.setLineColor(limitLineColor);
        limitLine.setLineWidth(2);
        return limitLine;
    }

    public void clearRangeMarkers() {
        getXAxis().removeAllLimitLines();
        invalidate();
        rangeVisible = false;
        invalidateOptionsMenu();
    }

    @Override
    @SuppressLint("DrawAllocation")
    protected void onDraw(Canvas canvas) {
        List<LimitLine> limitLines = mXAxis.getLimitLines();
        if (limitLines != null && limitLines.size() == 2) {
            rangeVisible = true;

            float limitA = limitLines.get(0).getLimit();
            float limitB = limitLines.get(1).getLimit();
            pointValues[0] = limitA;
            pointValues[2] = limitB;

            mLeftAxisTransformer.pointValuesToPixel(pointValues);
            canvas.drawRect(pointValues[0], mViewPortHandler.contentBottom(), pointValues[2], mViewPortHandler.contentTop(), rangePaint);

            if (rangeMarkerView != null) {
                entry.setX(limitA);
                rangeMarkerView.refreshContent(entry, new Highlight(limitA, 0f,0));
                rangeMarkerView.draw(canvas, pointValues[0], mViewPortHandler.contentBottom());
            }
        }
        super.onDraw(canvas);
    }

    public RangeMarkerView getRangeMarkerView() {
        return rangeMarkerView;
    }

    public boolean isRangeVisible() {
        return rangeVisible;
    }
}

