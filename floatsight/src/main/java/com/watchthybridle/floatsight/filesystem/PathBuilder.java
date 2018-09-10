package com.watchthybridle.floatsight.filesystem;

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;

public class PathBuilder {

    public static final File ROOT_DIR = new File(Environment.getExternalStorageDirectory(), "FloatSight");

    private static final File TRACKS_DIR = new File(ROOT_DIR, "tracks");

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
