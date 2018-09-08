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

import com.watchthybridle.floatsight.configparser.ConfigParser;
import com.watchthybridle.floatsight.configparser.ConfigItem;
import com.watchthybridle.floatsight.data.ConfigData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class SettingsParserTest {

    private String defaultConfigContent = "; GPS settings\n" +
            "\n" +
            "Model:     6    ; Dynamic model\n" +
            "                ;   0 = Portable\n" +
            "                ;   2 = Stationary\n" +
            "                ;   3 = Pedestrian\n" +
            "                ;   4 = Automotive\n" +
            "                ;   5 = Sea\n" +
            "                ;   6 = Airborne with < 1 G acceleration\n" +
            "                ;   7 = Airborne with < 2 G acceleration\n" +
            "                ;   8 = Airborne with < 4 G acceleration\n" +
            "Rate:      200  ; Measurement rate (ms)\n" +
            "\n" +
            "; Tone settings\n" +
            "\n" +
            "Mode:      2    ; Measurement mode\n" +
            "                ;   0 = Horizontal speed\n" +
            "                ;   1 = Vertical speed\n" +
            "                ;   2 = Glide ratio\n" +
            "                ;   3 = Inverse glide ratio\n" +
            "                ;   4 = Total speed\n" +
            "Min:       0    ; Lowest pitch value\n" +
            "                ;   cm/s        in Mode 0, 1, or 4\n" +
            "                ;   ratio * 100 in Mode 2 or 3\n" +
            "Max:       300  ; Highest pitch value\n" +
            "                ;   cm/s        in Mode 0, 1, or 4\n" +
            "                ;   ratio * 100 in Mode 2 or 3\n" +
            "Chirp:     0    ; Chirp when outside bounds\n" +
            "                ;   0 = No\n" +
            "                ;   1 = Yes\n" +
            "Volume:    6    ; 0 (min) to 8 (max)\n" +
            "\n" +
            "; Thresholds\n" +
            "\n" +
            "V_Thresh:  1000 ; Minimum vertical speed for tone (cm/s)\n" +
            "H_Thresh:  0    ; Minimum horizontal speed for tone (cm/s)\n" +
            "\n" +
            "; Miscellaneous\n" +
            "\n" +
            "Use_SAS:   1    ; Use skydiver's airspeed\n" +
            "                ;   0 = No\n" +
            "                ;   1 = Yes\n";

    @Test
    public void testGetStringFromFile() throws IOException {

        InputStream inputStream = new ByteArrayInputStream(defaultConfigContent.getBytes());

        ConfigParser parser = new ConfigParser();
        String content = parser.readToString(inputStream);

        assertEquals(content, defaultConfigContent);
        inputStream.close();
    }

    @Test
    public void testGetSettingsFromFile() throws IOException {

        String expectedContent = "setting0:   0   ;    description0\n" +
                "                ;   Comment0\n ;\n" +
                "                ;   Comment1 comment with 10 [m/s]\n" +
                "\n\n" +
                "setting_1:1   ; description1\n" +
                "        ;   Comment1 comment with 20 [m/s]\n" +
                "setting2: 2 ; a description with spaces and 10 [m/s]\n";

        InputStream inputStream = new ByteArrayInputStream(expectedContent.getBytes());

        ConfigParser parser = new ConfigParser();
        List<ConfigItem> settings = parser.read(inputStream).getSettings();

        assertEquals("setting0", settings.get(0).name);
        assertEquals(0, settings.get(0).value);
        assertEquals("description0", settings.get(0).description);
        assertEquals("Comment0", settings.get(0).comments[0]);
        assertEquals("", settings.get(0).comments[1]);
        assertEquals("Comment1 comment with 10 [m/s]", settings.get(0).comments[2]);

        assertEquals("setting_1", settings.get(1).name);
        assertEquals(1, settings.get(1).value);
        assertEquals("description1", settings.get(1).description);
        assertEquals("Comment1 comment with 20 [m/s]", settings.get(1).comments[0]);

        assertEquals("setting2", settings.get(2).name);
        assertEquals(2, settings.get(2).value);
        assertEquals("a description with spaces and 10 [m/s]", settings.get(2).description);

        inputStream.close();
    }

    @Test
    public void testWriteToFile() throws IOException {

        String expectedContent = "setting0: 0 ; description0\n" +
                "                ;   Comment0\n" +
                "                ;   Comment1 comment with 10 [m/s]\n" +
                "setting1: 1 ; description1\n" +
                "                ;   Comment0\n" +
                "setting2: 2 ; description2\n";

        ConfigData settingsData = new ConfigData();
        List<ConfigItem> settings = new ArrayList<>();
        settings.add(new ConfigItem("setting0", 0,
                "description0", "Comment0", "Comment1 comment with 10 [m/s]"));
        settings.add(new ConfigItem("setting1", 1,
                "description1", "Comment0"));
        settings.add(new ConfigItem("setting2", 2, "description2"));
        settingsData.setSettings(settings);

        OutputStream outputStream = new ByteArrayOutputStream();

        ConfigParser parser = new ConfigParser();
        parser.write(outputStream, settingsData);

        assertEquals(expectedContent, outputStream.toString());
        outputStream.close();
    }
}
