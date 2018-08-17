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

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.net.Uri;
import com.watchthybridle.floatsight.csvparser.FlySightTrackData;

public class FlySightTrackDataViewModel extends ViewModel {
    private FlySightTrackDataRepository trackDataRepository;
    private LiveData<FlySightTrackData> flySightTrackDataLiveData;

    public FlySightTrackDataViewModel(FlySightTrackDataRepository trackDataRepository) {
        this.trackDataRepository = trackDataRepository;
        flySightTrackDataLiveData = trackDataRepository.init();
    }

    public LiveData<FlySightTrackData> getFlySightTrackDataLiveData() {
        return flySightTrackDataLiveData;
    }

    public void loadFromFile(Uri uri) {
        flySightTrackDataLiveData = trackDataRepository.getFlySightTrackData(uri);
    }
}
