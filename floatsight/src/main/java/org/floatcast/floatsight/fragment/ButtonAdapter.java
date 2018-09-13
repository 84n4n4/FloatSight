
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

package org.floatcast.floatsight.fragment;

import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import org.floatcast.floatsight.R;

import java.util.List;

public class ButtonAdapter extends RecyclerView.Adapter<ButtonAdapter.ButtonViewHolder> {

    public List<ButtonItem> buttonItems;
    private ButtonItemClickListener buttonItemClickListener;
    @ColorInt int highlightColor;
    @ColorInt int defaultColor;

    public ButtonAdapter(List<ButtonItem> buttonItem) {
        this.buttonItems = buttonItem;
    }

    public void setButtonItemClickListener(ButtonItemClickListener buttonItemClickListener) {
        this.buttonItemClickListener = buttonItemClickListener;
    }

    @Override
    @NonNull
    public ButtonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vh_button, parent, false);
        highlightColor = ContextCompat.getColor(itemView.getContext(), R.color.buttonHighlighted);
        defaultColor = ContextCompat.getColor(itemView.getContext(), R.color.buttonDefault);
        return new ButtonViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ButtonViewHolder holder, int position) {
        ButtonItem item = buttonItems.get(position);

        if (item.overrideTitle == null) {
            holder.title.setText(item.title);
        } else {
            holder.title.setText(item.overrideTitle);
        }
        if (item.overrideDescription == null) {
            holder.description.setText(item.description);
        } else {
            holder.description.setText(item.overrideDescription);
        }

        holder.icon.setImageResource(item.icon);
        holder.title.setEnabled(item.isEnabled);
        holder.description.setEnabled(item.isEnabled);
        holder.icon.setEnabled(item.isEnabled);
        holder.itemView.setOnClickListener(new OnItemViewClickListener(buttonItems.get(position)));

        if (item.highlighted) {
            holder.title.setTextColor(highlightColor);
        } else {
            holder.title.setTextColor(defaultColor);
        }
    }

    @Override
    public int getItemCount() {
        return buttonItems.size();
    }

    static class ButtonViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView description;
        ImageView icon;

        ButtonViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.button_title);
            description = itemView.findViewById(R.id.button_description);
            icon = itemView.findViewById(R.id.button_icon);
        }
    }

    private class OnItemViewClickListener implements View.OnClickListener {

        ButtonItem buttonItem;

        OnItemViewClickListener(ButtonItem buttonItem) {
            this.buttonItem = buttonItem;
        }

        @Override
        public void onClick(View v) {
            buttonItemClickListener.onItemClick(buttonItem);
        }
    }

    public interface ButtonItemClickListener {

        void onItemClick(ButtonItem buttonItem);
    }
}
