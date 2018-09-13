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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.TestCase.assertEquals;

@RunWith(JUnit4.class)

public class GPSCoordinateTest {
    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testShortDistance() {
        GPSCoordinate coordinateA = new GPSCoordinate(46.9673220, 15.4397928, GPSCoordinate.VALID);
        GPSCoordinate coordinateB = new GPSCoordinate(46.9783088, 15.4341040, GPSCoordinate.VALID);

        assertEquals(1295.6837f, coordinateA.calculateHaversinDistanceMeters(coordinateB));
    }

    @Test
    public void testLongDistance() {
        GPSCoordinate coordinateA = new GPSCoordinate(-33.865143, 151.209900, GPSCoordinate.VALID);
        GPSCoordinate coordinateB = new GPSCoordinate(46.9783088, 15.4341040, GPSCoordinate.VALID);

        assertEquals(16059592.00f, coordinateA.calculateHaversinDistanceMeters(coordinateB));
    }
}
