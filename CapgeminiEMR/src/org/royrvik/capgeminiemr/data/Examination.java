package org.royrvik.capgeminiemr.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Examination {

    private int patientSsn;
    private String patientName;
    private ArrayList<String> comments;
    private ArrayList<String> imageUris;
    private String date;

    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd.MMM yyyy");

    public Examination(int patientSsn, String patientName, ArrayList<String> comments, ArrayList<String> imageUris) {
        this.date = dateFormat.format(new Date());
        this.patientSsn = patientSsn;
        this.patientName = patientName;
        this.comments = comments;
        this.imageUris = imageUris;
    }

    public int getPatientSsn() {
        return patientSsn;
    }

    public void setPatientSsn(int patientSsn) {
        this.patientSsn = patientSsn;
    }

    public String getDate() {
        return date;
    }

    public ArrayList<String> getImageUris() {
        return imageUris;
    }

    public void setImageUris(ArrayList<String> imageUris) {
        this.imageUris = imageUris;
    }

    public ArrayList<String> getComments() {
        return comments;
    }

    public void setComments(ArrayList<String> comments) {
        this.comments = comments;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

}