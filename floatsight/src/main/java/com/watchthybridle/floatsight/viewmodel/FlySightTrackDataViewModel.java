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

package com.watchthybridle.floatsight.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import com.watchthybridle.floatsight.data.FlySightTrackData;

public class FlySightTrackDataViewModel extends DataViewModel<FlySightTrackData> {

    private MutableLiveData<FlySightTrackData> flySightTrackDataLiveData;

    public FlySightTrackDataViewModel() {
        flySightTrackDataLiveData = new MutableLiveData<>();
    }

    public void clear() {
        flySightTrackDataLiveData = new MutableLiveData<>();
    }

    @Override
    public LiveData<FlySightTrackData> getLiveData() {
        return flySightTrackDataLiveData;
    }

    @Override
    public MutableLiveData<FlySightTrackData> getMutableLiveData() {
        return flySightTrackDataLiveData;
    }

    public boolean containsValidData() {
        return flySightTrackDataLiveData.getValue() != null
                && !flySightTrackDataLiveData.getValue().getFlySightTrackPoints().isEmpty()
                && flySightTrackDataLiveData.getValue().getParsingStatus() != FlySightTrackData.PARSING_FAIL;
    }
}
