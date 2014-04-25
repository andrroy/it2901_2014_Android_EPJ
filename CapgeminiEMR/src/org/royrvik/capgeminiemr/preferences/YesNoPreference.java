package org.royrvik.capgeminiemr.preferences;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import org.royrvik.capgeminiemr.EMRApplication;
import org.royrvik.capgeminiemr.database.DatabaseHelper;

/**
 * Dialog box with two options (positive and negative) used in settings.xml
 */

public class YesNoPreference extends DialogPreference {
    private Context context;
    private DatabaseHelper dbHelper;
    private EMRApplication globalApp;

    public YesNoPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        dbHelper = new DatabaseHelper(context);
        globalApp = (EMRApplication) context.getApplicationContext();
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            // Delete everything
            Log.d("APP", "Delete everything");
            dbHelper.deleteAllExaminations();
            globalApp.clearSharedPreferences();
        }
        else {
            Log.d("APP", "Delete nothing");
        }
    }
}