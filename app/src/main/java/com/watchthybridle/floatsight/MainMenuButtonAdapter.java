package com.watchthybridle.floatsight;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

class MainMenuButtonAdapter extends ArrayAdapter<MainMenuButtonItem> {
	MainMenuButtonAdapter(Context context, List<MainMenuButtonItem> objects) {
		super(context, 0, objects);
	}

	@Override
	public @NonNull	View getView(int position, View convertView, ViewGroup parent) {

		MainMenuButtonItem mainMenuButtonItem = getItem(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.main_menu_button, parent, false);
		}

		TextView title = convertView.findViewById(R.id.button_title);
		TextView description = convertView.findViewById(R.id.button_description);

		if(mainMenuButtonItem != null) {
			description.setText(mainMenuButtonItem.description);
			title.setText(mainMenuButtonItem.title);
		}
		return convertView;
	}
}
