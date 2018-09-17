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
