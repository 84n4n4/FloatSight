package com.watchthybridle.floatsight;

import android.support.annotation.StringRes;

public class MainMenuButtonItem {
	@StringRes int title;
	@StringRes int description;

	public MainMenuButtonItem(@StringRes int title, @StringRes int description) {
		this.title = title;
		this.description = description;
	}
}