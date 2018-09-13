
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

package org.floatcast.floatsight.fragment.trackfragment.stats;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import org.floatcast.floatsight.R;

import java.util.List;

class StatsAdapter extends RecyclerView.Adapter<StatsAdapter.StatsItemViewHolder> {

    public List<StatsItem> statsItems;

	StatsAdapter(List<StatsItem> statsItem) {
        this.statsItems = statsItem;
	}

    @Override
    @NonNull
    public StatsItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vh_button, parent, false);
        return new StatsItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StatsItemViewHolder holder, int position) {
	    StatsItem item = statsItems.get(position);
        holder.title.setText(item.title);
        holder.description.setText(item.value);
        holder.icon.setImageResource(item.icon);
    }

    @Override
    public int getItemCount() {
        return statsItems.size();
    }

    static class StatsItemViewHolder extends RecyclerView.ViewHolder {

		TextView title;
		TextView description;
		ImageView icon;

        StatsItemViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.button_title);
            description = itemView.findViewById(R.id.button_description);
            icon = itemView.findViewById(R.id.button_icon);
        }
	}
}
