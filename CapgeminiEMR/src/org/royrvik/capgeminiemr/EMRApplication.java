package org.royrvik.capgeminiemr;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class EMRApplication extends Application {
    private String settingsPackageName, settingsPackageServer, settingsHospitalServer, settingsProtocol;


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

        // Initialize fields
        settingsHospitalServer = "";
        settingsPackageName = "";
        settingsHospitalServer = "";
        settingsProtocol = "";

    }

    public void setExternalPackageSettings(String packageName, String packageServer, String hospitalServer, String protocol) {
        this.settingsPackageName = packageName;
        this.settingsHospitalServer = packageServer;
        this.settingsHospitalServer = hospitalServer;
        this.settingsProtocol = protocol;

        // Get SharedPreferences and create editor for manipulating preferences
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        SharedPreferences.Editor preferencesEditor = appSharedPrefs.edit();
        // Add preferences
        preferencesEditor.putString("packageName", settingsPackageName);
        preferencesEditor.putString("packageServer", settingsPackageServer);
        preferencesEditor.putString("hospitalServer", settingsHospitalServer);
        preferencesEditor.putString("protocol", settingsProtocol);
        // Commit changes
        preferencesEditor.commit();

    }

    public String getSettingsPackageName() {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        String s = appSharedPrefs.getString("packageName", "");

        return s;
    }

    public String getSettingsPackageServer() {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        String s = appSharedPrefs.getString("packageServer", "");

        return s;
    }

    public String getSettingsHospitalServer() {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        String s = appSharedPrefs.getString("hospitalServer", "");

        return s;
    }

    public String getSettingsProtocol() {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        String s = appSharedPrefs.getString("protocol", "");

        return s;
    }
}
