package org.royrvik.capgeminiemr.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Representation of an Examination and the
 */
public class Examination {

    private String patientName, patientSsn;
    private ArrayList<UltrasoundImage> ultrasoundImages;
    private String date;

    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd.MMM yyyy");

    public Examination(String patientSsn, String patientName, ArrayList<UltrasoundImage> ultrasoundImages) {
        this.date = dateFormat.format(new Date());
        this.patientSsn = patientSsn;
        this.patientName = patientName;
        this.ultrasoundImages = ultrasoundImages;
    }

    public Examination(String patientSsn, String patientName, ArrayList<UltrasoundImage> ultrasoundImages, String date) {
        this.date = date;
        this.patientSsn = patientSsn;
        this.patientName = patientName;
        this.ultrasoundImages = ultrasoundImages;
    }

    public Examination() {
        ultrasoundImages = new ArrayList<UltrasoundImage>();
        this.date = dateFormat.format(new Date());
    }

    public String getPatientSsn() {
        return patientSsn;
    }

    public void setPatientSsn(String patientSsn) {
        this.patientSsn = patientSsn;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    @Override
    public String toString() {
        return "Examination{" +
                "patientName='" + patientName + '\'' +
                ", patientSsn='" + patientSsn + '\'' +
                ", ultrasoundImages=" + ultrasoundImages +
                ", date='" + date + '\'' +
                '}';
    }
}