package org.royrvik.capgeminiemr.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Representation of an Examination
 */

public class Examination implements Parcelable {

    private String patientFirstName, patientLastName, patientSsn, examinationComment;
    private ArrayList<UltrasoundImage> ultrasoundImages;
    private long examinationTime;
    private int databaseId, examinationId;

//    public Examination(String patientSsn, String patientFirstName, String patientLastName, ArrayList<UltrasoundImage> ultrasoundImages) {
//        this.patientSsn = patientSsn;
//        this.dateOfBirth = patientSsn.substring(0,5);
//        this.patientFirstName = patientFirstName;
//        this.patientLastName = patientLastName;
//        this.ultrasoundImages = ultrasoundImages;
//        this.examinationTime = new Date().getTime(); //TODO: Create function that gets examinationTime based on image metadata
//        this.databaseId = -1; // Should have this value until its set
//    }

    public Examination(int examinationId, String patientFirstName, String patientLastName, String patientSsn,
                        long examinationTime, String examinationComment, ArrayList<UltrasoundImage> ultrasoundImages) {
        this.examinationTime = examinationTime; //TODO: Create function that gets examinationTime based on image metadata
        this.patientSsn = patientSsn;
        this.patientFirstName = patientFirstName;
        this.patientLastName = patientLastName;
        this.ultrasoundImages = ultrasoundImages;
        this.examinationComment = examinationComment;
        this.examinationId = examinationId;
        this.databaseId = -1;
    }

    public Examination() {
        ultrasoundImages = new ArrayList<UltrasoundImage>();
        this.databaseId = -1;

        //ANDREAS TESTER
//        this.examinationTime = 123455432; //TODO: Create function that gets examinationTime based on image metadata
//        this.patientSsn = "lol";
//        this.patientFirstName = "lol";
//        this.patientLastName = "lol";
//        this.examinationComment = "lol";
//        this.examinationId = 12;
    }

    public String getPatientSsn() {
        return patientSsn;
    }

    public void setPatientSsn(String patientSsn) {
        this.patientSsn = patientSsn;
    }

    public long getExaminationTime() {
        return examinationTime;
    }

    public void setExaminationTime(long examinationTime) {
        this.examinationTime= examinationTime;
    }

    public String getExaminationComment() { return examinationComment; }

    public void setExaminationComment(String note) { this.examinationComment = note; }

    public String getPatientFirstName() {return patientFirstName;}

    public void setPatientFirstName(String patientFirstName) {
        this.patientFirstName= patientFirstName;
    }

    public String getPatientLastName() {return patientLastName;}

    public void setPatientLastName(String patientLastName) { this.patientLastName = patientLastName; }

    public ArrayList<UltrasoundImage> getUltrasoundImages() {
        return ultrasoundImages;
    }

    public void setUltrasoundImages(ArrayList<UltrasoundImage> ultrasoundImages) {
        examinationTime = 1398973142; //TODO: Get date from metadata
        this.ultrasoundImages = ultrasoundImages;
    }

    public void setDatabaseId(Integer databaseId) {
        this.databaseId = databaseId;
    }

    public Integer getDatabaseId() {
        return databaseId;
    }

    public void setExaminationId(Integer examinationId) { this.examinationId = examinationId; }

    public Integer getExaminationId() { return examinationId; }

    public void addUltrasoundImage(UltrasoundImage usImage) {
        this.ultrasoundImages.add(usImage);
    }

    public void deleteImage(int index) {
        this.ultrasoundImages.remove(index);
    }

    public ArrayList<String> getAllComments() {
        ArrayList<String> allComments = new ArrayList<String>();

        for (UltrasoundImage us : ultrasoundImages) {
            allComments.add(us.getComment());
        }
        return allComments;
    }

    public ArrayList<String> getAllImages() {
        ArrayList<String> allImages = new ArrayList<String>();

        for (UltrasoundImage us : ultrasoundImages) {
            allImages.add(us.getImageUri());
        }
        return allImages;
    }


    // Parcelable methods
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(patientFirstName);
        parcel.writeString(patientLastName);
        parcel.writeString(patientSsn);
        parcel.writeList(ultrasoundImages);
        parcel.writeLong(examinationTime);
        parcel.writeInt(examinationId);
        parcel.writeString(examinationComment);
        parcel.writeInt(databaseId);
    }

    public static final Parcelable.Creator<Examination> CREATOR = new Parcelable.Creator<Examination>() {
        public Examination createFromParcel(Parcel in) {
            return new Examination(in);
        }

        public Examination[] newArray(int size) {
            return new Examination[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    private Examination(Parcel in) {
        patientFirstName = in.readString();
        patientLastName = in.readString();
        patientSsn = in.readString();
        ultrasoundImages = new ArrayList<UltrasoundImage>();
        in.readList(ultrasoundImages, ((Object) this).getClass().getClassLoader());
        examinationTime = in.readLong();
        examinationId = in.readInt();
        examinationComment = in.readString();
        databaseId = in.readInt();
    }
}