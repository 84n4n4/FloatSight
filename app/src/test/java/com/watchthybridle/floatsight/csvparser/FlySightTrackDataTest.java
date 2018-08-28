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

import com.watchthybridle.floatsight.data.FlySightTrackData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class FlySightTrackDataTest {
    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testParseCsvRow() {
        String validCsvRow = "2018-08-12T12:44:06.20Z,47.0573044,15.5849899,3522.051,17.80,-47.72,-2.22,28.084,8.157,1.32,290.46083,0.39322,3,6";
        Double horVelocity = Math.sqrt(Math.pow(17.80,2) + Math.pow(-47.72,2)) * 3.6;
        Double verVelocity = -2.22 * 3.6;
        FlySightCsvParser flySightCsvParser = new FlySightCsvParser();
        FlySightTrackData flySightTrackData = new FlySightTrackData();

        flySightCsvParser.addCsvRow(flySightTrackData, validCsvRow);

        assertEquals(FlySightTrackData.PARSING_SUCCESS, flySightTrackData.getParsingStatus());

        assertFalse(flySightTrackData.getFlySightTrackPoints().isEmpty());
        FlySightTrackPoint flySightTrackPoint = flySightTrackData.getFlySightTrackPoints().get(0);

        assertEquals(0f, flySightTrackPoint.trackTimeInSeconds);
        assertEquals(3522.051f, flySightTrackPoint.altitude);
        assertEquals(0f, flySightTrackPoint.distance);
        assertEquals(0f, flySightTrackPoint.glide);
        assertEquals(horVelocity.floatValue(), flySightTrackPoint.horVelocity, 0.0001);
        assertEquals(verVelocity.floatValue(), flySightTrackPoint.vertVelocity, 0.001);
    }

    @Test
    public void testInvalidCSVRow() {
        String validCsvRow = "This is not a proper csv data row from a flysight track file";
        FlySightCsvParser flySightCsvParser = new FlySightCsvParser();
        FlySightTrackData flySightTrackData = new FlySightTrackData();

        flySightCsvParser.addCsvRow(flySightTrackData, validCsvRow);

        assertEquals(FlySightTrackData.PARSING_ERRORS, flySightTrackData.getParsingStatus());
    }

    @Test
    public void testShortDistance() {
        String pointA = "2018-08-12T12:53:19.40Z,46.9673220,15.4397928,3073.025,-20.78,-19.20,31.30,0.537,0.664,0.15,222.73278,0.24067,3,20";
        String pointB = "2018-08-12T12:54:05.00Z,46.9783088,15.4341040,1898.729,38.01,-3.11,23.77,0.504,0.645,0.16,355.32497,0.15122,3,20";
        FlySightCsvParser flySightCsvParser = new FlySightCsvParser();
        FlySightTrackData flySightTrackData = new FlySightTrackData();

        flySightCsvParser.addCsvRow(flySightTrackData, pointA);
        flySightCsvParser.addCsvRow(flySightTrackData, pointB);

        assertEquals(2, flySightTrackData.getFlySightTrackPoints().size());
        FlySightTrackPoint flySightTrackPoint = flySightTrackData.getFlySightTrackPoints().get(1);

        assertEquals(1295.6837f, flySightTrackPoint.distance);
    }

    @Test
    public void testLongDistance() {
        String pointA = "2018-08-12T12:53:19.40Z,-33.865143,151.209900,3073.025,-20.78,-19.20,31.30,0.537,0.664,0.15,222.73278,0.24067,3,20";
        String pointB = "2018-08-12T12:54:05.00Z,46.9783088,15.4341040,1898.729,38.01,-3.11,23.77,0.504,0.645,0.16,355.32497,0.15122,3,20";
        FlySightCsvParser flySightCsvParser = new FlySightCsvParser();
        FlySightTrackData flySightTrackData = new FlySightTrackData();

        flySightCsvParser.addCsvRow(flySightTrackData, pointA);
        flySightCsvParser.addCsvRow(flySightTrackData, pointB);

        assertEquals(2, flySightTrackData.getFlySightTrackPoints().size());
        FlySightTrackPoint flySightTrackPoint = flySightTrackData.getFlySightTrackPoints().get(1);

        assertEquals(16059592.00f, flySightTrackPoint.distance);
    }

    @Test
    public void testTimeDifferenceShort() {
        String pointA = "2018-08-12T12:53:19.40Z,-33.865143,151.209900,3073.025,-20.78,-19.20,31.30,0.537,0.664,0.15,222.73278,0.24067,3,20";
        String pointB = "2018-08-12T12:53:20.55Z,-33.865143,151.209900,3073.025,-20.78,-19.20,31.30,0.537,0.664,0.15,222.73278,0.24067,3,20";
        FlySightCsvParser flySightCsvParser = new FlySightCsvParser();
        FlySightTrackData flySightTrackData = new FlySightTrackData();

        flySightCsvParser.addCsvRow(flySightTrackData, pointA);
        flySightCsvParser.addCsvRow(flySightTrackData, pointB);

        assertEquals(2, flySightTrackData.getFlySightTrackPoints().size());
        FlySightTrackPoint flySightTrackPoint = flySightTrackData.getFlySightTrackPoints().get(1);

        assertEquals(1.15f, flySightTrackPoint.trackTimeInSeconds);
    }

    @Test
    public void testTimeDifferenceLong() {
        String pointA = "2018-08-12T12:53:19.40Z,-33.865143,151.209900,3073.025,-20.78,-19.20,31.30,0.537,0.664,0.15,222.73278,0.24067,3,20";
        String pointB = "2018-08-12T13:53:19.40Z,-33.865143,151.209900,3073.025,-20.78,-19.20,31.30,0.537,0.664,0.15,222.73278,0.24067,3,20";
        FlySightCsvParser flySightCsvParser = new FlySightCsvParser();
        FlySightTrackData flySightTrackData = new FlySightTrackData();

        flySightCsvParser.addCsvRow(flySightTrackData, pointA);
        flySightCsvParser.addCsvRow(flySightTrackData, pointB);

        assertEquals(2, flySightTrackData.getFlySightTrackPoints().size());
        FlySightTrackPoint flySightTrackPoint = flySightTrackData.getFlySightTrackPoints().get(1);

        assertEquals(3600.00f, flySightTrackPoint.trackTimeInSeconds);
    }
}
