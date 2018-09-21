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

package org.floatcast.floatsight.data;

import com.github.mikephil.charting.data.Entry;
import org.floatcast.floatsight.csvparser.FlySightTrackPoint;
import org.floatcast.floatsight.mpandroidchart.linedatasetcreation.TrackPointValueProvider;

import java.util.ArrayList;
import java.util.List;

public class FlySightTrackData extends ParsableData {

    private final List<FlySightTrackPoint> flySightTrackPoints;
    private boolean preV20141005 = false;

    public FlySightTrackData() {
        flySightTrackPoints = new ArrayList<>();
    }

    public FlySightTrackData(List<FlySightTrackPoint> flySightTrackPoints, FlySightTrackData flySightTrackData) {
        this.flySightTrackPoints = flySightTrackPoints;
        setSourceFileName(flySightTrackData.getSourceFileName());
        setParsingStatus(PARSING_SUCCESS);
        setDirty(true);
    }

    public void setPreV20141005(boolean preV20141005) {
        this.preV20141005 = preV20141005;
    }

    public boolean isPreV20141005() {
        return preV20141005;
    }

    public List<FlySightTrackPoint> getFlySightTrackPoints() {
        return flySightTrackPoints;
    }

    public List<Entry> getEntries(TrackPointValueProvider xValueProvider, TrackPointValueProvider yValueProvider) {
        List<Entry> entries = new ArrayList<>();
        for (FlySightTrackPoint trackPoint : flySightTrackPoints) {
            entries.add(new Entry(xValueProvider.getValue(trackPoint), yValueProvider.getValue(trackPoint)));
        }
        return entries;
    }

    public void offsetDistanceAndTime(float distanceOffset, float timeOffset) {
        for(FlySightTrackPoint point : flySightTrackPoints) {
            point.trackTimeInSeconds = point.trackTimeInSeconds - timeOffset;
            point.distance = point.distance - distanceOffset;
        }
    }

    public float distanceSurpassesAltitude() {
        for(FlySightTrackPoint point : flySightTrackPoints) {
            float altitudeDiff = flySightTrackPoints.get(0).altitude - point.altitude;
            if(point.distance > altitudeDiff && point.vertVelocity > 10f) {
                return point.distance;
            }
        }
        return 0;
    }
}
