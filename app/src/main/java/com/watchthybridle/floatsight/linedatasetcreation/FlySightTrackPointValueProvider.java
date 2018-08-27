package com.watchthybridle.floatsight.linedatasetcreation;

import com.watchthybridle.floatsight.csvparser.FlySightTrackPoint;

public interface FlySightTrackPointValueProvider {
    float getValue(FlySightTrackPoint trackPoint);
}
