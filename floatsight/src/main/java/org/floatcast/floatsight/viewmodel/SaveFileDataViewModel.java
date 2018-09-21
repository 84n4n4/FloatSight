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

package org.floatcast.floatsight.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.IntDef;

import org.floatcast.floatsight.data.SaveFileUriHolder;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

@SuppressWarnings("PMD.FieldDeclarationsShouldBeAtStartOfClass")
public class SaveFileDataViewModel extends DataViewModel<SaveFileUriHolder> {
    @Retention(SOURCE)
    @IntDef({STATUS_SAVE_SUCCESS, STATUS_SAVE_READ_ERROR, STATUS_SAVE_WRITE_ERROR})
    public @interface SaveFileStatus {}
    public static final int STATUS_SAVE_SUCCESS = 0;
    public static final int STATUS_SAVE_READ_ERROR = 1;
    public static final int STATUS_SAVE_WRITE_ERROR = 2;

    private final MutableLiveData<SaveFileUriHolder> savedFileUri;

    public SaveFileDataViewModel() {
        savedFileUri = new MutableLiveData<>();
    }

    @Override
    public LiveData<SaveFileUriHolder> getLiveData() {
        return savedFileUri;
    }

    @Override
    public MutableLiveData<SaveFileUriHolder> getMutableLiveData() {
        return savedFileUri;
    }

    @Override
    public boolean containsValidData() {
        return savedFileUri.getValue() != null;
    }

}
