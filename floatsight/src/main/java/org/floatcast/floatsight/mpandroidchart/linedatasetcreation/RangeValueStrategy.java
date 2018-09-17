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

import org.floatcast.floatsight.csvparser.FlySightTrackPoint;
import org.floatcast.floatsight.data.FlySightTrackData;

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

