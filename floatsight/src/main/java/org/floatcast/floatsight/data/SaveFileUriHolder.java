package org.floatcast.floatsight.data;


import android.net.Uri;

import org.floatcast.floatsight.viewmodel.SaveFileDataViewModel;

@SuppressWarnings("PMD.FieldDeclarationsShouldBeAtStartOfClass")
public class SaveFileUriHolder {
	public Uri uri;
	@SaveFileDataViewModel.SaveFileStatus public int status;
}