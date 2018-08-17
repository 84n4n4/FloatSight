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

import android.arch.lifecycle.MutableLiveData;
import android.content.ContentResolver;
import android.net.Uri;
import android.os.AsyncTask;
import com.watchthybridle.floatsight.csvparser.FlySightCsvParser;
import com.watchthybridle.floatsight.csvparser.FlySightTrackData;

import java.io.InputStream;

public class FlySightTrackDataRepository {
    public MutableLiveData<FlySightTrackData> init() {
        MutableLiveData<FlySightTrackData> liveData = new MutableLiveData<>();
        liveData.setValue(new FlySightTrackData());
        return liveData;
    }

    public void loadFlySightTrackData(Uri uri, ContentResolver contentResolver, MutableLiveData<FlySightTrackData> liveData) {
        new ParseFileTask(contentResolver, liveData).execute(uri);
    }

    public static class ParseFileTask extends AsyncTask<Uri, Integer, Long> {
        private FlySightTrackData flySightTrackData = new FlySightTrackData();
        private ContentResolver contentResolver;
        private MutableLiveData<FlySightTrackData> liveData;

        ParseFileTask(ContentResolver contentResolver, MutableLiveData<FlySightTrackData> liveData) {
            this.contentResolver = contentResolver;
            this.liveData = liveData;
        }

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
            liveData.setValue(flySightTrackData);
        }
    }

}
