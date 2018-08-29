package com.watchthybridle.floatsight.linedatasetcreation;

import com.watchthybridle.floatsight.csvparser.FlySightTrackPoint;

public interface TrackPointValueProvider {
    float getValue(FlySightTrackPoint trackPoint);

    TrackPointValueProvider TIME_VALUE_PROVIDER =
            (FlySightTrackPoint trackPoint) -> trackPoint.trackTimeInSeconds;

    TrackPointValueProvider DISTANCE_VALUE_PROVIDER =
            (FlySightTrackPoint trackPoint) -> trackPoint.distance;

    TrackPointValueProvider ALTITUDE_VALUE_PROVIDER =
            (FlySightTrackPoint trackPoint) -> trackPoint.altitude;

    TrackPointValueProvider HOR_VELOCITY_VALUE_PROVIDER =
            (FlySightTrackPoint trackPoint) -> trackPoint.horVelocity;

    TrackPointValueProvider VERT_VELOCITY_VALUE_PROVIDER =
            (FlySightTrackPoint trackPoint) -> trackPoint.vertVelocity;

    TrackPointValueProvider GLIDE_VALUE_PROVIDER =
            (FlySightTrackPoint trackPoint) -> trackPoint.glide;
}
