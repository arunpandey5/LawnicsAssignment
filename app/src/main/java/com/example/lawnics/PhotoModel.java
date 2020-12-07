package com.example.lawnics;

public class PhotoModel {
    String imageUrl;
    String imageName;
    String date;
    String time;
    String page;
    String imageType;

    public PhotoModel() {
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public PhotoModel(String imageUrl, String imageName, String date, String time, String page, String imageType) {
        this.imageUrl = imageUrl;
        this.imageName = imageName;
        this.date = date;
        this.time = time;
        this.page = page;
        this.imageType = imageType;
    }
}
