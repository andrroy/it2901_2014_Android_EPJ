package org.royrvik.capgeminiemr.preferences;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import org.royrvik.capgeminiemr.EMRApplication;

import java.io.File;

/**
 * Dialog box with two options (positive and negative) used in settings.xml
 */

public class YesNoPreference extends DialogPreference {
    private EMRApplication globalApp;

    public YesNoPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        globalApp = (EMRApplication) context.getApplicationContext();
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            // Delete everything
            Log.d("APP", "Delete everything");
            globalApp.clearSharedPreferences();
            clearApplicationData();
        }
        else {
            Log.d("APP", "Delete nothing");
        }
    }

    /**
     * Clears the application cache.
     */
    public void clearApplicationData() {
        File cache = globalApp.getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));Log.i("TAG", "**************** File /data/data/APP_PACKAGE/" + s + " DELETED *******************");
                }
            }
        }
    }

    /**
     * Tries to delete a {@linkplain java.io.File}.
     * @param dir The {@linkplain java.io.File} to delete.
     * @return True if the {@linkplain java.io.File} was deleted.
     */
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
}