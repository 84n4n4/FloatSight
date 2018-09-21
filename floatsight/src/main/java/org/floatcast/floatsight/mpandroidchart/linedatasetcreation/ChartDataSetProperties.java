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

package org.floatcast.floatsight.mpandroidchart.linedatasetcreation;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.support.annotation.StringDef;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.util.Pair;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;

import org.floatcast.floatsight.R;
import org.floatcast.floatsight.csvparser.FlySightTrackPoint;
import org.floatcast.floatsight.data.FlySightTrackData;

import java.lang.annotation.Retention;
import java.text.DecimalFormat;
import java.util.List;

import static java.lang.annotation.RetentionPolicy.SOURCE;

@SuppressWarnings("PMD.FieldDeclarationsShouldBeAtStartOfClass")
public abstract class ChartDataSetProperties {
    public static final DecimalFormat VELOCITY_FORMAT = new DecimalFormat("#0");
    public static final DecimalFormat DISTANCE_FORMAT = new DecimalFormat("###0");
    public static final DecimalFormat GLIDE_FORMAT = new DecimalFormat("#0.00");
    public static final DecimalFormat TIME_FORMAT = new DecimalFormat("00.00");

    @Retention(SOURCE)
    @StringDef({METRIC, IMPERIAL})
    public @interface UnitSystem {}
    public static final String METRIC = "METRIC";
    public static final String IMPERIAL = "IMPERIAL";
    public static final String UNIT_BUNDLE_KEY = "UNIT_SYSTEM";

    public @IdRes int markerTextView;
    public @StringRes int labelStringResource;
    public @StringRes int unitStringResource;
    public @ColorRes int color;
    public DecimalFormat decimalFormat;
    public YAxis.AxisDependency axisDependency;
    public LineDataSet iLineDataSet;

    public TrackPointValueProvider xValueProvider;
    public TrackPointValueProvider yValueProvider;

    public RangeValueStrategy rangeValueStrategy;

    public ChartDataSetProperties(@StringRes int labelStringResource,
                                  @ColorRes int color,
                                  @StringRes int unitStringResource,
                                  YAxis.AxisDependency axisDependency,
                                  DecimalFormat decimalFormat,
                                  @IdRes int markerTextView,
                                  RangeValueStrategy rangeValueStrategy,
                                  TrackPointValueProvider xValueProvider,
                                  TrackPointValueProvider yValueProvider) {
        this.markerTextView = markerTextView;
        this.labelStringResource = labelStringResource;
        this.color = color;
        this.decimalFormat = decimalFormat;
        this.axisDependency = axisDependency;
        this.unitStringResource = unitStringResource;
        this.rangeValueStrategy = rangeValueStrategy;
        this.xValueProvider = xValueProvider;
        this.yValueProvider = yValueProvider;
    }

    public void initLineData(Context context, FlySightTrackData flySightTrackData) {
        List<Entry> data = flySightTrackData.getEntries(xValueProvider, yValueProvider);
        iLineDataSet = new LineDataSet(data, context.getString(labelStringResource));
        iLineDataSet.setColor(ContextCompat.getColor(context, color));
        iLineDataSet.setValueTextColor(ContextCompat.getColor(context, color));
        iLineDataSet.setAxisDependency(axisDependency);
        iLineDataSet.setDrawValues(false);
        iLineDataSet.setDrawCircles(false);
        iLineDataSet.setDrawHorizontalHighlightIndicator(false);
        iLineDataSet.setHighLightColor(ContextCompat.getColor(context, R.color.highlightColor));
        iLineDataSet.setHighlightLineWidth(2);
    }

    public String getFormattedValueForPosition(Context context, float xPosition, FlySightTrackData flySightTrackData) {
        float value = 0;
        for (FlySightTrackPoint trackPoint : flySightTrackData.getFlySightTrackPoints()) {
            if (xValueProvider.getValue(trackPoint) >= xPosition) {
                value = yValueProvider.getValue(trackPoint);
                break;
            }
        }
        return context.getString(unitStringResource, decimalFormat.format(value));
    }

    public String getFormattedValue(Context context, float value) {
        return context.getString(unitStringResource, decimalFormat.format(value));
    }

    public String getFormattedYMax(Context context, FlySightTrackData flySightTrackData) {
        float max = 0;
        for(FlySightTrackPoint point : flySightTrackData.getFlySightTrackPoints()) {
            if(yValueProvider.getValue(point) > max) {
                max = yValueProvider.getValue(point);
            }
        }
        return getFormattedValue(context, max);
    }

    public String getFormattedYMin(Context context, FlySightTrackData flySightTrackData) {
        float min = Float.MAX_VALUE;
        for(FlySightTrackPoint point : flySightTrackData.getFlySightTrackPoints()) {
            if(yValueProvider.getValue(point) < min) {
                min = yValueProvider.getValue(point);
            }
        }
        return getFormattedValue(context, min);
    }

    public String getFormattedValueForRange(Context context, float start, float end, FlySightTrackData flySightTrackData) {
        float value = rangeValueStrategy.getRangeValue(start, end, xValueProvider, yValueProvider, flySightTrackData);
        return context.getString(unitStringResource, decimalFormat.format(value));
    }

