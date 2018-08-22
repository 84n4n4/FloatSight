package com.watchthybridle.floatsight.linedatasetcreation;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.watchthybridle.floatsight.R;
import com.watchthybridle.floatsight.csvparser.FlySightTrackData;

import java.text.DecimalFormat;
import java.util.List;

public class ChartDataSetProperties {
    public static final DecimalFormat VELOCITY_FORMAT = new DecimalFormat("#0");
    public static final DecimalFormat DISTANCE_FORMAT = new DecimalFormat("###0");
    public static final DecimalFormat GLIDE_FORMAT = new DecimalFormat("#0.00");

    public @IdRes int markerTextView;
    public @StringRes int labelStringResource;
    public @StringRes int unitStringResource;
    public @ColorRes int color;
    public DecimalFormat decimalFormat;
    public YAxis.AxisDependency axisDependency;
    public LineDataSet iLineDataSet;

    private ChartDataSetProperties(@StringRes int labelStringResource,
                                   @ColorRes int color,
                                   @StringRes int unitStringResource,
                                   YAxis.AxisDependency axisDependency,
                                   DecimalFormat decimalFormat,
                                   @IdRes int markerTextView,
                                   List<Entry> data,
                                   Context context) {
        this.markerTextView = markerTextView;
        this.labelStringResource = labelStringResource;
        this.color = color;
        this.decimalFormat = decimalFormat;
        this.axisDependency = axisDependency;
        this.unitStringResource = unitStringResource;
        init(context, data);
    }

    private void init(Context context, List<Entry> data) {
        iLineDataSet = new LineDataSet(data, context.getString(labelStringResource));
        iLineDataSet.setColor(ContextCompat.getColor(context, color));
        iLineDataSet.setValueTextColor(ContextCompat.getColor(context, color));
        iLineDataSet.setAxisDependency(axisDependency);
        iLineDataSet.setDrawValues(false);
        iLineDataSet.setDrawCircles(false);
    }

    public String getFormattedValueForPosition(Context context, float xPosition) {
        Entry entry = iLineDataSet.getEntriesForXValue(xPosition).get(0);
        return context.getString(unitStringResource, decimalFormat.format(entry.getY()));
    }

    public static ChartDataSetProperties getHorizontalVelocityProperties(Context context, FlySightTrackData flySightTrackData) {
        return new ChartDataSetProperties(
                R.string.hor_velocity_label,
                R.color.horVelocity,
                R.string.kmh,
                YAxis.AxisDependency.LEFT,
                VELOCITY_FORMAT,
                R.id.hor_velocity_marker_text_view,
                flySightTrackData.getHorVelocity(),
                context);
    }

    public static ChartDataSetProperties getVerticalVelocityProperties(Context context, FlySightTrackData flySightTrackData) {
        return new ChartDataSetProperties(
                 R.string.vert_velocity_label,
                 R.color.vertVelocity,
                 R.string.kmh,
                 YAxis.AxisDependency.LEFT,
                 VELOCITY_FORMAT,
                 R.id.vert_velocity_marker_text_view,
                 flySightTrackData.getVertVelocity(),
                 context);
    }

    public static ChartDataSetProperties getAltitudeVsTimeProperties(Context context, FlySightTrackData flySightTrackData) {
        return new ChartDataSetProperties(
                R.string.altitude_label,
                R.color.altitude,
                R.string.m,
                YAxis.AxisDependency.RIGHT,
                DISTANCE_FORMAT,
                R.id.altitude_marker_text_view,
                flySightTrackData.getAltitude(),
                context);
    }

    public static ChartDataSetProperties getDistanceVsTimeProperties(Context context, FlySightTrackData flySightTrackData) {
        return new ChartDataSetProperties(
                R.string.distance_label,
                R.color.distance,
                R.string.m,
                YAxis.AxisDependency.RIGHT,
                DISTANCE_FORMAT,
                R.id.distance_marker_text_view,
                flySightTrackData.getDistance(),
                context);
    }

    public static ChartDataSetProperties getGlideProperties(Context context, FlySightTrackData flySightTrackData) {
        return new ChartDataSetProperties(
                R.string.glide_label,
                R.color.glide,
                R.string.none,
                YAxis.AxisDependency.LEFT,
                GLIDE_FORMAT,
                R.id.glide_marker_text_view,
                flySightTrackData.getGlide(),
                context);
    }

    public static ChartDataSetProperties getDistanceVsAltitudeProperties(Context context, FlySightTrackData flySightTrackData) {
        return new ChartDataSetProperties(
                R.string.distance_altitude_label,
                R.color.distanceAltitude,
                R.string.m,
                YAxis.AxisDependency.LEFT,
                DISTANCE_FORMAT,
                R.id.distance_marker_text_view,
                flySightTrackData.getDistanceVsAltitude(),
                context);
    }
}
