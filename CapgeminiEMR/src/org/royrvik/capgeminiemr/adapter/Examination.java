package org.royrvik.capgeminiemr.adapter;

import java.util.ArrayList;

/**
 * Created by Joakim on 01.03.14.
 */
public class Examination {

    private String pid;
    private String name;
    private ArrayList<UltrasoundRowItem> images;

    public Examination(String pid, String name, ArrayList<UltrasoundRowItem> images) {
        this.pid = pid;
        this.name = name;
        this.images = images;
    }

    public String getPid() {
        return pid;
    }

    public String getName() {
        return name;
    }
    public ArrayList<UltrasoundRowItem> getImages() {
        return images;
    }

    public void updateImages(ArrayList<UltrasoundRowItem> images) {
        this.images = images;
    }

    /**
     *
     * @return The number of images with a non-empty description
     */
    public int hasDesc() {
        int i = 0;
        for (UltrasoundRowItem u : images) {
            if (!u.getDescription().equals("")) {
                i++;
            }
        }
        return i;
    }
}
