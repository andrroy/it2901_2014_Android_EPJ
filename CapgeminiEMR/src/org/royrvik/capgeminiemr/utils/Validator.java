package org.royrvik.capgeminiemr.utils;

import org.royrvik.capgeminiemr.EMRApplication;

import java.util.HashMap;

public class Validator {

    /**
     * Utility for validating settings data before committing to sharedMemory
     * @param settingsHashMap
     * @return true if all necessary settings are present in hash map
    */
    public static boolean validateSettings(HashMap<String, String> settingsHashMap){
        try{
            //Checks if all "core" settings are in place
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

        //Exception occurs if some of the fields above does not exist
        catch (NullPointerException e){
            return false;
        }
    }
}
