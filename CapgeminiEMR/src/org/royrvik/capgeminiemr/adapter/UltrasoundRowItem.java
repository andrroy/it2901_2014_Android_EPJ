package org.royrvik.capgeminiemr.adapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UltrasoundRowItem {

    private String imageUri;
    private String date;
    private int id;
    private String description;

    //SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm:ss");
    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd.MMM yyyy");


    public UltrasoundRowItem(String imageUri, int id, String description) {
        this.date = dateFormat.format(new Date());
        this.imageUri = imageUri;
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
