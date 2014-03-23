package org.royrvik.capgeminiemr;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashMap;
import java.util.Map;

public class EMRApplication extends Application {

    private final static String PACKAGE_NAME = "packageName";
    private final static String PACKAGE_SERVER = "packageServer";
    private final static String HOSPITAL_SERVER = "hospitalServer";
    private final static String PROTOCOL = "protocol";


    /*
        USAGE:
        private EMRApplication globalApp;
        .
        .
        globalApp = (EMRApplication) getApplicationContext();
        String olol = globalApp.getSettingsPackageName();


     */


    @Override
    public void onCreate() {
        super.onCreate();


    }

    public void setExternalPackageSettings(HashMap<String, String> settingsHashMap) {

        clearSharedPreferences();

        // Get SharedPreferences and create editor for manipulating preferences
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        SharedPreferences.Editor preferencesEditor = appSharedPrefs.edit();

        // Add preferences
        preferencesEditor.putString(PACKAGE_NAME, settingsHashMap.get(PACKAGE_NAME));
        preferencesEditor.putString(PACKAGE_SERVER, settingsHashMap.get(PACKAGE_SERVER));
        preferencesEditor.putString(HOSPITAL_SERVER, settingsHashMap.get(HOSPITAL_SERVER));
        preferencesEditor.putString(PROTOCOL, settingsHashMap.get(PROTOCOL));

        // Commit changes
        preferencesEditor.commit();

    }

    public void clearSharedPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    public Map<String, ?> getAllPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getAll();
    }

    public String getSettingsPackageName() {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        String s = appSharedPrefs.getString(PACKAGE_NAME, "");

        return s;
    }

    public String getSettingsPackageServer() {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        String s = appSharedPrefs.getString(PACKAGE_SERVER, "");

        return s;
    }

    public String getSettingsHospitalServer() {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        String s = appSharedPrefs.getString(HOSPITAL_SERVER, "");

        return s;
    }

    public String getSettingsProtocol() {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        String s = appSharedPrefs.getString(PROTOCOL, "");

        return s;
    }
}
