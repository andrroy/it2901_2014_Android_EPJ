package org.royrvik.capgeminiemr.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import org.royrvik.capgeminiemr.EMRApplication;
import java.util.Date;

public class SessionManager {

    private static final int SESSION_TIMEOUT_IN_MIN = 10;
    private static final String PREF_CODE = "find a better code?";
    private static final String KEY_NAME = "NameKeyString";
    private static final String KEY_PASS = "PasswordKeyString";
    private static final String KEY_TIME = "SessionStartTime";

    private SharedPreferences pref;
    private Editor editor;
    private Context context;

    /**
     * Constructor for the {@linkplain org.royrvik.capgeminiemr.utils.SessionManager}.
     * @param context The application {@linkplain android.content.Context} used for {@linkplain android.content.SharedPreferences}.
     */
    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_CODE, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    /**
     * Creates a new login session.
     * @param name The username entered by the user.
     * @param password The encrypted password entered by the user.
     */
    public void createLoginSession(String name, String password) {
        editor.clear();
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_PASS, password);
        editor.commit();
        validate();
    }

    /**
     * Checks if the session is still valid.
     * @return True if the session is valid.
     */
    public boolean isValid() {
        return NetworkChecker.isNetworkAvailable(context) && isSessionActive();
    }

    /**
     * Checks to see if the session has timed out.
     * @return True if the session is still active.
     */
    private boolean isSessionActive() {
        return pref.getLong(KEY_TIME, 0) != 0 && new Date().getTime() < pref.getLong(KEY_TIME, 0) + SESSION_TIMEOUT_IN_MIN*60000;
    }
    /**
     *  Tries to validate the user input, and starts the session if successful.
     */
    private void validate() {
        String username = pref.getString(KEY_NAME, "");
        String passwordHash = pref.getString(KEY_PASS, "");

        EMRApplication settings = (EMRApplication) context.getApplicationContext();
        if (!username.equals("")) {
            if (!Encryption.decrypt(username, passwordHash).equals("")) {
                if (Authenticator.AuthenticateWithLdap( username, Encryption.decrypt(username, passwordHash), settings.getLDAPStrings())) {
                    startNewSession();
                }
            }
        }
    }

    /**
     * Updates the session timer
     */
    public void updateSession() {
        Long prevTime = (pref.getLong(KEY_TIME, 0)+SESSION_TIMEOUT_IN_MIN*60000-new Date().getTime())/60000;
        validate();
        Long newtime = (pref.getLong(KEY_TIME, 0)+SESSION_TIMEOUT_IN_MIN*60000-new Date().getTime())/60000;
        Log.d("SESSION", "Session updated. Previous time: "+prevTime+" min. New time: "+ newtime.toString()+" min");
    }

    /**
     * Sets the session start time.
     */
    private void startNewSession() {
        editor.putLong(KEY_TIME, new Date().getTime());
        editor.commit();
    }

    /**
     *  Clears out all session information.
     */
    public void logout() {
        editor.clear();
        editor.commit();
    }
}
