
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

package com.watchthybridle.floatsight.fragment.stats;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.watchthybridle.floatsight.R;

import java.util.List;

public class StatsAdapter extends RecyclerView.Adapter<StatsAdapter.ButtonViewHolder> {
    public List<StatsItem> statsItems;

	public StatsAdapter(List<StatsItem> statsItem) {
        this.statsItems = statsItem;
	}

    @Override
    @NonNull
    public ButtonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.button_view_holder, parent, false);
        return new ButtonViewHolder(linearLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull ButtonViewHolder holder, int position) {
	    StatsItem statsItem = statsItems.get(position);
	    if(statsItem.overrideTitle == null) {
            holder.title.setText(statsItem.title);
        } else {
            holder.title.setText(statsItem.overrideTitle);
        }
        if(statsItem.overrideDescription == null) {
            holder.description.setText(statsItem.description);
        } else {
            holder.description.setText(statsItem.overrideDescription);
        }
        holder.icon.setImageResource(statsItem.icon);
    }

    @Override
    public int getItemCount() {
        return statsItems.size();
    }

    public class ButtonViewHolder extends RecyclerView.ViewHolder {
		public TextView title;
		public TextView description;
		public ImageView icon;
        ButtonViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.button_title);
            description = itemView.findViewById(R.id.button_description);
            icon = itemView.findViewById(R.id.button_icon);
        }
	}
}
