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

package com.watchthybridle.floatsight.csvparser;

import android.support.annotation.LongDef;
import com.github.mikephil.charting.data.Entry;

import java.lang.annotation.Retention;
import java.util.ArrayList;
import java.util.List;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public class FlySightTrackData {

    @Retention(SOURCE)
    @LongDef({PARSING_SUCCESS, PARSING_FAIL, PARSING_ERRORS})
    public @interface ParsingResult {}
    public static final long PARSING_SUCCESS = 0;
    public static final long PARSING_FAIL = -1;
    public static final long PARSING_ERRORS = 1;

    private long parsingStatus = PARSING_SUCCESS;

    private String sourceFileName = "";

    private List<FlySightTrackPoint> flySightTrackPoints;

    public FlySightTrackData() {
        flySightTrackPoints = new ArrayList<>();
    }

    public List<FlySightTrackPoint> getFlySightTrackPoints() {
        return flySightTrackPoints;
    }

    public long getParsingStatus() {
        return parsingStatus;
    }

    public void setParsingStatus(@ParsingResult long parsingStatus) {
        this.parsingStatus = parsingStatus;
    }

    public List<Entry> getHorVelocityVTime() {
        List<Entry> entries = new ArrayList<>();
        for (FlySightTrackPoint trackPoint : flySightTrackPoints) {
            entries.add(new Entry(trackPoint.trackTimeInSeconds, trackPoint.horVelocity));
        }
        return entries;
    }

    public List<Entry> getVertVelocityVTime() {
        List<Entry> entries = new ArrayList<>();
        for (FlySightTrackPoint trackPoint : flySightTrackPoints) {
            entries.add(new Entry(trackPoint.trackTimeInSeconds, trackPoint.vertVelocity));
        }
        return entries;
    }

    public List<Entry> getAltitudeVTime() {
        List<Entry> entries = new ArrayList<>();
        for (FlySightTrackPoint trackPoint : flySightTrackPoints) {
            entries.add(new Entry(trackPoint.trackTimeInSeconds, trackPoint.altitude));
        }
        return entries;
    }

    public List<Entry> getGlideVTime() {
        List<Entry> entries = new ArrayList<>();
        for (FlySightTrackPoint trackPoint : flySightTrackPoints) {
            entries.add(new Entry(trackPoint.trackTimeInSeconds, trackPoint.glide));
        }
        return entries;
    }

    public List<Entry> getDistanceVTime() {
        List<Entry> entries = new ArrayList<>();
        for (FlySightTrackPoint trackPoint : flySightTrackPoints) {
            entries.add(new Entry(trackPoint.trackTimeInSeconds, trackPoint.distance));
        }
        return entries;
    }

    public List<Entry> getDistanceVsAltitude() {
        List<Entry> entries = new ArrayList<>();
        for (FlySightTrackPoint trackPoint : flySightTrackPoints) {
            entries.add(new Entry(trackPoint.distance, trackPoint.altitude));
        }
        return entries;
    }

    public String getSourceFileName() {
        return sourceFileName;
    }

    public void setSourceFileName(String sourceFileName) {
        this.sourceFileName = sourceFileName;
    }
}
