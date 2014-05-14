package org.royrvik.emrservice;

/**
 * Created by Joakim.
 */
interface EMRRemoteInterface {
    /** Get patient data linked to the provided ssn **/
    List<String> getPatientData(in String ssn, in String username, in String password);

    /** Upload the provided data **/
    List<String> upload(in List<String> examinationData, in List<String> imagePaths, in List<String> notes, in String username, in String password);
}
