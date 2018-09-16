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
import org.floatcast.floatsight.csvparser.FlySightTrackPoint;
import org.floatcast.floatsight.data.FlySightTrackData;
import org.floatcast.floatsight.mpandroidchart.linedatasetcreation.TrackPointValueProvider;

import java.util.ArrayList;
import java.util.List;

import static org.floatcast.floatsight.data.ParsableData.PARSING_FAIL;

public class FlySightTrackDataViewModel extends DataViewModel<FlySightTrackData> {

    private final MutableLiveData<FlySightTrackData> flySightTrackDataLiveData;

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

    @SuppressWarnings("PMD.AvoidReassigningParameters")
    public void crop(float start, float end, TrackPointValueProvider valueProvider) {
        if(start > end) {
            float tmp = start;
            start = end;
            end = tmp;
        }
        List<FlySightTrackPoint> flySightTrackPoints = flySightTrackDataLiveData.getValue().getFlySightTrackPoints();
        int startIndex = 0;
        int endIndex = flySightTrackPoints.size();
        for(FlySightTrackPoint point : flySightTrackPoints) {
            if(valueProvider.getValue(point) <= start) {
                startIndex = flySightTrackPoints.indexOf(point);
            }
            if(valueProvider.getValue(point) <= end) {
                endIndex = flySightTrackPoints.indexOf(point);
            }
        }
        if(startIndex == endIndex) {
            return;
        }

        List<FlySightTrackPoint> subListCopy = new ArrayList<>(flySightTrackPoints.subList(startIndex, endIndex));
        FlySightTrackData cropped = new FlySightTrackData(subListCopy, flySightTrackDataLiveData.getValue().getSourceFileName());
        float timeOffset = cropped.getFlySightTrackPoints().get(0).trackTimeInSeconds;
        float distanceOffset = cropped.getFlySightTrackPoints().get(0).distance;
        cropped.offsetDistanceAndTime(distanceOffset, timeOffset);
        flySightTrackDataLiveData.setValue(cropped);
    }
}
