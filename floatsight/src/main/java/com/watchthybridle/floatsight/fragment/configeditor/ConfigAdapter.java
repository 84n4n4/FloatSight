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
import android.widget.TextView;
import com.watchthybridle.floatsight.R;
import com.watchthybridle.floatsight.configparser.ConfigItem;

import java.util.List;
import java.util.Locale;

class ConfigAdapter extends RecyclerView.Adapter<ConfigAdapter.ConfigItemViewHolder> {

    private List<ConfigItem> configItems;

    ConfigAdapter(List<ConfigItem> configItems) {
        this.configItems = configItems;
    }

    @NonNull
    @Override
    public ConfigItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vh_config_item, parent, false);
        return new ConfigItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ConfigItemViewHolder holder, int position) {
        ConfigItem item = configItems.get(position);

        holder.description.setText(item.description);
        holder.name.setText(item.name);
        holder.value.setText(String.format(Locale.getDefault(), "%d", item.value));
        for (String comment : item.comments) {
            holder.comments.append(comment + "\n");
        }
    }

    @Override
    public int getItemCount() {
        return configItems.size();
    }

    public void setData(List<ConfigItem> configItems) {
        this.configItems = configItems;
        notifyDataSetChanged();
    }

    static class ConfigItemViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView value;
        TextView description;
        TextView comments;

        ConfigItemViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            value = itemView.findViewById(R.id.value);
            description = itemView.findViewById(R.id.description);
            comments = itemView.findViewById(R.id.comments);
        }
    }
}
