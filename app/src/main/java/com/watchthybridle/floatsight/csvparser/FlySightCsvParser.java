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

import com.github.mikephil.charting.data.Entry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.watchthybridle.floatsight.csvparser.FlySightTrackData.PARSING_ERRORS;

public class FlySightCsvParser {
    public static final String FLYSIGHT_CSV_HEADER = "time,lat,lon,hMSL,velN,velE,velD,hAcc,vAcc,sAcc,heading,cAcc,gpsFix,numSV";
    public static final String FLYSIGHT_CSV_HEADER_UNITS = ",(deg),(deg),(m),(m/s),(m/s),(m/s),(m),(m),(m/s),(deg),(deg),,";

    InputStream inputStream;

    public FlySightCsvParser(InputStream inputStream){
        this.inputStream = inputStream;
    }

    public FlySightTrackData read() throws CsvParsingException {
        FlySightTrackData flySightTrackData = new FlySightTrackData();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine;

            while ((csvLine = reader.readLine()) != null) {
                addCsvRow(flySightTrackData, csvLine);
            }
        }
        catch (IOException exception) {
            throw new CsvParsingException("Error in reading CSV file: " + exception);
        }
        finally {
            try {
                inputStream.close();
            }
            catch (IOException exception) {
                throw new CsvParsingException("Error while closing input stream: " + exception);
            }
        }
        return flySightTrackData;
    }

    public class CsvParsingException extends Exception {
        public CsvParsingException(String message) {
            super(message);
        }
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
            String time = row[0];
            Float vertVelocity = Float.parseFloat(row[6]) * 3.6f;
            Float altitude = Float.parseFloat(row[3]);

            Double velocityNorth = Double.parseDouble(row[4]) * 3.6f;
            Double velocityEast = Double.parseDouble(row[5]) * 3.6f;
            Float horVelocity = calculateHorizontalVelocity(velocityNorth, velocityEast);
            Float glide = calculateGlide(horVelocity, vertVelocity);
            GPSCoordinate gpsCoordinate = new GPSCoordinate(Double.parseDouble(row[1]), Double.parseDouble(row[2]), GPSCoordinate.VALID);
            Float distance = 0f;
            if (flySightTrackData.getPositions().size() > 0 &&
                gpsCoordinate.valid == GPSCoordinate.VALID){
                GPSCoordinate previousPoint = flySightTrackData.getPositions().get(flySightTrackData.getPositions().size() - 1);
                float previousDistance = flySightTrackData.getDistance().get(flySightTrackData.getDistance().size() - 1).getY();
                distance = previousDistance + previousPoint.calculateHaversinDistanceMeters(gpsCoordinate);
            }

            int entryPosition = flySightTrackData.getTimeStamps().size();
            flySightTrackData.getTimeStamps().add(time);
            flySightTrackData.getPositions().add(gpsCoordinate);
            flySightTrackData.getHorVelocity().add(new Entry(entryPosition, horVelocity));
            flySightTrackData.getVertVelocity().add(new Entry(entryPosition,vertVelocity));
            flySightTrackData.getAltitude().add(new Entry(entryPosition, altitude));
            flySightTrackData.getGlide().add(new Entry(entryPosition, glide));
            flySightTrackData.getDistance().add(new Entry(entryPosition, distance));
            flySightTrackData.getDistanceVsAltitude().add(new Entry(distance, altitude));
        } catch (Exception e) {
            handleLineParsingError(flySightTrackData);
        }
    }

    private void handleLineParsingError(FlySightTrackData flySightTrackData) {
        flySightTrackData.setParsingStatus(PARSING_ERRORS);

        int entryPosition = flySightTrackData.getTimeStamps().size();
        flySightTrackData.getTimeStamps().add("invalid");
        flySightTrackData.getPositions().add(new GPSCoordinate(0,0, GPSCoordinate.INVALID));
        flySightTrackData.getHorVelocity().add(new Entry(entryPosition, 0f));
        flySightTrackData.getVertVelocity().add(new Entry(entryPosition, 0f));
        flySightTrackData.getAltitude().add(new Entry(entryPosition, 0f));
        flySightTrackData.getGlide().add(new Entry(entryPosition, 0f));
        flySightTrackData.getDistance().add(new Entry(entryPosition, 0f));
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
}
