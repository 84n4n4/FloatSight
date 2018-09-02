package com.watchthybridle.floatsight.mpandroidchart.linedatasetcreation;

import com.watchthybridle.floatsight.csvparser.FlySightTrackPoint;

public interface TrackPointValueProvider {
    float getValue(FlySightTrackPoint trackPoint);

    TrackPointValueProvider TIME_VALUE_PROVIDER =
            (FlySightTrackPoint trackPoint) -> trackPoint.trackTimeInSeconds;

    TrackPointValueProvider METRIC_DISTANCE_VALUE_PROVIDER =
            (FlySightTrackPoint trackPoint) -> trackPoint.distance;

    TrackPointValueProvider METRIC_ALTITUDE_VALUE_PROVIDER =
            (FlySightTrackPoint trackPoint) -> trackPoint.altitude;

    TrackPointValueProvider METRIC_HOR_VELOCITY_VALUE_PROVIDER =
            (FlySightTrackPoint trackPoint) -> trackPoint.horVelocity;

    TrackPointValueProvider METRIC_VERT_VELOCITY_VALUE_PROVIDER =
            (FlySightTrackPoint trackPoint) -> trackPoint.vertVelocity;

    TrackPointValueProvider IMPERIAL_DISTANCE_VALUE_PROVIDER =
            (FlySightTrackPoint trackPoint) -> trackPoint.distance * 3.28084f;

    TrackPointValueProvider IMPERIAL_ALTITUDE_VALUE_PROVIDER =
            (FlySightTrackPoint trackPoint) -> trackPoint.altitude * 3.28084f;

    TrackPointValueProvider IMPERIAL_HOR_VELOCITY_VALUE_PROVIDER =
            (FlySightTrackPoint trackPoint) -> trackPoint.horVelocity * 0.621371f;

    TrackPointValueProvider IMPERIAL_VERT_VELOCITY_VALUE_PROVIDER =
            (FlySightTrackPoint trackPoint) -> trackPoint.vertVelocity * 0.621371f;

    TrackPointValueProvider GLIDE_VALUE_PROVIDER =
            (FlySightTrackPoint trackPoint) -> trackPoint.glide;
}
