package org.royrvik.capgeminiemr.utils;

import android.util.Log;

public class Authenticator {
    public static boolean AuthenticateWithLdap(String username, String password){
        try{

            boolean authenticated = new AuthenticateWithLdapTask().execute(username,password).get();

            return authenticated;
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return false;
    }
}
