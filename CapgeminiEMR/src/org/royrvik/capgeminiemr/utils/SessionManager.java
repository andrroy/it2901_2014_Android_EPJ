package org.royrvik.capgeminiemr.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Created by Laxcor on 26.03.2014.
 */
public class SessionManager {

    private static final String PREF_CODE = "find a better code?";
    private static final String KEY_NAME = "NameKeyString";
    private static final String KEY_PASS = "PasswordKeyString";

    private SharedPreferences pref;
    private Editor editor;


    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_CODE, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(String name, String password) {
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_PASS, password);
    }

    public boolean checkLogin() {
        return Authenticator.AuthenticateWithLdap(pref.getString(KEY_NAME, ""), pref.getString(KEY_PASS, ""));
    }
}
