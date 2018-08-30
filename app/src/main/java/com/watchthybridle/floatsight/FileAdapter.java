
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

class FileAdapter extends ArrayAdapter<FileAdapterItem> {

	FileAdapter(Context context, List<FileAdapterItem> objects) {
		super(context, 0, objects);
	}

	@Override
	public @NonNull	View getView(int position, View convertView, ViewGroup parent) {
		FileAdapterItem fileAdapterItem = getItem(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.file_adapter_item, parent, false);
		}

		TextView fileName = convertView.findViewById(R.id.file_name);

		if(fileAdapterItem != null) {
			fileName.setText(fileAdapterItem.fileName);
		}
		return convertView;
	}
}
