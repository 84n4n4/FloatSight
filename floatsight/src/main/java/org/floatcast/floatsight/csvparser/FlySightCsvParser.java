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

import org.apache.commons.lang3.time.DateParser;
import org.apache.commons.lang3.time.DatePrinter;
import org.apache.commons.lang3.time.FastDateFormat;
import org.floatcast.floatsight.Parser;
import org.floatcast.floatsight.data.FlySightTrackData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import static org.floatcast.floatsight.data.FlySightTrackData.PARSING_ERRORS;

public class FlySightCsvParser implements Parser<FlySightTrackData> {

    public static final String FLYSIGHT_CSV_HEADER_POST_V20141005 = "time,lat,lon,hMSL,velN,velE,velD,hAcc,vAcc,sAcc,heading,cAcc,gpsFix,numSV";
    public static final String FLYSIGHT_CSV_HEADER_UNITS_POST_V20141005 = ",(deg),(deg),(m),(m/s),(m/s),(m/s),(m),(m),(m/s),(deg),(deg),,";

    public static final String FLYSIGHT_CSV_HEADER_PRE_V20141005 = "time,lat,lon,hMSL,velN,velE,velD,hAcc,vAcc,sAcc,gpsFix,numSV";
    public static final String FLYSIGHT_CSV_HEADER_UNITS_PRE_V20141005 = ",(deg),(deg),(m),(m/s),(m/s),(m/s),(m),(m),(m/s),,,";

