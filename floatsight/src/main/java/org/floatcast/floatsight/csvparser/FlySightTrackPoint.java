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

package org.floatcast.floatsight.csvparser;

public class FlySightTrackPoint {
    public String csvRow;

    public long unixTimeStamp;
    public float trackTimeInSeconds;
    public GPSCoordinate position;

    public float horVelocity;
    public float vertVelocity;
    public float altitude;
    public float glide;
    public float distance;

    public float velE;
    public float velN;
    public float hAcc;
    public float vAcc;
    public float sAcc;
    public float heading;
    public float cAcc;
    public int gpsFix;
    public int numSV;

    public FlySightTrackPoint() {
    }
}
