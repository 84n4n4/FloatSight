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

package com.watchthybridle.floatsight.fragment;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

public class ButtonItem {

	public @StringRes int title;
	public @StringRes int description;
	public @DrawableRes int icon;
	public boolean isEnabled = true;
	public int id;
	public String overrideTitle = null;
	public String overrideDescription = null;

	public ButtonItem(int id, @StringRes int title, @StringRes int description, @DrawableRes int icon) {
		this.title = title;
		this.description = description;
		this.id = id;
		this.icon = icon;
	}

	public void setEnabled(boolean enabled) {
		isEnabled = enabled;
	}
}