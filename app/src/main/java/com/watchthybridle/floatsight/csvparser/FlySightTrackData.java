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
import android.util.Log;

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
    private int parsingErrorCount = 0;

    private List<String> time;
    private List<Entry> horVelocity;
    private List<Entry> vertVelocity;
    private List<Entry> altitude;
    private List<Entry> glide;
    private List<Entry> distance;

    public FlySightTrackData() {
        time = new ArrayList<>();
        horVelocity = new ArrayList<>();
        vertVelocity = new ArrayList<>();
        altitude = new ArrayList<>();
        glide = new ArrayList<>();
        distance = new ArrayList<>();
    }

    public long getParsingStatus() {
        return parsingStatus;
    }

    public void setParsingStatus(@ParsingResult long parsingStatus) {
        this.parsingStatus = parsingStatus;
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

    //0   ,1  ,2  ,3   ,4   ,5   ,6   ,7   ,8   ,9   ,10     ,11  ,12    ,13
    //time,lat,lon,hMSL,velN,velE,velD,hAcc,vAcc,sAcc,heading,cAcc,gpsFix,numSV
    protected void addCsvLine(String csvLine) {
        String[] row = csvLine.split(",");

        try {
            if (row.length != 14 &&
                    !row[0].matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{2}Z")) {
                    throw new NumberFormatException();
            }
            String time = row[0];
            Float vertVelocity = Float.parseFloat(row[6]) * 3.6f;
            Float altitude = Float.parseFloat(row[3]);

            Double velocityNorth = Double.parseDouble(row[4]) * 3.6f;
            Double velocityEast = Double.parseDouble(row[5]) * 3.6f;
            Float horVelocity = calculateHorizontalVelocity(velocityNorth, velocityEast);
            Float glide = calculateGlide(horVelocity, vertVelocity);

            int entryPosition = this.time.size();
            this.time.add(time);
            this.horVelocity.add(new Entry(entryPosition, horVelocity));
            this.vertVelocity.add(new Entry(entryPosition,vertVelocity));
            this.altitude.add(new Entry(entryPosition, altitude));
            this.glide.add(new Entry(entryPosition, glide));
        } catch (NumberFormatException e) {
            parsingErrorCount++;
            if(parsingErrorCount > 2) {
                parsingStatus = PARSING_ERRORS;
            }
            Log.d(FlySightTrackData.class.getSimpleName(),
                    "cant read line from csv somewhere around time:" + row[0]
                            + ", around line number:" + time.size() + 1 + ", therefore skipped.");
        }
    }

    private Float calculateHorizontalVelocity(Double velocityNorth, Double velocityEast) {
        Double glide = Math.sqrt(Math.pow(velocityNorth, 2) + Math.pow(velocityEast, 2));
        return glide.floatValue();
    }

    private Float calculateGlide(Float horVelocity, Float vertVelocity) {
        Float glide = 0f;
        Float minVertVelocity = 0.1f;

        if (vertVelocity > minVertVelocity) {
            glide = horVelocity / vertVelocity;
        }

        Float capGlideAt = 5f;
        return glide > capGlideAt ? capGlideAt : glide;
    }

    public boolean isAnyMetricEmpty() {
        return time.isEmpty() || vertVelocity.isEmpty() || horVelocity.isEmpty() || altitude.isEmpty() || glide.isEmpty();
    }
}
