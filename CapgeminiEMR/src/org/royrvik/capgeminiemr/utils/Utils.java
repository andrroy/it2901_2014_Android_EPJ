package org.royrvik.capgeminiemr.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Utils {

    /**
     * Converts a norwegian SSN number to a more readable date of birth string
     *
     * @param ssn SSN string to convert to date of birth
     * @return Date or birth formatted as "dd.mm.yy"
     */
    public static String ssnToDateOfBirth(String ssn) {
        String dateOfBirth = ssn.substring(0, 6);
        String dateOfBirthFormatted = "";
        for (int i = 0; i < dateOfBirth.length(); i++) {
            dateOfBirthFormatted += dateOfBirth.charAt(i);
            if (i == 1 || i == 3) {
                dateOfBirthFormatted += ".";
            }
        }
        return dateOfBirthFormatted;
    }

    /**
     * Formats an unix timestamp to a readable date on the form "HH:mm:ss dd.MM.yyyy"
     * @param timestamp Unix timestamp
     * @return A formatted string
     */
    public static String formattedDate(long timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp*1000);
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
        return dateFormat.format(cal.getTime());


    }
}
