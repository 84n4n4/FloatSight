package com.watchthybridle.floatsight.mpandroidchart.linedatasetcreation;

import com.watchthybridle.floatsight.csvparser.FlySightTrackPoint;

public class CappedTrackPointValueProvider implements TrackPointValueProvider {
    private float capYValueAt;
    private TrackPointValueProvider valueProvider;
    public static final String BUNDLE_KEY = "CappedTrackPointValueProvider";

    public CappedTrackPointValueProvider(TrackPointValueProvider trackPointValueProvider, float capYValueAt) {
        this.capYValueAt = capYValueAt;
        this.valueProvider = trackPointValueProvider;
    }

    @Override
    public float getValue(FlySightTrackPoint trackPoint) {
        return valueProvider.getValue(trackPoint) < capYValueAt ? valueProvider.getValue(trackPoint) : capYValueAt;
    }

    public float getCapYValueAt() {
        return capYValueAt;
    }
}
