package com.watchthybridle.floatsight.linedatasetcreation;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.util.Pair;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.watchthybridle.floatsight.R;
import com.watchthybridle.floatsight.csvparser.FlySightTrackData;
import com.watchthybridle.floatsight.csvparser.FlySightTrackPoint;

import java.lang.annotation.Retention;
import java.text.DecimalFormat;
import java.util.List;

import static java.lang.annotation.RetentionPolicy.SOURCE;

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
    public @RangeInfo int rangeInfo;
    public FlySightTrackPointValueProvider xValueProvider;
    public FlySightTrackPointValueProvider yValueProvider;

    public static final FlySightTrackPointValueProvider TIME_VALUE_PROVIDER =
            (FlySightTrackPoint trackPoint)-> trackPoint.trackTimeInSeconds;

    public static final FlySightTrackPointValueProvider DISTANCE_VALUE_PROVIDER =
            (FlySightTrackPoint trackPoint)-> trackPoint.distance;

    @Retention(SOURCE)
    @IntDef({RANGE_AVERAGE, RANGE_DIFFERENTIAL})
    @interface RangeInfo {}
    public static final int RANGE_AVERAGE = 0;
    public static final int RANGE_DIFFERENTIAL = 1;

    public ChartDataSetProperties(@StringRes int labelStringResource,
                                   @ColorRes int color,
                                   @StringRes int unitStringResource,
                                   YAxis.AxisDependency axisDependency,
                                   DecimalFormat decimalFormat,
                                   @IdRes int markerTextView,
                                   @RangeInfo int rangeInfo) {
        this.markerTextView = markerTextView;
        this.labelStringResource = labelStringResource;
        this.color = color;
        this.decimalFormat = decimalFormat;
        this.axisDependency = axisDependency;
        this.unitStringResource = unitStringResource;
        this.rangeInfo = rangeInfo;
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

    public String getFormattedValueForPosition(Context context, float xPosition) {
        Entry entry = iLineDataSet.getEntriesForXValue(xPosition).get(0);
        return context.getString(unitStringResource, decimalFormat.format(entry.getY()));
    }

    public String getFormattedValueForRange(Context context, float start, float end) {
        float rangeValue;

        switch(rangeInfo) {
            case RANGE_AVERAGE:
                rangeValue = calculateArithmeticAverage(start, end);
                break;
            case RANGE_DIFFERENTIAL:
                rangeValue = calculateDiff(start, end);
                break;
            default:
                rangeValue = 0;
                break;
        }
        return context.getString(unitStringResource, decimalFormat.format(rangeValue));
    }

    private float calculateDiff(float start, float end) {
        Entry startEntry = iLineDataSet.getEntryForXValue(start, 0f);
        Entry endEntry = iLineDataSet.getEntryForXValue(end, 0f);
        return endEntry.getY() - startEntry.getY();
    }

    private float calculateArithmeticAverage(float start, float end) {
        Entry startEntry = iLineDataSet.getEntryForXValue(start, 0f);
        Entry endEntry = iLineDataSet.getEntryForXValue(end, 0f);

        List<Entry> entries = iLineDataSet.getValues().subList(iLineDataSet.getEntryIndex(startEntry), iLineDataSet.getEntryIndex(endEntry));
        float sum = 0;
        float count = entries.size();
        for (Entry entry : entries) {
            sum += entry.getY();
        }
        return sum / count;
    }


    public Pair<Entry, Entry> getMinMaxYEntries() {
        Entry min = iLineDataSet.getEntryForIndex(0);
        Entry max = iLineDataSet.getEntryForIndex(0);

        for(int index = 0; index < iLineDataSet.getEntryCount(); index ++) {
            Entry altitude = iLineDataSet.getEntryForIndex(index);
            if (altitude.getY() > max.getY()) {
                max = altitude;
            }
            if (altitude.getY() < min.getY()) {
                min = altitude;
            }
        }
        return new Pair<>(min, max);
    }

    public static class HorizontalVelocityDataSetProperties extends ChartDataSetProperties {
        public HorizontalVelocityDataSetProperties() {
            super(R.string.hor_velocity_label,
                    R.color.horVelocity,
                    R.string.kmh,
                    YAxis.AxisDependency.LEFT,
                    VELOCITY_FORMAT,
                    R.id.hor_velocity_marker_text_view,
                    RANGE_AVERAGE);
            yValueProvider = (FlySightTrackPoint trackPoint) -> trackPoint.horVelocity;
            xValueProvider = TIME_VALUE_PROVIDER;
        }
    }

    public static class VerticalVelocityDataSetProperties extends ChartDataSetProperties {
        public VerticalVelocityDataSetProperties() {
            super(R.string.vert_velocity_label,
                    R.color.vertVelocity,
                    R.string.kmh,
                    YAxis.AxisDependency.LEFT,
                    VELOCITY_FORMAT,
                    R.id.vert_velocity_marker_text_view,
                    RANGE_AVERAGE);
            yValueProvider = (FlySightTrackPoint trackPoint) -> trackPoint.vertVelocity;
            xValueProvider = TIME_VALUE_PROVIDER;
        }
    }

    public static class AltitudeVsTimeDataSetProperties extends ChartDataSetProperties {
        public AltitudeVsTimeDataSetProperties() {
            super(R.string.altitude_label,
                    R.color.altitude,
                    R.string.m,
                    YAxis.AxisDependency.RIGHT,
                    DISTANCE_FORMAT,
                    R.id.altitude_marker_text_view,
                    RANGE_DIFFERENTIAL);
            yValueProvider = (FlySightTrackPoint trackPoint) -> trackPoint.altitude;
            xValueProvider = TIME_VALUE_PROVIDER;
        }
    }

    public static class DistanceVsTimeDataSetProperties extends ChartDataSetProperties {
        public DistanceVsTimeDataSetProperties() {
            super(R.string.distance_label,
                    R.color.distance,
                    R.string.m,
                    YAxis.AxisDependency.RIGHT,
                    DISTANCE_FORMAT,
                    R.id.distance_marker_text_view,
                    RANGE_DIFFERENTIAL);
            yValueProvider = (FlySightTrackPoint trackPoint) -> trackPoint.distance;
            xValueProvider = TIME_VALUE_PROVIDER;
        }
    }

    public static class GlideVsTimeDataSetProperties extends ChartDataSetProperties {
        public GlideVsTimeDataSetProperties() {
            super(R.string.glide_label,
                    R.color.glide,
                    R.string.glide,
                    YAxis.AxisDependency.LEFT,
                    GLIDE_FORMAT,
                    R.id.glide_marker_text_view,
                    RANGE_AVERAGE);
            yValueProvider = (FlySightTrackPoint trackPoint) -> trackPoint.glide;
            xValueProvider = TIME_VALUE_PROVIDER;
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
                    RANGE_DIFFERENTIAL);
            yValueProvider = (FlySightTrackPoint trackPoint) -> trackPoint.altitude;
            xValueProvider = DISTANCE_VALUE_PROVIDER;
        }
    }
}
