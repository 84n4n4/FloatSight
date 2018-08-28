
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

package com.watchthybridle.floatsight;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

class MainMenuButtonAdapter extends ArrayAdapter<MainMenuButtonItem> {
	MainMenuButtonAdapter(Context context, List<MainMenuButtonItem> objects) {
		super(context, 0, objects);
	}

	@Override
	public @NonNull	View getView(int position, View convertView, ViewGroup parent) {

		MainMenuButtonItem mainMenuButtonItem = getItem(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.main_menu_button, parent, false);
		}

		TextView title = convertView.findViewById(R.id.button_title);
		TextView description = convertView.findViewById(R.id.button_description);

		if(mainMenuButtonItem != null) {
			description.setText(mainMenuButtonItem.description);
			title.setText(mainMenuButtonItem.title);
		}
		return convertView;
	}
}
