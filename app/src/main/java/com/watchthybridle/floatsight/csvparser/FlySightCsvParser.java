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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FlySightCsvParser {
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
                    flySightTrackData.addCsvLine(csvLine);
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
}
