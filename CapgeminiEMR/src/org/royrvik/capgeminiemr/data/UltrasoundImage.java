package org.royrvik.capgeminiemr.data;

/**
 * Representation of an ultrasoundimage and the data related to it.
 */
public class UltrasoundImage {

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
}
