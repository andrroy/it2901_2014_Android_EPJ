package org.royrvik.capgeminiemr;

public class UltrasoundRowItem {

    private String imageUri;
    private String date;
    private int id;
    private String description;

    public UltrasoundRowItem(String imageUri, String date, int id, String description) {
        this.imageUri = imageUri;
        this.date = date;
        this.id = id;
        this.description = description;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
