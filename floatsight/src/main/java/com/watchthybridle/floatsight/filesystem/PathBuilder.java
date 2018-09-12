package com.watchthybridle.floatsight.filesystem;

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;

public class PathBuilder {

    public static final String FLOAT_SIGHT_FOLDER_NAME = "FloatSight";
    public static final String TRACKS_FOLDER_NAME = "tracks";
    public static final File ROOT_DIR = new File(Environment.getExternalStorageDirectory(), FLOAT_SIGHT_FOLDER_NAME);

    private static final File TRACKS_DIR = new File(ROOT_DIR, TRACKS_FOLDER_NAME);

    public static File getTracksFolder() throws FileNotFoundException {
        if (!TRACKS_DIR.exists()) {
            TRACKS_DIR.mkdirs();
        }
        if (!TRACKS_DIR.exists() || !TRACKS_DIR.isDirectory()) {
            throw new FileNotFoundException("Could not access folder on local storage");
        }
        return TRACKS_DIR;
    }
}