    public Pair<Entry, Entry> getMinMaxYEntries() {
        Entry min = iLineDataSet.getEntryForIndex(0);
        Entry max = iLineDataSet.getEntryForIndex(0);

        for(int index = 0; index < iLineDataSet.getEntryCount(); index ++) {
            Entry entry = iLineDataSet.getEntryForIndex(index);
            if (entry.getY() > max.getY()) {
                max = entry;
            }
            if (entry.getY() < min.getY()) {
                min = entry;
            }
        }
        return new Pair<>(min, max);
    }

    public static class HorizontalVelocityDataSetProperties extends ChartDataSetProperties {
        public HorizontalVelocityDataSetProperties(TrackPointValueProvider xValueProvider, @UnitSystem String units) {
            super(R.string.hor_velocity_label,
                    R.color.horVelocity,
                    R.string.kmh,
                    YAxis.AxisDependency.LEFT,
                    VELOCITY_FORMAT,
                    R.id.hor_velocity_marker_text_view,
                    RangeValueStrategy.RANGE_AVERAGE,
                    xValueProvider,
                    TrackPointValueProvider.METRIC_HOR_VELOCITY_VALUE_PROVIDER);
            if(units.equals(IMPERIAL)) {
                this.yValueProvider = TrackPointValueProvider.IMPERIAL_HOR_VELOCITY_VALUE_PROVIDER;
                this.unitStringResource = R.string.mph;
            }
        }
    }

    public static class VerticalVelocityDataSetProperties extends ChartDataSetProperties {
        public VerticalVelocityDataSetProperties(TrackPointValueProvider xValueProvider, @UnitSystem String units) {
            super(R.string.vert_velocity_label,
                    R.color.vertVelocity,
                    R.string.kmh,
                    YAxis.AxisDependency.LEFT,
                    VELOCITY_FORMAT,
                    R.id.vert_velocity_marker_text_view,
                    RangeValueStrategy.RANGE_AVERAGE,
                    xValueProvider,
                    TrackPointValueProvider.METRIC_VERT_VELOCITY_VALUE_PROVIDER);
            if(units.equals(IMPERIAL)) {
                this.yValueProvider = TrackPointValueProvider.IMPERIAL_VERT_VELOCITY_VALUE_PROVIDER;
                this.unitStringResource = R.string.mph;
            }
        }
    }

    public static class AltitudeDataSetProperties extends ChartDataSetProperties {
        public AltitudeDataSetProperties(TrackPointValueProvider xValueProvider, @UnitSystem String units) {
            super(R.string.altitude_label,
                    R.color.altitude,
                    R.string.m,
                    YAxis.AxisDependency.RIGHT,
                    DISTANCE_FORMAT,
                    R.id.altitude_marker_text_view,
                    RangeValueStrategy.RANGE_DIFFERENTIAL,
                    xValueProvider,
                    TrackPointValueProvider.METRIC_ALTITUDE_VALUE_PROVIDER);
            if(units.equals(IMPERIAL)) {
                this.yValueProvider = TrackPointValueProvider.IMPERIAL_ALTITUDE_VALUE_PROVIDER;
                this.unitStringResource = R.string.ft;
            }
        }
    }

    public static class GlideDataSetProperties extends ChartDataSetProperties {
        public GlideDataSetProperties(TrackPointValueProvider xValueProvider, CappedTrackPointValueProvider yGlideValueProvider) {
            super(R.string.glide_label,
                    R.color.glide,
                    R.string.glide,
                    YAxis.AxisDependency.LEFT,
                    GLIDE_FORMAT,
                    R.id.glide_marker_text_view,
                    RangeValueStrategy.RANGE_AVERAGE,
                    xValueProvider,
                    yGlideValueProvider);
        }
    }

    public static class TimeDataSetProperties extends ChartDataSetProperties {
        public TimeDataSetProperties(TrackPointValueProvider xValueProvider) {
            super(-1,
                    R.color.time,
                    R.string.seconds,
                    null,
                    TIME_FORMAT,
                    R.id.time_marker_text_view,
                    RangeValueStrategy.RANGE_DIFFERENTIAL,
                    xValueProvider,
                    TrackPointValueProvider.TIME_VALUE_PROVIDER);
        }

        @Override
        public void initLineData(Context context, FlySightTrackData flySightTrackData) {
            //Y axis data! dont init linedata!
        }
    }


    public static class DistanceDataSetProperties extends ChartDataSetProperties {
        public DistanceDataSetProperties(TrackPointValueProvider xValueProvider, @UnitSystem String units) {
            super(R.string.distance_label,
                    R.color.distance,
                    R.string.m,
                    YAxis.AxisDependency.RIGHT,
                    DISTANCE_FORMAT,
                    R.id.distance_marker_text_view,
                    RangeValueStrategy.RANGE_DIFFERENTIAL,
                    xValueProvider,
                    TrackPointValueProvider.METRIC_DISTANCE_VALUE_PROVIDER);
            if(units.equals(IMPERIAL)) {
                this.yValueProvider = TrackPointValueProvider.IMPERIAL_DISTANCE_VALUE_PROVIDER;
                this.unitStringResource = R.string.ft;
            }
        }

        @Override
        public void initLineData(Context context, FlySightTrackData flySightTrackData) {
            //Y axis data! dont init linedata!
        }
    }
}
