package org.royrvik.capgeminiemr;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import org.royrvik.capgeminiemr.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EMRApplication extends Application {

    public final static String PACKAGE_SERVER_PORT = "packageServerPort";
    public final static String HOSPITAL_SERVER_ADDRESS = "hospitalServerAddress";
    public final static String HOSPITAL_SERVER_PROTOCOL = "hospitalServerProtocol";
    public final static String HOSPITAL_SERVER_PORT = "hospitalServerPort";
    public final static String AUTHENTICATION_PROTOCOL = "authenticationProtocol";
    public final static String AUTHENTICATION_SERVER_ADDRESS = "authenticationServerAddress";
    public final static String AUTHENTICATION_SERVER_PORT = "authenticationServerPort";

    //Service Package Strings
    public final static String PACKAGE_NAME = "packageName";
    public final static String PACKAGE_LOCATION = "packageLocation";

    //LDAP Strings
    public final static String LDAP_USERID = "LDAPuserID";
    public final static String LDAP_OU = "LDAPOU";
    public final static String LDAP_DC = "LDAPDC";

    //Shared user credentials
    private final static String DEPUSER = "departmentUsername";
    private final static String DEPPWD = "departmentPassword";
    private final static String TECHPWD = "techPassword";

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
        strings.add(getSettingsAuthenticationServerPort());
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

    public boolean hasDepartmentAuthConfigured() {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

        return !appSharedPrefs.getString(DEPUSER, "").equals("") && !appSharedPrefs.getString(DEPPWD, "").equals("");
    }

    public ArrayList<String> getDepartmentAuth() {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        ArrayList<String> result = new ArrayList<String>();
        result.add(appSharedPrefs.getString(DEPUSER, ""));
        result.add(appSharedPrefs.getString(DEPPWD, ""));
        return result;
    }

    public boolean isTechPasswordSet() {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        return !appSharedPrefs.getString(TECHPWD, "").equals("");
    }

    public boolean checkTechPassword(String password) {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

        return !password.equals("") && appSharedPrefs.getString(TECHPWD, "").equals(password);
    }

    public void saveTechPassword(String password) {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        appSharedPrefs.edit().putString(TECHPWD, password).commit();
    }

    public String getSettingsPackageName() {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

        return appSharedPrefs.getString(PACKAGE_NAME, "");
    }

    public String getSettingsPackageLocation() {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

        return appSharedPrefs.getString(PACKAGE_LOCATION, "");
    }

    public String getSettingsPackageServerPort() {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

        return appSharedPrefs.getString(PACKAGE_SERVER_PORT, "");
    }

    public String getSettingsHospitalServerAddress() {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

        return appSharedPrefs.getString(HOSPITAL_SERVER_ADDRESS, "");
    }

    public String getSettingsHospitalServerProtocol() {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

        return appSharedPrefs.getString(HOSPITAL_SERVER_PROTOCOL, "");
    }

    public String getSettingsHospitalServerPort() {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

        return appSharedPrefs.getString(HOSPITAL_SERVER_PORT, "");
    }

    public String getSettingsAuthenticationProtocol() {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

        return appSharedPrefs.getString(AUTHENTICATION_PROTOCOL, "");
    }

    public String getSettingsAuthenticationServerAddress() {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

        return appSharedPrefs.getString(AUTHENTICATION_SERVER_ADDRESS, "");
    }

    public String getSettingsAuthenticationServerPort() {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

        return appSharedPrefs.getString(AUTHENTICATION_SERVER_PORT, "");
    }

    public String getSettingsLDAPUserID() {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

        return appSharedPrefs.getString(LDAP_USERID, "");
    }

    public String getSettingsLDAPOU() {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

        return appSharedPrefs.getString(LDAP_OU, "");
    }

    public String getSettingsLDAPDC() {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

        return appSharedPrefs.getString(LDAP_DC, "");
    }

    public void setDepartmentAuth(String username, String password) {
        SharedPreferences.Editor pref = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).edit();
        pref.putString(DEPUSER, username);
        pref.putString(DEPPWD, password);
        pref.commit();
    }
}
