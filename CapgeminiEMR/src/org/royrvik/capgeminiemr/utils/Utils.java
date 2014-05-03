package org.royrvik.capgeminiemr.utils;

import android.util.Log;

public class Utils {

    public static String ssnToDateOfBirth(String ssn) {
        String dateOfBirth = ssn.substring(0, 6);
        String dateOfBirthFormatted = "";
        for (int i = 0; i < dateOfBirth.length(); i++) {
            dateOfBirthFormatted += dateOfBirth.charAt(i);
            if (i==1 || i==3) {
                dateOfBirthFormatted += ".";
            }
        }
        return dateOfBirthFormatted;
    }
}
