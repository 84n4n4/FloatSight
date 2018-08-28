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

import com.watchthybridle.floatsight.Parser;
import com.watchthybridle.floatsight.data.FlySightTrackData;
import org.apache.commons.lang3.time.DateParser;
import org.apache.commons.lang3.time.FastDateFormat;

import java.io.*;
import java.text.ParseException;
import java.util.Date;

import static com.watchthybridle.floatsight.data.FlySightTrackData.PARSING_ERRORS;

public class FlySightCsvParser implements Parser<FlySightTrackData> {

    public static final String FLYSIGHT_CSV_HEADER = "time,lat,lon,hMSL,velN,velE,velD,hAcc,vAcc,sAcc,heading,cAcc,gpsFix,numSV";
    public static final String FLYSIGHT_CSV_HEADER_UNITS = ",(deg),(deg),(m),(m/s),(m/s),(m/s),(m),(m),(m/s),(deg),(deg),,";
    private static final DateParser DATE_PARSER = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    @Override
    public FlySightTrackData read(InputStream inputStream) throws IOException {
        FlySightTrackData flySightTrackData = new FlySightTrackData();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine;

            while ((csvLine = reader.readLine()) != null) {
                addCsvRow(flySightTrackData, csvLine);
            }
        } finally {
            inputStream.close();
        }
        return flySightTrackData;
    }

    @Override
    public void write(OutputStream outputStream, FlySightTrackData data) throws IOException {
        // not implemented for CSV parser.
    }

    //0   ,1  ,2  ,3   ,4   ,5   ,6   ,7   ,8   ,9   ,10     ,11  ,12    ,13
    //time,lat,lon,hMSL,velN,velE,velD,hAcc,vAcc,sAcc,heading,cAcc,gpsFix,numSV
    void addCsvRow(FlySightTrackData flySightTrackData, String csvRow) {
        if (csvRow.equals(FLYSIGHT_CSV_HEADER) || csvRow.equals(FLYSIGHT_CSV_HEADER_UNITS)) {
            return;
        }

        String[] row = csvRow.split(",");
        try {
            if (row.length != 14 ||
                    !row[0].matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{2}Z")) {
                throw new NumberFormatException();
            }

            FlySightTrackPoint flySightTrackPoint = new FlySightTrackPoint();
            flySightTrackPoint.csvRow = csvRow;
            flySightTrackPoint.unixTimeStamp = getUnixTimeForTimeString(row[0]);
            flySightTrackPoint.position = new GPSCoordinate(Double.parseDouble(row[1]), Double.parseDouble(row[2]), GPSCoordinate.VALID);
            flySightTrackPoint.altitude = Float.parseFloat(row[3]);
            flySightTrackPoint.velN = Float.parseFloat(row[4]) * 3.6f;
            flySightTrackPoint.velE = Float.parseFloat(row[5]) * 3.6f;
            flySightTrackPoint.vertVelocity = Float.parseFloat(row[6]) * 3.6f;
            flySightTrackPoint.hAcc = Float.parseFloat(row[7]);
            flySightTrackPoint.vAcc = Float.parseFloat(row[8]);
            flySightTrackPoint.sAcc = Float.parseFloat(row[9]);
            flySightTrackPoint.heading = Float.parseFloat(row[10]);
            flySightTrackPoint.cAcc = Float.parseFloat(row[11]);
            flySightTrackPoint.gpsFix = Integer.parseInt(row[12]);
            flySightTrackPoint.numSV = Integer.parseInt(row[13]);

            flySightTrackPoint.horVelocity = calculateHorizontalVelocity(flySightTrackPoint.velN, flySightTrackPoint.velE);
            flySightTrackPoint.glide = calculateGlide(flySightTrackPoint.horVelocity, flySightTrackPoint.vertVelocity);

            if (flySightTrackData.getFlySightTrackPoints().isEmpty()) {
                flySightTrackPoint.trackTimeInSeconds = 0;
                flySightTrackPoint.distance = 0;
            } else {
                FlySightTrackPoint firstPoint = flySightTrackData.getFlySightTrackPoints().get(0);
                flySightTrackPoint.trackTimeInSeconds = (flySightTrackPoint.unixTimeStamp - firstPoint.unixTimeStamp) / 1000f;

                FlySightTrackPoint previousPoint = flySightTrackData.getFlySightTrackPoints()
                        .get(flySightTrackData.getFlySightTrackPoints().size() - 1);
                flySightTrackPoint.distance = previousPoint.distance
                        + previousPoint.position.calculateHaversinDistanceMeters(flySightTrackPoint.position);
            }
            flySightTrackData.getFlySightTrackPoints().add(flySightTrackPoint);

        } catch (Exception e) {
            flySightTrackData.setParsingStatus(PARSING_ERRORS);
        }
    }

    private Float calculateHorizontalVelocity(Float velocityNorth, Float velocityEast) {
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

    //2018-08-12T14:25:43.07Z
    private long getUnixTimeForTimeString(String timeStamp) throws ParseException {
        Date dateTime = DATE_PARSER.parse(timeStamp.replace("Z", "0Z"));
        return dateTime.getTime();
    }
}
