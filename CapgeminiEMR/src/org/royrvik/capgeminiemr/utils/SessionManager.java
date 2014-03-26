package org.royrvik.capgeminiemr.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import java.util.Date;

/**
 * Created by Joakim on 26.03.2014.
 */
public class SessionManager {

    private static final int SESSION_TIMEOUT_IN_MIN = 10;
    private static final String PREF_CODE = "find a better code?";
    private static final String KEY_NAME = "NameKeyString";
    private static final String KEY_PASS = "PasswordKeyString";
    private static final String KEY_TIME = "SessionStartTime";

    private SharedPreferences pref;
    private Editor editor;

    /**
     *
     * @param context
     */
    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_CODE, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    /**
     *
     * @param name
     * @param password
     */
    public void createLoginSession(String name, String password) {
        editor.clear();
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_PASS, password);
        editor.commit();
        authenticate();
    }

    /**
     * Checks if the session is still valid
     * @return
     */
    public boolean checkSession() {
        return pref.getLong(KEY_TIME, 0) != 0 && new Date().getTime() < pref.getLong(KEY_TIME, 0) + SESSION_TIMEOUT_IN_MIN*60000;
    }

    /**
     *
     */
    private void authenticate() {
        //FOR TESTING
        if (pref.getString(KEY_NAME, "").equals("a")) {
            //Setting session start time
            editor.putLong(KEY_TIME, new Date().getTime());
            editor.commit();
            return;
        }
        //END FOR TESTING

        if (pref.getString(KEY_NAME, "").equals("") || pref.getString(KEY_PASS, "").equals("")) {
            return;
        }
        else if (Authenticator.AuthenticateWithLdap(
                pref.getString(KEY_NAME, ""),
                Encryption.decrypt(pref.getString(KEY_NAME, ""), pref.getString(KEY_PASS, "")))) {

            //Setting session start time
            editor.putLong(KEY_TIME, new Date().getTime());
            editor.commit();
        }
    }

    /**
     *
     */
    public void logout() {
        editor.clear();
        editor.commit();
    }
}
