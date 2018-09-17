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

@SuppressWarnings("PMD.FieldDeclarationsShouldBeAtStartOfClass")
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
