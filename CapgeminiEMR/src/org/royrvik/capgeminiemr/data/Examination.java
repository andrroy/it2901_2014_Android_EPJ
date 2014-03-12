package org.royrvik.capgeminiemr.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Examination {

    private int patientSsn;
    private String patientName;
    private ArrayList<UltrasoundImage> ultrasoundImages;
    private String date;

    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd.MMM yyyy");

    public Examination(int patientSsn, String patientName, ArrayList<UltrasoundImage> ultrasoundImages) {
        this.date = dateFormat.format(new Date());
        this.patientSsn = patientSsn;
        this.patientName = patientName;
        this.ultrasoundImages = ultrasoundImages;
    }

    public Examination() {
        patientName = " ";
        patientSsn = 0;
        ultrasoundImages = new ArrayList<UltrasoundImage>();
        this.date = dateFormat.format(new Date());
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

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public ArrayList<UltrasoundImage> getUltrasoundImages() {
        return ultrasoundImages;
    }

    public void setUltrasoundImages(ArrayList<UltrasoundImage> ultrasoundImages) {
        this.ultrasoundImages = ultrasoundImages;
    }

    public void addUltrasoundImage(UltrasoundImage usImage) {
        this.ultrasoundImages.add(usImage);
    }

    public void deleteImage(int index) {
        this.ultrasoundImages.remove(index);
    }

    public ArrayList<String> getAllComments() {
        ArrayList<String> allComments = new ArrayList<String>();

        for(UltrasoundImage us : ultrasoundImages) {
            allComments.add(us.getComment());
        }
        return allComments;
    }

    public ArrayList<String> getAllImages() {
        ArrayList<String> allImages = new ArrayList<String>();

        for(UltrasoundImage us : ultrasoundImages) {
            allImages.add(us.getImageUri());
        }
        return allImages;
    }
}