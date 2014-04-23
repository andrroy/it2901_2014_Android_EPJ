package org.royrvik.capgeminiemr.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Representation of an ultrasoundimage and the data related to it.
 */
public class UltrasoundImage implements Parcelable {

    private String imageUri;
    private String comment;

    public UltrasoundImage(String imageUri, String comment) {
        this.imageUri = imageUri;
        this.comment = comment;
    }

    public UltrasoundImage(String imageUri) {
        this.imageUri = imageUri;
        this.comment = " ";
    }

    public UltrasoundImage() {

    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    // Parcelable methods
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(imageUri);
        parcel.writeString(comment);
    }

    public static final Parcelable.Creator<UltrasoundImage> CREATOR = new Parcelable.Creator<UltrasoundImage>() {
        public UltrasoundImage createFromParcel(Parcel in) {
            return new UltrasoundImage(in);
        }

        public UltrasoundImage[] newArray(int size) {
            return new UltrasoundImage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    private UltrasoundImage(Parcel in) {
        imageUri = in.readString();
        comment = in.readString();
    }
}

