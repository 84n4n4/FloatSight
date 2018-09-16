package org.floatcast.floatsight.mpandroidchart.linedatasetcreation;

import org.floatcast.floatsight.csvparser.FlySightTrackPoint;

public class CappedTrackPointValueProvider implements TrackPointValueProvider {
    private final float capYValueAt;
    private final TrackPointValueProvider valueProvider;
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
