package org.royrvik.capgeminiemr.adapter;

public class UltrasoundRowItem {

    private String imageUri;
    private String date;
    private int id;

    public UltrasoundRowItem(String imageUri, String date, int id) {
        this.imageUri = imageUri;
        this.date = date;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
