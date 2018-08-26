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

package com.watchthybridle.floatsight.configparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ConfigParser {

    private ConfigParser() {
        throw new AssertionError("No.");
    }

    public static String readToString(InputStream inputStream) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder builder = new StringBuilder();

        for (String line; (line = reader.readLine()) != null; ) {
            builder.append(line).append("\n");
        }

        reader.close();

        return builder.toString();
    }

    public static List<ConfigSetting> parse(InputStream inputStream) throws IOException {

        List<ConfigSetting> settings = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new StringReader(readToString(inputStream)));

        Pattern commentPattern = Pattern.compile(";[^\\n]*");
        Pattern namePattern = Pattern.compile("[\\S]+:");
        Pattern valuePattern = Pattern.compile("[0-9]+");

        for (String line = reader.readLine(); line != null; ) {

            Matcher nameMatcher = namePattern.matcher(line);
            if (nameMatcher.find()) {
                String settingName = nameMatcher.group().replace(":", "");
                line = line.replace(nameMatcher.group(), "");

                Matcher valueMatcher = valuePattern.matcher(line);
                if (!valueMatcher.find()) {
                    throw new AssertionError("Missing value after setting "
                            + settingName);
                }

                int value = Integer.parseInt(valueMatcher.group());

                Matcher commentMatcher = commentPattern.matcher(line);
                String description = "";

                if (commentMatcher.find()) {
                    description = commentMatcher.group().replace(";", "").trim();
                }

                List<String> commentList = new ArrayList<>();

                for (line = reader.readLine();
                     line != null && line.startsWith(" ") && (commentMatcher = commentPattern.matcher(line)).find(); ) {

                    commentList.add(commentMatcher.group().replace(";", "").trim());
                    line = reader.readLine();
                }

                String[] comments = new String[commentList.size()];
                comments = commentList.toArray(comments);

                settings.add(new ConfigSetting(settingName, value, description, comments));
            } else {
                line = reader.readLine();
            }
        }

        reader.close();

        return settings;
    }

    public static void write(OutputStream outputStream, List<ConfigSetting> settings) throws IOException {

        OutputStreamWriter writer = new OutputStreamWriter(outputStream);

        for (ConfigSetting setting : settings) {
            writer.write(setting.getString());
        }

        writer.close();
    }
}
