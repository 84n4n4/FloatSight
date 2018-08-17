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

package com.watchthybridle.floatsight.linedatasetcreation;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.IntDef;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.watchthybridle.floatsight.R;
import com.watchthybridle.floatsight.csvparser.FlySightTrackData;

import java.lang.annotation.Retention;
import java.util.ArrayList;
import java.util.List;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public class TrackLineDataSetWrapper extends LineDataSet {

    @Retention(SOURCE)
    @IntDef({HOR_VELOCITY_VS_TIME, VERT_VELOCITY_VS_TIME, ALTITUDE_VS_TIME, GLIDE_VS_TIME,
            DISTANCE_VS_TIME})
    public @interface Metric {}
    public static final int HOR_VELOCITY_VS_TIME = 0;
    public static final int VERT_VELOCITY_VS_TIME = 1;
    public static final int ALTITUDE_VS_TIME = 2;
    public static final int GLIDE_VS_TIME = 3;
    public static final int DISTANCE_VS_TIME = 4;

    private TrackLineDataSetWrapper(List<Entry> yVals, String label) {
        super(yVals, label);
    }

    public static TrackLineDataSetWrapper getLineDataSet(Context context, @Metric int metric,
                                                         FlySightTrackData trackData) {
        switch (metric) {
            case HOR_VELOCITY_VS_TIME:
                return getLineDataSet(context, trackData.getHorVelocity(), R.string.hor_velocity_label,
                        R.color.horVelocity, YAxis.AxisDependency.LEFT);
            case VERT_VELOCITY_VS_TIME:
                return getLineDataSet(context, trackData.getVertVelocity(), R.string.vert_velocity_label,
                        R.color.vertVelocity, YAxis.AxisDependency.LEFT);
            case ALTITUDE_VS_TIME:
                return getLineDataSet(context, trackData.getAltitude(), R.string.altitude_label,
                        R.color.altitude, YAxis.AxisDependency.RIGHT);
            case GLIDE_VS_TIME:
                return getLineDataSet(context, trackData.getGlide(), R.string.glide_label,
                        R.color.glide, YAxis.AxisDependency.LEFT);
            case DISTANCE_VS_TIME:
                break;
        }
        return new TrackLineDataSetWrapper(new ArrayList<Entry>(), "empty");
    }

    private static TrackLineDataSetWrapper getLineDataSet(Context context, List<Entry> data, @StringRes int label,
                                       @ColorRes int color, YAxis.AxisDependency axisDependency) {
        TrackLineDataSetWrapper lineDataSet = new TrackLineDataSetWrapper(data, context.getString(label));
        lineDataSet.setColor(ContextCompat.getColor(context, color));
        lineDataSet.setValueTextColor(ContextCompat.getColor(context, color));
        lineDataSet.setAxisDependency(axisDependency);
        lineDataSet.setDrawValues(false);
        lineDataSet.setDrawCircles(false);
        return lineDataSet;
    }

}
