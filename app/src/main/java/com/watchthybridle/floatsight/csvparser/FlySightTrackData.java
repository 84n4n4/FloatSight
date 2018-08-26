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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

    private List<String> time;
    private List<GPSCoordinate> position;
    private List<Entry> horVelocity;
    private List<Entry> vertVelocity;
    private List<Entry> altitude;
    private List<Entry> glide;
    private List<Entry> distance;
    private List<Entry> distanceVsAltitude;

    public FlySightTrackData() {
        time = new ArrayList<>();
        position = new ArrayList<>();
        horVelocity = new ArrayList<>();
        vertVelocity = new ArrayList<>();
        altitude = new ArrayList<>();
        glide = new ArrayList<>();
        distance = new ArrayList<>();
        distanceVsAltitude = new ArrayList<>();
    }

    public float calculateTimeDiffSec(int start, int end) {
        return calculateTimeDiffSec(time.get(start), time.get(end));
    }

    //2018-08-12T14:25:43.07Z
    private float calculateTimeDiffSec(String startTimeStamp, String endTimeStamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        try {
            Date start = dateFormat.parse(startTimeStamp.replace("Z","0Z"));
            Date end = dateFormat.parse(endTimeStamp.replace("Z","0Z"));
            return (end.getTime() - start.getTime()) / 1000f ;
        } catch (ParseException exception) {
            return 0;
        }
    }

    public long getParsingStatus() {
        return parsingStatus;
    }

    public void setParsingStatus(@ParsingResult long parsingStatus) {
        this.parsingStatus = parsingStatus;
    }

    public List<String> getTimeStamps() {
        return time;
    }

    public List<Entry> getHorVelocity() {
        return horVelocity;
    }

    public List<Entry> getVertVelocity() {
        return vertVelocity;
    }

    public List<Entry> getAltitude() {
        return altitude;
    }

    public List<Entry> getGlide() {
        return glide;
    }

    public List<Entry> getDistance() {
        return distance;
    }

    public List<Entry> getDistanceVsAltitude() {
        return distanceVsAltitude;
    }

    public List<GPSCoordinate> getPositions() {
        return position;
    }

    public boolean isAnyMetricEmpty() {
        return time.isEmpty() || vertVelocity.isEmpty() || horVelocity.isEmpty() || altitude.isEmpty() || glide.isEmpty();
    }

    public String getSourceFileName() {
        return sourceFileName;
    }

    public void setSourceFileName(String sourceFileName) {
        this.sourceFileName = sourceFileName;
    }
}
