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

package org.floatcast.floatsight.filesystem;

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;

public final class PathBuilder {

    public static final String FLOAT_SIGHT_FOLDER_NAME = "FloatSight";
    public static final String TRACKS_FOLDER_NAME = "tracks";
    public static final String EXAMPLE_TRACKS_FOLDER_NAME = "Example";
    public static final File ROOT_DIR = new File(Environment.getExternalStorageDirectory(), FLOAT_SIGHT_FOLDER_NAME);
    public static final File TRACKS_DIR = new File(ROOT_DIR, TRACKS_FOLDER_NAME);
    public static final File EXAMPLE_TRACKS_DIR = new File(TRACKS_DIR, EXAMPLE_TRACKS_FOLDER_NAME);

    private PathBuilder() {
    }

    public static File getTracksFolder() throws FileNotFoundException {
        return createFolder(TRACKS_DIR);
    }

    public static File getExampleTracksFolder() throws FileNotFoundException {
        return createFolder(EXAMPLE_TRACKS_DIR);
    }

    private static File createFolder(File folder) throws FileNotFoundException {
        if (!folder.exists()) {
            folder.mkdirs();
        }
        if (!folder.exists() || !folder.isDirectory()) {
            throw new FileNotFoundException("Could not access folder on local storage");
        }
        return folder;
    }
}