    private static final DateParser DATE_PARSER = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private static final DatePrinter DATE_PRINTER = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    @Override
    public FlySightTrackData read(InputStream inputStream) throws IOException {
        FlySightTrackData flySightTrackData = new FlySightTrackData();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                readCsvRow(flySightTrackData, csvLine);
            }
        } finally {
            inputStream.close();
        }
        return flySightTrackData;
    }

    void readCsvRow(FlySightTrackData flySightTrackData, String csvRow) {
        if (csvRow.equals(FLYSIGHT_CSV_HEADER_POST_V20141005)
                || csvRow.equals(FLYSIGHT_CSV_HEADER_PRE_V20141005)) {
            flySightTrackData.setPreV20141005(csvRow.equals(FLYSIGHT_CSV_HEADER_PRE_V20141005));
            return;
        }

        if (csvRow.equals(FLYSIGHT_CSV_HEADER_UNITS_POST_V20141005)
            || csvRow.equals(FLYSIGHT_CSV_HEADER_UNITS_PRE_V20141005)) {
            return;
        }

        try {
            FlySightTrackPoint flySightTrackPoint = parseValuesFromCsvRow(flySightTrackData.isPreV20141005(), csvRow);

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

    // 0   ,1  ,2  ,3   ,4   ,5   ,6   ,7   ,8   ,9   ,10     ,11  ,12    ,13
    // time,lat,lon,hMSL,velN,velE,velD,hAcc,vAcc,sAcc,heading,cAcc,gpsFix,numSV

    // pre pre v20141005 log format
    // time,lat,lon,hMSL,velN,velE,velD,hAcc,vAcc,sAcc,gpsFix,numSV
    private FlySightTrackPoint parseValuesFromCsvRow(boolean preV20141005, String csvRow) throws Exception {
        String[] row = csvRow.split(",");
        if(preV20141005 && (row.length != 12)
            || !preV20141005 && (row.length != 14)) {
            throw new NumberFormatException();
        }

        FlySightTrackPoint flySightTrackPoint = new FlySightTrackPoint();
        flySightTrackPoint.unixTimeStamp = getUnixTimeForTimeString(row[0]);
        flySightTrackPoint.position = new GPSCoordinate(Double.parseDouble(row[1]), Double.parseDouble(row[2]), GPSCoordinate.VALID);
        flySightTrackPoint.altitude = Float.parseFloat(row[3]);
        flySightTrackPoint.velN = Float.parseFloat(row[4]) * 3.6f;
        flySightTrackPoint.velE = Float.parseFloat(row[5]) * 3.6f;
        flySightTrackPoint.vertVelocity = Float.parseFloat(row[6]) * 3.6f;
        flySightTrackPoint.hAcc = Float.parseFloat(row[7]);
        flySightTrackPoint.vAcc = Float.parseFloat(row[8]);
        flySightTrackPoint.sAcc = Float.parseFloat(row[9]);
        if (preV20141005) {
            flySightTrackPoint.gpsFix = Integer.parseInt(row[10]);
            flySightTrackPoint.numSV = Integer.parseInt(row[11]);
        } else {
            flySightTrackPoint.heading = Float.parseFloat(row[10]);
            flySightTrackPoint.cAcc = Float.parseFloat(row[11]);
            flySightTrackPoint.gpsFix = Integer.parseInt(row[12]);
            flySightTrackPoint.numSV = Integer.parseInt(row[13]);
        }
        return flySightTrackPoint;
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

        return glide;
    }

    // format 2018-08-12T14:25:43.07Z
    private long getUnixTimeForTimeString(String timeStamp) throws ParseException {
        Date dateTime = DATE_PARSER.parse(timeStamp.replace("Z", "0Z"));
        return dateTime.getTime();
    }

    @Override
    public void write(OutputStream outputStream, FlySightTrackData data) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        writer.write(FLYSIGHT_CSV_HEADER_POST_V20141005 + "\n");
        writer.write(FLYSIGHT_CSV_HEADER_UNITS_POST_V20141005 + "\n");
        for (FlySightTrackPoint trackPoint : data.getFlySightTrackPoints()) {
            writer.write(trackPointToCsvRow(trackPoint));
        }
        writer.close();
    }

    // 0   ,
    // time,
    // 2015-09-27T17:09:22.00Z,
    // 1          ,2          ,3        ,4     ,5      ,6     ,7     ,8     ,9    ,10        ,11      ,12     ,13
    // lat        ,lon        ,hMSL     ,velN  ,velE   ,velD  ,hAcc  ,vAcc  ,sAcc ,heading   ,cAcc    ,gpsFix ,numSV
    // 47.1119989 ,15.4605011 ,3541.855 ,24.18 ,-46.99 ,-2.43 ,5.044 ,5.756 ,0.57 ,297.23267 ,0.67036 ,3      ,10
    @SuppressWarnings("PMD.AvoidDuplicateLiterals")
    String trackPointToCsvRow(FlySightTrackPoint flySightTrackPoint) {
        StringBuilder rowBuilder = new StringBuilder();
        rowBuilder.append(getTimeStringForUnixTime(flySightTrackPoint.unixTimeStamp)).append(',')
            .append(String.format(Locale.US, "%.7f", flySightTrackPoint.position.lat)).append(',')
            .append(String.format(Locale.US, "%.7f", flySightTrackPoint.position.lon)).append(',')
            .append(String.format(Locale.US, "%.3f", flySightTrackPoint.altitude)).append(',')
            .append(String.format(Locale.US, "%.2f", flySightTrackPoint.velN / 3.6f)).append(',')
            .append(String.format(Locale.US, "%.2f", flySightTrackPoint.velE / 3.6f)).append(',')
            .append(String.format(Locale.US, "%.2f", flySightTrackPoint.vertVelocity / 3.6f)).append(',')
            .append(String.format(Locale.US, "%.3f", flySightTrackPoint.hAcc)).append(',')
            .append(String.format(Locale.US, "%.3f", flySightTrackPoint.vAcc)).append(',')
            .append(String.format(Locale.US, "%.2f", flySightTrackPoint.sAcc)).append(',')
            .append(String.format(Locale.US, "%.5f", flySightTrackPoint.heading)).append(',')
            .append(String.format(Locale.US, "%.5f", flySightTrackPoint.cAcc)).append(',')
            .append(String.format(Locale.US, "%d", flySightTrackPoint.gpsFix)).append(',')
            .append(String.format(Locale.US, "%d", flySightTrackPoint.numSV))
            .append('\n');
        return rowBuilder.toString();
    }

    // format 2018-08-12T14:25:43.07Z
    private String getTimeStringForUnixTime(long unixTimeStamp) {
        Date dateTime = new Date(unixTimeStamp);
        return DATE_PRINTER.format(dateTime).substring(0, 22) + "Z";
    }
}
