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

import android.support.annotation.IntDef;
import android.support.annotation.LongDef;
import android.support.annotation.NonNull;
import android.util.Log;
import com.github.mikephil.charting.data.Entry;

import java.lang.annotation.Retention;
import java.util.ArrayList;
import java.util.List;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public class FlySightTrackData {
    public static final String FLYSIGHT_CSV_HEADER = "time,lat,lon,hMSL,velN,velE,velD,hAcc,vAcc,sAcc,heading,cAcc,gpsFix,numSV";
    public static final String FLYSIGHT_CSV_HEADER_UNITS = ",(deg),(deg),(m),(m/s),(m/s),(m/s),(m),(m),(m/s),(deg),(deg),,";

    @Retention(SOURCE)
    @LongDef({PARSING_SUCCESS, PARSING_FAIL, PARSING_ERRORS})
    public @interface ParsingResult {}
    public static final long PARSING_SUCCESS = 0;
    public static final long PARSING_FAIL = -1;
    public static final long PARSING_ERRORS = 1;

    private long parsingStatus = PARSING_SUCCESS;

    private List<String> time;
    private List<Point> position;
    private List<Entry> horVelocity;
    private List<Entry> vertVelocity;
    private List<Entry> altitude;
    private List<Entry> glide;
    private List<Entry> distance;
    private List<Entry> distanceVsAltitude;
    private Point firstValidPoint = new Point(0,0, Point.INVALID);

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

    //0   ,1  ,2  ,3   ,4   ,5   ,6   ,7   ,8   ,9   ,10     ,11  ,12    ,13
    //time,lat,lon,hMSL,velN,velE,velD,hAcc,vAcc,sAcc,heading,cAcc,gpsFix,numSV
    void addCsvRow(String csvRow) {
        if (csvRow.equals(FLYSIGHT_CSV_HEADER) || csvRow.equals(FLYSIGHT_CSV_HEADER_UNITS)) {
            return;
        }

        String[] row = csvRow.split(",");
        try {
            if (row.length != 14 ||
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
            Point point = new Point(Double.parseDouble(row[1]), Double.parseDouble(row[2]), Point.VALID);
            Float distance = 0f;
            if (firstValidPoint.valid == Point.VALID){
                distance = calculateHaversinDistanceMeters(firstValidPoint, point);
            } else {
                firstValidPoint = point;
            }

            int entryPosition = this.time.size();
            this.time.add(time);
            this.position.add(point);
            this.horVelocity.add(new Entry(entryPosition, horVelocity));
            this.vertVelocity.add(new Entry(entryPosition,vertVelocity));
            this.altitude.add(new Entry(entryPosition, altitude));
            this.glide.add(new Entry(entryPosition, glide));
            this.distance.add(new Entry(entryPosition, distance));
            this.distanceVsAltitude.add(new Entry(distance, altitude));
        } catch (Exception e) {
            handleLineParsingError();
        }
    }

    private void handleLineParsingError() {
        parsingStatus = PARSING_ERRORS;
        String timeStamp = "start";
        if(!time.isEmpty()){
            timeStamp = time.get(time.size() - 1);
        }

        int entryPosition = time.size();
        time.add("invalid");
        position.add(new Point(0,0, Point.INVALID));
        horVelocity.add(new Entry(entryPosition, 0f));
        vertVelocity.add(new Entry(entryPosition, 0f));
        altitude.add(new Entry(entryPosition, 0f));
        glide.add(new Entry(entryPosition, 0f));
        distance.add(new Entry(entryPosition, 0f));

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

    @NonNull
    private Float calculateHaversinDistanceMeters(Point pointA, Point pointB) {
        if(pointA.valid == Point.INVALID || pointB.valid == Point.INVALID) {
            return 0f;
        }
        double EARTH_RADIUS = 6371000;
        double dLat = Math.toRadians(pointB.lat - pointA.lat);
        double dLon = Math.toRadians(pointB.lon - pointA.lon);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLon / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(pointA.lat)) * Math.cos(Math.toRadians(pointB.lat));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        Double distance = EARTH_RADIUS * c;
        return distance.floatValue();
    }

    @Retention(SOURCE)
    @IntDef({Point.INVALID, Point.VALID})
    @interface ValidPoint {}

    class Point {
        public static final int INVALID = 0;
        public static final int VALID = 1;
        final double lat;
        final double lon;
        final int valid;
        Point(double lat, double lon, @ValidPoint int valid ) {
            this.lat = lat;
            this.lon = lon;
            this.valid = valid;
        }
    }

    public boolean isAnyMetricEmpty() {
        return time.isEmpty() || vertVelocity.isEmpty() || horVelocity.isEmpty() || altitude.isEmpty() || glide.isEmpty();
    }
}
