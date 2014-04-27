package org.royrvik.capgeminiemr.utils;

import java.util.ArrayList;

public class Authenticator {
    public static boolean AuthenticateWithLdap(String username, String password, ArrayList<String> ldapSettings) {
        String[] params = new String[6];
        params[0] = username;
        params[1] = password;
        params[2] = ldapSettings.get(0);
        params[3] = ldapSettings.get(1);
        params[4] = ldapSettings.get(2);
        params[5] = ldapSettings.get(3);
        try{
            boolean authenticated = new AuthenticateWithLdapTask().execute(params).get();

            return authenticated;
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return false;
    }
}