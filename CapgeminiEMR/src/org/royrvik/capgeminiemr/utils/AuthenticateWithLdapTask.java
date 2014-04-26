package org.royrvik.capgeminiemr.utils;


import android.os.AsyncTask;
import com.unboundid.ldap.sdk.*;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustAllTrustManager;

import javax.net.ssl.SSLSocketFactory;
import java.security.GeneralSecurityException;
import java.util.regex.Pattern;

class AuthenticateWithLdapTask extends AsyncTask<String, Void, Boolean> {

    private String ldapDC1;
    private String ldapDC2;
    private String ldapOU;
    private String ldapAddress;
    private int ldapPort;

    protected Boolean doInBackground(String... userInformation){
        //  userInformation Layout:
        //  0-username
        //  1-password
        //  2-ldapDC
        //  3-ldapOU
        //  4-ldapAddress
        //  5-ldapPort

        String[] ldapDC = userInformation[2].split(Pattern.quote("."));
        ldapDC1 = ldapDC[0];
        ldapDC2 = ldapDC[1];
        ldapOU = userInformation[3];
        ldapAddress = userInformation[4];
        ldapPort = Integer.parseInt(userInformation[5]);

        try {
            SSLUtil sslUtil = new SSLUtil(new TrustAllTrustManager());
            SSLSocketFactory socketFactory;
            socketFactory = sslUtil.createSSLSocketFactory();

            //Connecting to ldap-server
            LDAPConnection connection = new LDAPConnection(socketFactory, ldapAddress, ldapPort);

            //Authenticating (Will cast exception if authentication fails)
            connection.bind("uid="+userInformation[0]+",ou="+ldapOU+",dc="+ldapDC1+",dc="+ldapDC2, userInformation[1]);

            return true;
        }
        catch (GeneralSecurityException e) {
            e.printStackTrace();

        }
        catch (LDAPException e) {
            e.printStackTrace();
        }
        return false;
    }
}
