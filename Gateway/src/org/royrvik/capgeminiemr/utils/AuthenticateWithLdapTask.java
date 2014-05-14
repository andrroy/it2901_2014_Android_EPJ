package org.royrvik.capgeminiemr.utils;


import android.os.AsyncTask;
import android.util.Log;
import com.unboundid.ldap.sdk.*;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustAllTrustManager;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.security.GeneralSecurityException;
import java.util.regex.Pattern;

class AuthenticateWithLdapTask extends AsyncTask<String, Void, Boolean> {

    protected Boolean doInBackground(String... userInformation){
        //  userInformation Layout:
        //  0-username
        //  1-passwordHash
        //  2-ldapDC
        //  3-ldapOU
        //  4-ldapAddress
        //  5-ldapPort
        //  6-Protocol (ldaps/ldap)

        String[] ldapDC = userInformation[2].split(Pattern.quote("."));
        String ldapDC1 = ldapDC[0];
        String ldapDC2 = ldapDC[1];
        String ldapOU = userInformation[3];
        String ldapAddress = userInformation[4];
        int ldapPort = Integer.parseInt(userInformation[5]);
        String protocol = userInformation[6];

        try {

            LDAPConnection connection;

            //If ldaps
            if(protocol.equals("ldaps")){
                SSLUtil sslUtil = new SSLUtil(new TrustAllTrustManager());
                SSLSocketFactory socketFactory;
                socketFactory = sslUtil.createSSLSocketFactory();
                connection = new LDAPConnection(socketFactory, ldapAddress, ldapPort);
            }
            //Regular ldap
            else if(protocol.equals("ldap")){
                connection = new LDAPConnection();
                connection.connect(ldapAddress, ldapPort);
            }
            //Something is probably specified wrong in settings
            else{
                return false;
            }

            //Authenticating (Will cast exception if authentication fails)
            connection.bind("uid="+userInformation[0]+",ou="+ ldapOU +",dc="+ ldapDC1 +",dc="+ ldapDC2,
                    Encryption.decrypt(userInformation[0], userInformation[1]));

            return true;
        }
        catch (LDAPException e) {
            return false;
        }
        catch (Exception e) {
            return false;

        }


    }
}
