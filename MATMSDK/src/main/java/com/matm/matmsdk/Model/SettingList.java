package com.matm.matmsdk.Model;

public class SettingList {
    public int getImageId() {
        return imageId;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "SettingList{" +
                "imageId=" + imageId +
                ", title='" + title + '\'' +
                '}';
    }

    public SettingList(int imageId, String title) {
        this.imageId = imageId;
        this.title = title;
    }

    private int imageId;

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String title;

}
