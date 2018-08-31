
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

package com.watchthybridle.floatsight.fragment.mainmenu;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.watchthybridle.floatsight.R;

import java.util.List;

class MainMenuButtonAdapter extends RecyclerView.Adapter<MainMenuButtonAdapter.MainMenuButtonViewHolder> {
    List<MainMenuButtonItem> mainMenuButtonItems;
    private MainMenuItemClickListener mainMenuItemClickListener;

	MainMenuButtonAdapter(List<MainMenuButtonItem> mainMenuButtonItem) {
        this.mainMenuButtonItems = mainMenuButtonItem;
	}

    public void setMainMenuItemClickListener(MainMenuItemClickListener mainMenuItemClickListener) {
        this.mainMenuItemClickListener = mainMenuItemClickListener;
    }

    @Override
    @NonNull
    public MainMenuButtonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_menu_button_view_holder, parent, false);
        return new MainMenuButtonViewHolder(linearLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull MainMenuButtonViewHolder holder, int position) {
        holder.title.setText(mainMenuButtonItems.get(position).title);
        holder.description.setText(mainMenuButtonItems.get(position).description);
        holder.title.setEnabled(mainMenuButtonItems.get(position).isEnabled);
        holder.description.setEnabled(mainMenuButtonItems.get(position).isEnabled);
        holder.itemView.setOnClickListener(new ViewOnClickListener(mainMenuButtonItems.get(position)));

    }

    @Override
    public int getItemCount() {
        return mainMenuButtonItems.size();
    }

    private class ViewOnClickListener implements View.OnClickListener {
	    MainMenuButtonItem mainMenuButtonItem;

	    ViewOnClickListener(MainMenuButtonItem mainMenuButtonItem) {
	        this.mainMenuButtonItem = mainMenuButtonItem;
        }

        @Override
        public void onClick(View v) {
            mainMenuItemClickListener.onItemClick(mainMenuButtonItem.id);
        }
    }

    public class MainMenuButtonViewHolder extends RecyclerView.ViewHolder {
		public TextView title;
		public TextView description;
        MainMenuButtonViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.button_title);
            description = itemView.findViewById(R.id.button_description);
        }
	}

    public interface MainMenuItemClickListener {
        void onItemClick(int id);
    }
}
