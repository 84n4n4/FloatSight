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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;

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
        FlySightTrackData flySightTrackData = new FlySightTrackData();
        flySightTrackData.addCsvRow(validCsvRow);

        assertEquals(FlySightTrackData.PARSING_SUCCESS, flySightTrackData.getParsingStatus());

        assertFalse(flySightTrackData.getTimeStamps().isEmpty());
        assertFalse(flySightTrackData.getAltitude().isEmpty());
        assertFalse(flySightTrackData.getDistance().isEmpty());
        assertFalse(flySightTrackData.getGlide().isEmpty());
        assertFalse(flySightTrackData.getHorVelocity().isEmpty());
        assertFalse(flySightTrackData.getVertVelocity().isEmpty());
        assertFalse(flySightTrackData.getDistanceVsAltitude().isEmpty());

        assertEquals("2018-08-12T12:44:06.20Z", flySightTrackData.getTimeStamps().get(0));
        assertEquals(3522.051f, flySightTrackData.getAltitude().get(0).getY());
        assertEquals(0f, flySightTrackData.getDistance().get(0).getY());
        assertEquals(0f, flySightTrackData.getGlide().get(0).getY());
        assertEquals(horVelocity.floatValue(), flySightTrackData.getHorVelocity().get(0).getY());
        assertEquals(verVelocity.floatValue(), flySightTrackData.getVertVelocity().get(0).getY());
    }

    @Test
    public void testInvalidCSVRow() {
        String validCsvRow = "This is not a proper csv data row from a flysight track file";
        FlySightTrackData flySightTrackData = new FlySightTrackData();
        flySightTrackData.addCsvRow(validCsvRow);

        assertEquals(FlySightTrackData.PARSING_ERRORS, flySightTrackData.getParsingStatus());
    }

    @Test
    public void testShortDistance() {
        String pointA = "2018-08-12T12:53:19.40Z,46.9673220,15.4397928,3073.025,-20.78,-19.20,31.30,0.537,0.664,0.15,222.73278,0.24067,3,20";
        String pointB = "2018-08-12T12:54:05.00Z,46.9783088,15.4341040,1898.729,38.01,-3.11,23.77,0.504,0.645,0.16,355.32497,0.15122,3,20";
        FlySightTrackData flySightTrackData = new FlySightTrackData();
        flySightTrackData.addCsvRow(pointA);
        flySightTrackData.addCsvRow(pointB);

        assertEquals(1295.6837f, flySightTrackData.getDistance().get(1).getY());
    }

    @Test
    public void testLongDistance() {
        String pointA = "2018-08-12T12:53:19.40Z,-33.865143,151.209900,3073.025,-20.78,-19.20,31.30,0.537,0.664,0.15,222.73278,0.24067,3,20";
        String pointB = "2018-08-12T12:54:05.00Z,46.9783088,15.4341040,1898.729,38.01,-3.11,23.77,0.504,0.645,0.16,355.32497,0.15122,3,20";
        FlySightTrackData flySightTrackData = new FlySightTrackData();
        flySightTrackData.addCsvRow(pointA);
        flySightTrackData.addCsvRow(pointB);

        assertEquals(16059592.00f, flySightTrackData.getDistance().get(1).getY());
    }

}
