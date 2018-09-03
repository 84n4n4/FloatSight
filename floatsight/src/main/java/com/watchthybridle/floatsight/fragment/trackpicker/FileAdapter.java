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

package com.watchthybridle.floatsight.fragment.trackpicker;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.watchthybridle.floatsight.R;
import com.watchthybridle.floatsight.recyclerview.ItemClickListener;

import java.io.File;
import java.util.List;

class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileAdapterViewHolder> {

    private ItemClickListener<FileAdapterItem> itemClickListener;
    private List<FileAdapterItem> fileAdapterItems;

    FileAdapter(List<FileAdapterItem> fileAdapterItems) {
        this.fileAdapterItems = fileAdapterItems;
    }

    public void setItemClickListener(ItemClickListener<FileAdapterItem> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    @NonNull
    public FileAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.file_view_holder, parent, false);
        return new FileAdapterViewHolder(linearLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull FileAdapterViewHolder holder, int position) {
        holder.fileName.setText(fileAdapterItems.get(position).fileName);
        holder.fileName.setEnabled(fileAdapterItems.get(position).isEnabled);

        if (fileAdapterItems.get(position).isEnabled) {
            if(fileAdapterItems.get(position).file.isDirectory()) {
                holder.fileIcon.setImageResource(R.drawable.load);
            } else {
                holder.fileIcon.setImageResource(R.drawable.ic_flysight_tile);
            }
        } else {
            holder.fileIcon.setImageResource(R.drawable.ic_flysight_tile_greyed);
        }

            ViewOnClickListener onClickListener = new ViewOnClickListener(fileAdapterItems.get(position));
        holder.itemView.setOnClickListener(onClickListener);
        holder.itemView.setOnLongClickListener(onClickListener);
    }

    @Override
    public int getItemCount() {
        return fileAdapterItems.size();
    }

    public boolean contains(String fileName) {
        for (FileAdapterItem fileAdapterItem : fileAdapterItems) {
            if (fileAdapterItem.fileName.equals(fileName)) {
                return true;
            }
        }
        return false;
    }

    public void rename(FileAdapterItem item, String newFileName) {
        File renamedFile = new File(item.file.getParentFile(), newFileName);
        if (item.file.renameTo(renamedFile)) {
            item.file = renamedFile;
            item.fileName = newFileName;
            notifyItemChanged(fileAdapterItems.indexOf(item));
        }
    }

    public boolean remove(FileAdapterItem item) {
        int position = fileAdapterItems.indexOf(item);

        if (!item.file.delete()) {
            Log.e("REMOVE", "Removing " + item.file.getAbsolutePath()
                    + " failed, removing form adapter anyway.");
        }

        boolean removed = fileAdapterItems.remove(item);
        if (removed) {
            notifyItemRemoved(position);
        }
        return removed;
    }

    private class ViewOnClickListener implements View.OnClickListener, View.OnLongClickListener {

        FileAdapterItem fileAdapterItem;

        ViewOnClickListener(FileAdapterItem fileAdapterItem) {
            this.fileAdapterItem = fileAdapterItem;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onItemClick(fileAdapterItem);
        }

        @Override
        public boolean onLongClick(View view) {
            itemClickListener.onItemLongClick(fileAdapterItem);
            return true;
        }
    }

    static class FileAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView fileName;
        ImageView fileIcon;

        FileAdapterViewHolder(View itemView) {
            super(itemView);
            fileIcon = itemView.findViewById(R.id.file_icon);
            fileName = itemView.findViewById(R.id.file_name);
        }
    }
}
