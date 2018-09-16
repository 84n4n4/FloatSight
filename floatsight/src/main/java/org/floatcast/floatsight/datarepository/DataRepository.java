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

package org.floatcast.floatsight.datarepository;

import android.arch.lifecycle.MutableLiveData;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Process;
import android.provider.OpenableColumns;
import org.floatcast.floatsight.Parser;
import org.floatcast.floatsight.data.ParsableData;
import org.floatcast.floatsight.viewmodel.DataViewModel;

import java.io.IOException;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;
import static android.os.Process.THREAD_PRIORITY_MORE_FAVORABLE;
import static org.floatcast.floatsight.data.ParsableData.*;

public class DataRepository<T extends ParsableData> {

    private Class<T> parsableDataClass;
    private ContentResolver contentResolver;
    private Parser<T> parser;

    public DataRepository(Class<T> parsableDataClass, ContentResolver contentResolver, Parser<T> parser) {
        this.parsableDataClass = parsableDataClass;
        this.contentResolver = contentResolver;
        this.parser = parser;
    }

    public void load(Uri uri, DataViewModel<T> viewModel) {
        try {
            new ParseFileTask<>(parsableDataClass, contentResolver, parser, viewModel.getMutableLiveData()).execute(uri);
        } catch (IllegalAccessException | InstantiationException e) {
            throw new IllegalArgumentException("Could not instantiate class: " + parsableDataClass.getSimpleName());
        }
    }

    private static class ParseFileTask<T extends ParsableData> extends AsyncTask<Uri, Integer, Long> {

        private T data;
        private ContentResolver contentResolver;
        private Parser<T> parser;
        private MutableLiveData<T> liveData;

        ParseFileTask(Class<T> parsableDataClass, ContentResolver contentResolver, Parser<T> parser,
                      MutableLiveData<T> liveData) throws IllegalAccessException, InstantiationException {

            data = parsableDataClass.newInstance();
            this.contentResolver = contentResolver;
            this.parser = parser;
            this.liveData = liveData;
        }

        @Override
        protected Long doInBackground(Uri... uris) {
            Process.setThreadPriority(THREAD_PRIORITY_BACKGROUND + THREAD_PRIORITY_MORE_FAVORABLE);
            if (uris.length > 0) {
                try {
                    data.setParsingStatus(PARSING_SUCCESS);
                    data = parser.read(contentResolver.openInputStream(uris[0]));
                } catch (IOException e) {
                    e.printStackTrace();
                    data.setParsingStatus(PARSING_FAIL);
                } finally {
                    data.setSourceFileName(resolveFileName(uris[0]));
                }
            } else {
                data.setParsingStatus(PARSING_FAIL);
            }
            return 0L;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            liveData.setValue(data);
        }

        private String resolveFileName(Uri uri) {
            String result = null;

            if (uri.getScheme().equals("content")) {
                try (Cursor cursor = contentResolver.query(uri, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                }
            }

            if (result == null) {
                result = uri.getPath();
                int cut = result.lastIndexOf('/');
                if (cut != -1) {
                    result = result.substring(cut + 1);
                }
            }

            return result;
        }
    }
}
