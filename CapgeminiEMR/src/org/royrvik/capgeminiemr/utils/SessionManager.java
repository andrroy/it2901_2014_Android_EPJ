package org.royrvik.capgeminiemr.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.concurrent.TimeUnit;

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
        editor.clear();
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_PASS, password);
        editor.commit();
    }

    public boolean checkLogin() {
        //FOR TESTING
        if (pref.getString(KEY_NAME, "").equals("a")) return true;
        //END FOR TESTING

        if (pref.getString(KEY_NAME, "").equals("") || pref.getString(KEY_PASS, "").equals("")) {
            return false;
        }
        System.out.println("Username: "+pref.getString(KEY_NAME, ""));
        System.out.println("Password Hash: "+ pref.getString(KEY_PASS, ""));
        return Authenticator.AuthenticateWithLdap(
                pref.getString(KEY_NAME, ""),
                Encryption.decrypt(pref.getString(KEY_NAME, ""), pref.getString(KEY_PASS, "")));
    }

    public void logout() {
        editor.clear();
        editor.commit();
    }
}
