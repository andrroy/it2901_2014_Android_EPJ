package org.royrvik.capgeminiemr.utils;

/**
 * Created by Laxcor.
 */
interface EMR_RemoteService {
    /** Get patient data linked to the provided ssn **/
    List<String> getPatientData(in String ssn);

    /** Upload the provided data **/
    boolean upload(in List<String> patientData, in List<String> imagePaths);
}
