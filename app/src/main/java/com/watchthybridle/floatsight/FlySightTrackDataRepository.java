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
import android.arch.lifecycle.MutableLiveData;
import android.content.ContentResolver;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import com.watchthybridle.floatsight.csvparser.FlySightCsvParser;
import com.watchthybridle.floatsight.csvparser.FlySightTrackData;

import java.io.InputStream;

public class FlySightTrackDataRepository {
    private ContentResolver contentResolver;
    private MutableLiveData<FlySightTrackData> data;

    public FlySightTrackDataRepository(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
        data = new MutableLiveData<>();
    }

    public LiveData<FlySightTrackData> init() {
        data.setValue(new FlySightTrackData());
        return data;
    }

    public LiveData<FlySightTrackData> getFlySightTrackData(Uri uri) {
        new ParseFileTask().execute(uri);
        return data;
    }

    public class ParseFileTask extends AsyncTask<Uri, Integer, Long> {
        private FlySightTrackData flySightTrackData = new FlySightTrackData();

        protected Long doInBackground(Uri... uris) {
            if(uris.length > 0) {
                try {
                    InputStream inputStream = contentResolver.openInputStream(uris[0]);
                    FlySightCsvParser flySightCsvParser = new FlySightCsvParser(inputStream);
                    flySightTrackData = flySightCsvParser.read();
                } catch (Exception e) {
                    flySightTrackData.setParsingStatus(FlySightTrackData.PARSING_FAIL);
                }
            } else {
                flySightTrackData.setParsingStatus(FlySightTrackData.PARSING_FAIL);
            }
            return 0L;
        }

        protected void onPostExecute(Long result) {
            data.setValue(flySightTrackData);
        }
    }

}
