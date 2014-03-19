package org.royrvik.capgeminiemr.utils;


import android.os.AsyncTask;
import android.util.Log;
import com.unboundid.ldap.sdk.*;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustAllTrustManager;

import javax.net.ssl.SSLSocketFactory;
import java.security.GeneralSecurityException;

class AuthenticateWithLdapTask extends AsyncTask<String, Void, Boolean> {

    protected Boolean doInBackground(String... userInformation){
        try {
            SSLUtil sslUtil = new SSLUtil(new TrustAllTrustManager());
            SSLSocketFactory socketFactory;
            socketFactory = sslUtil.createSSLSocketFactory();

            //Connecting to ldap-server
            LDAPConnection connection = new LDAPConnection(socketFactory, "at.ntnu.no", 636);

            //Authenticating (Will cast exception if authentication fails)
            connection.bind("uid="+userInformation[0]+",ou=people,dc=ntnu,dc=no", userInformation[1]);

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
