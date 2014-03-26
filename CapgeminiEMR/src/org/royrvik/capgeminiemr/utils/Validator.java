package org.royrvik.capgeminiemr.utils;

import org.royrvik.capgeminiemr.EMRApplication;

import java.util.HashMap;

public class Validator {

    public static boolean validateSettings(HashMap<String, String> settingsHashMap){
        try{
            if(
                    !(settingsHashMap.get(EMRApplication.PACKAGE_NAME).isEmpty()) &&
                            !(settingsHashMap.get(EMRApplication.PACKAGE_LOCATION).isEmpty()) &&
                            !(settingsHashMap.get(EMRApplication.PACKAGE_SERVER_PORT).isEmpty()) &&
                            !(settingsHashMap.get(EMRApplication.HOSPITAL_SERVER_ADDRESS).isEmpty()) &&
                            !(settingsHashMap.get(EMRApplication.HOSPITAL_SERVER_PROTOCOL).isEmpty()) &&
                            !(settingsHashMap.get(EMRApplication.HOSPITAL_SERVER_PORT).isEmpty()) &&
                            !(settingsHashMap.get(EMRApplication.AUTHENTICATION_PROTOCOL).isEmpty()) &&
                            !(settingsHashMap.get(EMRApplication.AUTHENTICATION_SERVER_ADDRESS).isEmpty()) &&
                            !(settingsHashMap.get(EMRApplication.AUTHENTICATION_SERVER_PORT).isEmpty())
                    ){
                return true;
            }
            return false;
        }
        catch (NullPointerException e){
            return false;
        }
    }
}
