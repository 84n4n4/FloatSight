
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

public class ButtonAdapter extends RecyclerView.Adapter<ButtonAdapter.ButtonViewHolder> {
    public List<ButtonItem> buttonItems;
    private ButtonItemClickListener buttonItemClickListener;

	public ButtonAdapter(List<ButtonItem> buttonItem) {
        this.buttonItems = buttonItem;
	}

    public void setButtonItemClickListener(ButtonItemClickListener buttonItemClickListener) {
        this.buttonItemClickListener = buttonItemClickListener;
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
        holder.title.setText(buttonItems.get(position).title);
        holder.description.setText(buttonItems.get(position).description);
        holder.icon.setImageResource(buttonItems.get(position).icon);
        holder.title.setEnabled(buttonItems.get(position).isEnabled);
        holder.description.setEnabled(buttonItems.get(position).isEnabled);
        holder.icon.setEnabled(buttonItems.get(position).isEnabled);
        holder.itemView.setOnClickListener(new ViewOnClickListener(buttonItems.get(position)));

    }

    @Override
    public int getItemCount() {
        return buttonItems.size();
    }

    private class ViewOnClickListener implements View.OnClickListener {
	    ButtonItem buttonItem;

	    ViewOnClickListener(ButtonItem buttonItem) {
	        this.buttonItem = buttonItem;
        }

        @Override
        public void onClick(View v) {
            buttonItemClickListener.onItemClick(buttonItem);
        }
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

    public interface ButtonItemClickListener {
        void onItemClick(ButtonItem buttonItem);
    }
}
