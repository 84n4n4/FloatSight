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
import com.watchthybridle.floatsight.csvparser.FlySightTrackPoint;
import com.watchthybridle.floatsight.data.FlySightTrackData;
import com.watchthybridle.floatsight.mpandroidchart.linedatasetcreation.TrackPointValueProvider;

import java.util.ArrayList;
import java.util.List;

import static com.watchthybridle.floatsight.data.ParsableData.PARSING_FAIL;

public class FlySightTrackDataViewModel extends DataViewModel<FlySightTrackData> {

    private MutableLiveData<FlySightTrackData> flySightTrackDataLiveData;

    public FlySightTrackDataViewModel() {
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

    @Override
    public boolean containsValidData() {
        return flySightTrackDataLiveData.getValue() != null
                && !flySightTrackDataLiveData.getValue().getFlySightTrackPoints().isEmpty()
                && flySightTrackDataLiveData.getValue().getParsingStatus() != PARSING_FAIL;
    }

    public void crop(float start, float end, TrackPointValueProvider valueProvider) {
        if(start > end) {
            float tmp = start;
            start = end;
            end = tmp;
        }
        int startIndex = 0;
        int endIndex = flySightTrackDataLiveData.getValue().getFlySightTrackPoints().size();
        for(FlySightTrackPoint point : flySightTrackDataLiveData.getValue().getFlySightTrackPoints()) {
            if(valueProvider.getValue(point) <= start) {
                startIndex = flySightTrackDataLiveData.getValue().getFlySightTrackPoints().indexOf(point);
            }
            if(valueProvider.getValue(point) <= end) {
                endIndex = flySightTrackDataLiveData.getValue().getFlySightTrackPoints().indexOf(point);
            }
        }

        List<FlySightTrackPoint> subListCopy = new ArrayList<>(flySightTrackDataLiveData.getValue().getFlySightTrackPoints().subList(startIndex, endIndex));
        FlySightTrackData cropped = new FlySightTrackData(subListCopy, flySightTrackDataLiveData.getValue().getSourceFileName());
        flySightTrackDataLiveData.setValue(cropped);
    }
}
