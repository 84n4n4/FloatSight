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

package com.watchthybridle.floatsight.fragment.configeditor;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.watchthybridle.floatsight.R;
import com.watchthybridle.floatsight.configparser.ConfigSetting;

import java.util.List;
import java.util.Locale;

public class ConfigSettingsAdapter extends RecyclerView.Adapter<ConfigSettingsViewHolder> {

    private List<ConfigSetting> data;

    @NonNull
    @Override
    public ConfigSettingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vh_config_setting, parent, false);
        return new ConfigSettingsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConfigSettingsViewHolder holder, int position) {
        ConfigSetting item = data.get(position);
        holder.description.setText(item.description);
        holder.name.setText(item.name);
        holder.value.setText(String.format(Locale.getDefault(), "%d", item.value));
        for (String comment : item.comments) {
        	holder.comments.append(comment + "\n");
		}
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<ConfigSetting> data) {
        this.data = data;
        notifyDataSetChanged();
    }
}
