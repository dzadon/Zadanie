package com.example.tomdado.zadanie;

public class SingleItemModel {
    private String author, url, dateTimeOfPost, numberOfPosts, dateTimeOfRegistration;
    private boolean profileView,image;

    public SingleItemModel() {
    }

    public boolean isImage() {
        return image;
    }

    public void setImage(boolean image) {
        this.image = image;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDateTimeOfPost() {
        return dateTimeOfPost;
    }

    public void setDateTimeOfPost(String dateTimeOfPost) {
        this.dateTimeOfPost = dateTimeOfPost;
    }

    public String getNumberOfPosts() {
        return numberOfPosts;
    }

    public void setNumberOfPosts(String numberOfPosts) {
        this.numberOfPosts = numberOfPosts;
    }

    public String getDateTimeOfRegistration() {
        return dateTimeOfRegistration;
    }

    public void setDateTimeOfRegistration(String dateTimeOfRegistration) {
        this.dateTimeOfRegistration = dateTimeOfRegistration;
    }

    public boolean isProfileView() {
        return profileView;
    }

    public void setProfileView(boolean profileView) {
        this.profileView = profileView;
    }
}
