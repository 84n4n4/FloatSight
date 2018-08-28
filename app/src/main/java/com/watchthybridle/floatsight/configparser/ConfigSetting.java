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

import java.util.Locale;

public class ConfigSetting {

	private static final String TEMPLATE_VALUE_LINE = "%s: %d ; %s\n";
	private static final String TEMPLATE_COMMENT_LINE = "                ;   %s\n";

	public String name;
	public int value;
	public String description;

	public String[] comments;

	public ConfigSetting(String name, int value, String description, String... comments) {
		this.name = name;
		this.value = value;
		this.description = description;
		this.comments = comments;
	}

	public String getString() {
		StringBuilder builder = new StringBuilder();

		builder.append(String.format(Locale.getDefault(), TEMPLATE_VALUE_LINE, name, value, description));

		for (String comment : comments) {
			builder.append(String.format(Locale.getDefault(), TEMPLATE_COMMENT_LINE, comment));
		}

		return builder.toString();
	}
}
