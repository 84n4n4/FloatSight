
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

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileAdapterViewHolder> {
	private FileItemClickListener fileItemClickListener;
	List<FileAdapterItem> fileAdapterItems;

	FileAdapter(List<FileAdapterItem> fileAdapterItems) {
		this.fileAdapterItems = fileAdapterItems;
	}

	public void setFileItemClickListener(FileItemClickListener fileItemClickListener) {
		this.fileItemClickListener = fileItemClickListener;
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
		holder.itemView.setOnClickListener(new ViewOnClickListener(fileAdapterItems.get(position)));
	}

	@Override
	public int getItemCount() {
		return fileAdapterItems.size();
	}

	private class ViewOnClickListener implements View.OnClickListener {
		FileAdapterItem fileAdapterItem;

		ViewOnClickListener(FileAdapterItem fileAdapterItem) {
			this.fileAdapterItem = fileAdapterItem;
		}

		@Override
		public void onClick(View v) {
			fileItemClickListener.onItemClick(fileAdapterItem);
		}
	}

	class FileAdapterViewHolder extends RecyclerView.ViewHolder {
		TextView fileName;
		FileAdapterViewHolder(View itemView) {
			super(itemView);
			fileName = itemView.findViewById(R.id.file_name);
		}
	}

	public interface FileItemClickListener {
		void onItemClick(FileAdapterItem fileAdapterItem);
	}
}
