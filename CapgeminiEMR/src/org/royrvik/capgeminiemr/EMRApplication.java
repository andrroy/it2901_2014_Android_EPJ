package org.royrvik.capgeminiemr;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EMRApplication extends Application {

    public final static String PACKAGE_NAME = "packageName";
    public final static String PACKAGE_LOCATION = "packageLocation";
    public final static String PACKAGE_SERVER_PORT = "packageServerPort";
    public final static String HOSPITAL_SERVER_ADDRESS = "hospitalServerAddress";
    public final static String HOSPITAL_SERVER_PROTOCOL = "hospitalServerProtocol";
    public final static String HOSPITAL_SERVER_PORT = "hospitalServerPort";
    public final static String AUTHENTICATION_PROTOCOL = "authenticationProtocol";
    public final static String AUTHENTICATION_SERVER_ADDRESS = "authenticationServerAddress";
    public final static String AUTHENTICATION_SERVER_PORT = "authenticationServerPort";
    public final static String LDAP_USERID = "LDAPuserID";
    public final static String LDAP_OU = "LDAPOU";
    public final static String LDAP_DC = "LDAPDC";



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
        preferencesEditor.putString(PACKAGE_LOCATION, settingsHashMap.get(PACKAGE_LOCATION));
        preferencesEditor.putString(PACKAGE_SERVER_PORT, settingsHashMap.get(PACKAGE_SERVER_PORT));
        preferencesEditor.putString(PACKAGE_SERVER_PORT, settingsHashMap.get(PACKAGE_SERVER_PORT));
        preferencesEditor.putString(HOSPITAL_SERVER_ADDRESS, settingsHashMap.get(HOSPITAL_SERVER_ADDRESS));
        preferencesEditor.putString(HOSPITAL_SERVER_PROTOCOL, settingsHashMap.get(HOSPITAL_SERVER_PROTOCOL));
        preferencesEditor.putString(HOSPITAL_SERVER_PORT, settingsHashMap.get(HOSPITAL_SERVER_PORT));
        preferencesEditor.putString(AUTHENTICATION_PROTOCOL, settingsHashMap.get(AUTHENTICATION_PROTOCOL));
        preferencesEditor.putString(AUTHENTICATION_SERVER_ADDRESS, settingsHashMap.get(AUTHENTICATION_SERVER_ADDRESS));
        preferencesEditor.putString(AUTHENTICATION_SERVER_PORT, settingsHashMap.get(AUTHENTICATION_SERVER_PORT));
        preferencesEditor.putString(LDAP_USERID, settingsHashMap.get(LDAP_USERID));
        preferencesEditor.putString(LDAP_OU, settingsHashMap.get(LDAP_OU));
        preferencesEditor.putString(LDAP_DC, settingsHashMap.get(LDAP_DC));


        // Commit changes
        preferencesEditor.commit();

    }

    public void clearSharedPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        // Delete old preferences before adding new
        editor.clear();
        editor.commit();
    }

    public Map<String, ?> getAllPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getAll();
    }


    public ArrayList<String> getLDAPStrings() {
        ArrayList<String> strings = new ArrayList<String>();
        strings.add(getSettingsLDAPDC());
        strings.add(getSettingsLDAPOU());
        strings.add(getSettingsAuthenticationServerAddress());
        return strings;
    }


    /**
     * Checks if app is currently set up with working settings
     * @return true if all necessary settings are present in sharedPreferences
     */
    public boolean hasSettingsConfigured(){

        //Check that "core" settings are specified
        if(
                !(getSettingsPackageName().isEmpty()) &&
                !(getSettingsPackageLocation().isEmpty()) &&
                !(getSettingsPackageServerPort().isEmpty()) &&
                !(getSettingsHospitalServerAddress().isEmpty()) &&
                !(getSettingsHospitalServerProtocol().isEmpty()) &&
                !(getSettingsHospitalServerPort().isEmpty()) &&
                !(getSettingsAuthenticationProtocol().isEmpty()) &&
                !(getSettingsAuthenticationServerAddress().isEmpty()) &&
                !(getSettingsAuthenticationServerPort().isEmpty())
        ) {

            //Check settings for specific authentication protocol
            //In this case, LDAP
            if (
                    !(getSettingsLDAPUserID().isEmpty()) &&
                            !(getSettingsLDAPOU().isEmpty()) &&
                            !(getSettingsLDAPDC().isEmpty())
                    ) {
                return true;
            }

        }
        //If some "core" settings NOT specified
        // OR authentication protocol NOT specified
        // return false
        return false;
    }

    public String getSettingsPackageName() {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        String s = appSharedPrefs.getString(PACKAGE_NAME, "");

        return s;
    }

    public String getSettingsPackageLocation() {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        String s = appSharedPrefs.getString(PACKAGE_LOCATION, "");

        return s;
    }

    public String getSettingsPackageServerPort() {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        String s = appSharedPrefs.getString(PACKAGE_SERVER_PORT, "");

        return s;
    }

    public String getSettingsHospitalServerAddress() {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        String s = appSharedPrefs.getString(HOSPITAL_SERVER_ADDRESS, "");

        return s;
    }

    public String getSettingsHospitalServerProtocol() {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        String s = appSharedPrefs.getString(HOSPITAL_SERVER_PROTOCOL, "");

        return s;
    }

    public String getSettingsHospitalServerPort() {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        String s = appSharedPrefs.getString(HOSPITAL_SERVER_PORT, "");

        return s;
    }

    public String getSettingsAuthenticationProtocol() {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        String s = appSharedPrefs.getString(AUTHENTICATION_PROTOCOL, "");

        return s;
    }

    public String getSettingsAuthenticationServerAddress() {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        String s = appSharedPrefs.getString(AUTHENTICATION_SERVER_ADDRESS, "");

        return s;
    }

    public String getSettingsAuthenticationServerPort() {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        String s = appSharedPrefs.getString(AUTHENTICATION_SERVER_PORT, "");

        return s;
    }

    public String getSettingsLDAPUserID() {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        String s = appSharedPrefs.getString(LDAP_USERID, "");

        return s;
    }

    public String getSettingsLDAPOU() {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        String s = appSharedPrefs.getString(LDAP_OU, "");

        return s;
    }

    public String getSettingsLDAPDC() {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        String s = appSharedPrefs.getString(LDAP_DC, "");

        return s;
    }

}
