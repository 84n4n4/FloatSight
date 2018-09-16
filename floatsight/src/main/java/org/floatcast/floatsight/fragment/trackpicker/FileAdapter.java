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

package org.floatcast.floatsight.fragment.trackpicker;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import org.floatcast.floatsight.R;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileAdapterItemViewHolder> {

    private List<FileAdapterItem> fileAdapterItems;
    FileAdapterItemClickListener itemClickListener;

    public void setItemClickListener(FileAdapterItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void update(List<FileAdapterItem> fileAdapterItems) {
        this.fileAdapterItems = fileAdapterItems;
        sort();
        notifyDataSetChanged();
    }

    @Override
    @NonNull
    public FileAdapterItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vh_file_adapter_item, parent, false);
        return new FileAdapterItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FileAdapterItemViewHolder holder, int position) {
        FileAdapterItem item = fileAdapterItems.get(position);

        holder.fileName.setText(item.fileName);
        holder.fileName.setEnabled(item.isEnabled);

        if (item.isEnabled) {
            if (item.file.isDirectory()) {
                holder.fileIcon.setImageResource(R.drawable.folder_file_picker);
            } else {
                holder.fileIcon.setImageResource(R.drawable.suit_file_picker);
            }
        } else {
            holder.fileIcon.setImageResource(R.drawable.no_track_file_picker);
        }

        OnItemViewClickListener onClickListener = new OnItemViewClickListener(item);
        holder.itemView.setOnClickListener(onClickListener);
        holder.itemView.setOnLongClickListener(onClickListener);
    }

    @Override
    public int getItemCount() {
        return fileAdapterItems.size();
    }

    public boolean contains(String fileName) {
        for (FileAdapterItem item : fileAdapterItems) {
            if (item.fileName.equals(fileName)) {
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
            sort();
            notifyItemChanged(fileAdapterItems.indexOf(item));
        }
    }

    public boolean remove(FileAdapterItem item) {
        int position = fileAdapterItems.indexOf(item);

        if (item.file.isDirectory()) {
            for (File file : item.file.listFiles()) {
                if (!file.delete()) {
                    Log.e("REMOVE", "Removing " + item.file.getAbsolutePath()
                            + " failed, removing form adapter anyway.");
                }
            }
        }
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

    private void sort() {
        Collections.sort(fileAdapterItems, new Comparator<FileAdapterItem>() {
            @Override
            public int compare(FileAdapterItem fileItem1, FileAdapterItem fileItem2) {
                return fileItem1.fileName.compareTo(fileItem2.fileName);
            }
        });
    }

    static class FileAdapterItemViewHolder extends RecyclerView.ViewHolder {

        TextView fileName;
        ImageView fileIcon;

        FileAdapterItemViewHolder(View itemView) {
            super(itemView);
            fileIcon = itemView.findViewById(R.id.file_icon);
            fileName = itemView.findViewById(R.id.file_name);
        }
    }

    private class OnItemViewClickListener implements View.OnClickListener, View.OnLongClickListener {

        FileAdapterItem fileAdapterItem;

        OnItemViewClickListener(FileAdapterItem fileAdapterItem) {
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

    public interface FileAdapterItemClickListener {

        void onItemClick(FileAdapterItem fileAdapterItem);

        void onItemLongClick(FileAdapterItem fileAdapterItem);
    }
}
