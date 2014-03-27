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
        validate();
    }

    /**
     * Checks if the session is still valid
     * @return - true if the session is valid
     */
    public boolean isValid() {
        return pref.getLong(KEY_TIME, 0) != 0 && new Date().getTime() < pref.getLong(KEY_TIME, 0) + SESSION_TIMEOUT_IN_MIN*60000;
    }

    /**
     *  Tries to validate the user input, and starts the session if successful
     */
    private void validate() {
        String username = pref.getString(KEY_NAME, "");
        String passwordHash = pref.getString(KEY_PASS, "");

        //FOR TESTING
        if (username.equals("a")) {
            startNewSession();
            return;
        }
        //END FOR TESTING

        if (username.equals("") || Encryption.decrypt(username, passwordHash).equals("")) {
            return;
        }
        else if (Authenticator.AuthenticateWithLdap( username, Encryption.decrypt(username, passwordHash))) {
            startNewSession();
        }
    }

    /**
     * Sets the session start time
     */
    private void startNewSession() {
        editor.putLong(KEY_TIME, new Date().getTime());
        editor.commit();
    }

    /**
     *  Clears out all session information
     */
    public void logout() {
        editor.clear();
        editor.commit();
    }
}
