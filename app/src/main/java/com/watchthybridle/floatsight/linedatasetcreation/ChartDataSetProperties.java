package com.watchthybridle.floatsight.linedatasetcreation;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.util.Pair;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.watchthybridle.floatsight.R;
import com.watchthybridle.floatsight.csvparser.FlySightTrackData;
import com.watchthybridle.floatsight.csvparser.FlySightTrackPoint;

import java.text.DecimalFormat;
import java.util.List;

import static com.watchthybridle.floatsight.linedatasetcreation.RangeValueStrategy.RANGE_AVERAGE;
import static com.watchthybridle.floatsight.linedatasetcreation.RangeValueStrategy.RANGE_DIFFERENTIAL;
import static com.watchthybridle.floatsight.linedatasetcreation.TrackPointValueProvider.*;

public abstract class ChartDataSetProperties {
    public static final DecimalFormat VELOCITY_FORMAT = new DecimalFormat("#0");
    public static final DecimalFormat DISTANCE_FORMAT = new DecimalFormat("###0");
    public static final DecimalFormat GLIDE_FORMAT = new DecimalFormat("#0.00");
    public static final DecimalFormat TIME_FORMAT = new DecimalFormat("00.00");

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
        public HorizontalVelocityDataSetProperties(TrackPointValueProvider xValueProvider) {
            super(R.string.hor_velocity_label,
                    R.color.horVelocity,
                    R.string.kmh,
                    YAxis.AxisDependency.LEFT,
                    VELOCITY_FORMAT,
                    R.id.hor_velocity_marker_text_view,
                    RANGE_AVERAGE,
                    xValueProvider,
                    HOR_VELOCITY_VALUE_PROVIDER);
        }
    }

    public static class VerticalVelocityDataSetProperties extends ChartDataSetProperties {
        public VerticalVelocityDataSetProperties(TrackPointValueProvider xValueProvider) {
            super(R.string.vert_velocity_label,
                    R.color.vertVelocity,
                    R.string.kmh,
                    YAxis.AxisDependency.LEFT,
                    VELOCITY_FORMAT,
                    R.id.vert_velocity_marker_text_view,
                    RANGE_AVERAGE,
                    xValueProvider,
                    VERT_VELOCITY_VALUE_PROVIDER);
        }
    }

    public static class AltitudeDataSetProperties extends ChartDataSetProperties {
        public AltitudeDataSetProperties(TrackPointValueProvider xValueProvider) {
            super(R.string.altitude_label,
                    R.color.altitude,
                    R.string.m,
                    YAxis.AxisDependency.RIGHT,
                    DISTANCE_FORMAT,
                    R.id.altitude_marker_text_view,
                    RANGE_DIFFERENTIAL,
                    xValueProvider,
                    ALTITUDE_VALUE_PROVIDER);
        }
    }

    public static class GlideDataSetProperties extends ChartDataSetProperties {
        public GlideDataSetProperties(TrackPointValueProvider xValueProvider) {
            super(R.string.glide_label,
                    R.color.glide,
                    R.string.glide,
                    YAxis.AxisDependency.LEFT,
                    GLIDE_FORMAT,
                    R.id.glide_marker_text_view,
                    RANGE_AVERAGE,
                    xValueProvider,
                    GLIDE_VALUE_PROVIDER);
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
                    RANGE_DIFFERENTIAL,
                    xValueProvider,
                    TIME_VALUE_PROVIDER);
        }

        @Override
        public void initLineData(Context context, FlySightTrackData flySightTrackData) {
            //Y axis data! dont init linedata!
        }
    }


    public static class DistanceDataSetProperties extends ChartDataSetProperties {
        public DistanceDataSetProperties(TrackPointValueProvider xValueProvider) {
            super(R.string.distance_label,
                    R.color.distance,
                    R.string.m,
                    YAxis.AxisDependency.RIGHT,
                    DISTANCE_FORMAT,
                    R.id.distance_marker_text_view,
                    RANGE_DIFFERENTIAL,
                    xValueProvider,
                    DISTANCE_VALUE_PROVIDER);
        }

        @Override
        public void initLineData(Context context, FlySightTrackData flySightTrackData) {
            //Y axis data! dont init linedata!
        }
    }

    public static class DistanceVsAltitudeDataSetProperties extends ChartDataSetProperties {
        public DistanceVsAltitudeDataSetProperties() {
            super(R.string.distance_altitude_label,
                    R.color.distanceAltitude,
                    R.string.m,
                    YAxis.AxisDependency.LEFT,
                    DISTANCE_FORMAT,
                    R.id.distance_marker_text_view,
                    RANGE_DIFFERENTIAL,
                    DISTANCE_VALUE_PROVIDER,
                    ALTITUDE_VALUE_PROVIDER);
        }
    }
}
