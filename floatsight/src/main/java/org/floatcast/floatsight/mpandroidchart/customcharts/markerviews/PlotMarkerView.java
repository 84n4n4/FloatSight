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

package org.floatcast.floatsight.mpandroidchart.customcharts.markerviews;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.TextView;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import org.floatcast.floatsight.R;
import org.floatcast.floatsight.data.FlySightTrackData;
import org.floatcast.floatsight.mpandroidchart.customcharts.GlideOverlayChart;
import org.floatcast.floatsight.mpandroidchart.linedatasetcreation.ChartDataSetProperties;

import java.util.List;

public class PlotMarkerView extends TouchAbleMarkerView {
    public PlotMarkerView(Context context) {
        super(context, R.layout.plot_marker);
    }

    void customOnLongClick() {
        ((GlideOverlayChart) getChartView()).setRangeMarker();
    }

    void customOnClick() {
        getChartView().highlightValues(null);
    }

    @Override
    public void refreshContent(Entry entry, Highlight highlight) {
        GlideOverlayChart chart = (GlideOverlayChart) getChartView();
        FlySightTrackData flySightTrackData = chart.getDataSetHolder().getFlySightTrackData();
        List<ChartDataSetProperties> dataSetPropertiesList = chart.getDataSetHolder().getDataSetPropertiesList();
        for (ChartDataSetProperties chartDataSetProperties : dataSetPropertiesList) {
            setTextView(chartDataSetProperties, entry.getX(), flySightTrackData);
        }
        super.refreshContent(entry, highlight);
    }

    private void setTextView(ChartDataSetProperties dataSetProperties, float xValue, FlySightTrackData flySightTrackData) {
        String formattedValue = dataSetProperties.getFormattedValueForPosition(getContext(), xValue, flySightTrackData);

        TextView textView = findViewById(dataSetProperties.markerTextView);
        textView.setTextColor(getResources().getColor(dataSetProperties.color));
        textView.setText(formattedValue);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-getWidth(), 0);
    }

    @Override
    public void draw(Canvas canvas, float posX, float posY)
    {
        float chartTop = getChartView().getViewPortHandler().contentTop();
        MPPointF offset = getOffsetForDrawingAtPoint(posX, posY);

        int saveId = canvas.save();

        canvas.translate(posX + offset.x, chartTop);
        draw(canvas);
        canvas.restoreToCount(saveId);

        setTouchArea(posX + offset.x, chartTop, posX + offset.x + getWidth(), chartTop + getHeight());
    }
}
