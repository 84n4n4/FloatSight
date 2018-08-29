package com.watchthybridle.floatsight.linedatasetcreation;

import com.watchthybridle.floatsight.csvparser.FlySightTrackPoint;
import com.watchthybridle.floatsight.data.FlySightTrackData;

interface RangeValueStrategy {
    float getRangeValue(float start, float end,
                        TrackPointValueProvider xValueProvider,
                        TrackPointValueProvider yValueProvider,
                        FlySightTrackData flySightTrackData);

    RangeValueStrategy RANGE_DIFFERENTIAL = new RangeValueStrategy() {
        @Override
        public float getRangeValue(float start, float end,
                                   TrackPointValueProvider xValueProvider,
                                   TrackPointValueProvider yValueProvider,
                                   FlySightTrackData flySightTrackData) {
            FlySightTrackPoint startPoint = null;
            FlySightTrackPoint endPoint = null;
            for (FlySightTrackPoint trackPoint : flySightTrackData.getFlySightTrackPoints()) {
                if (xValueProvider.getValue(trackPoint) == start) {
                    startPoint = trackPoint;
                }
                if (xValueProvider.getValue(trackPoint) == end) {
                    endPoint = trackPoint;
                }
                if (startPoint != null && endPoint != null) {
                    break;
                }
            }
            if(startPoint == null || endPoint == null) {
                return 0;
            }
            return yValueProvider.getValue(endPoint) - yValueProvider.getValue(startPoint);
        }
    };

    RangeValueStrategy RANGE_AVERAGE = new RangeValueStrategy() {
        @Override
        public float getRangeValue(float start, float end,
                                   TrackPointValueProvider xValueProvider,
                                   TrackPointValueProvider yValueProvider,
                                   FlySightTrackData flySightTrackData) {
            float sum = 0;
            int entryCount = 0;
            for (FlySightTrackPoint trackPoint : flySightTrackData.getFlySightTrackPoints()) {
                if (xValueProvider.getValue(trackPoint) >= start) {
                    sum += yValueProvider.getValue(trackPoint);
                    entryCount++;
                }
                if (xValueProvider.getValue(trackPoint) >= end) {
                    break;
                }
            }
            return sum / entryCount;
        }
    };
}

