package org.royrvik.capgeminiemr.preferences;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import org.royrvik.capgeminiemr.EMRApplication;
import org.royrvik.capgeminiemr.database.DatabaseHelper;

public class YesNoPreference extends DialogPreference {
    private Context context;
    private DatabaseHelper dbHelper;
    private EMRApplication globalApp;



    public YesNoPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        this.context = context;
        dbHelper = new DatabaseHelper(context);
        globalApp = (EMRApplication) context.getApplicationContext();

    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            // Delete everything
            Log.d("APP", "Slett alt");
            dbHelper.deleteAllExaminations();
            globalApp.clearSharedPreferences();

        }
        else {
            Log.d("APP", "Slett ingenting");
        }
    }
}