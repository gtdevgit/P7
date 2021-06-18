package com.example.go4lunch.models;

import androidx.annotation.ColorRes;

import java.util.List;

public class DetailRestaurant {
    private String placeId;
    private String name;
    private String info;
    private String phoneNumber;
    private String website;
    private String urlPicture;
    private double rating;
    private boolean haveStar1;
    private boolean haveStar2;
    private boolean haveStar3;
    private boolean isLiked;
    private boolean isOpen;
    private List<String> workmates;
    private List<String> urlPhotos;
    private int countLike;
    private int star1Color;
    private int star2Color;
    private int star3Color;

    public DetailRestaurant(String placeId, String name, String info, String phoneNumber,
                            String website, String urlPicture, double rating, boolean haveStar1,
                            boolean haveStar2, boolean haveStar3, boolean isLiked, boolean isOpen,
                            List<String> workmates, List<String> urlPhotos, int countLike,
                            int star1Color, int star2Color, int star3Color) {
        this.placeId = placeId;
        this.name = name;
        this.info = info;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.urlPicture = urlPicture;
        this.rating = rating;
        this.haveStar1 = haveStar1;
        this.haveStar2 = haveStar2;
        this.haveStar3 = haveStar3;
        this.isLiked = isLiked;
        this.isOpen = isOpen;
        this.workmates = workmates;
        this.urlPhotos = urlPhotos;
        this.countLike = countLike;
        this.star1Color = star1Color;
        this.star2Color = star2Color;
        this.star3Color = star3Color;

    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getUrlPicture() {
        return urlPicture;
    }

    public void setUrlPicture(String urlPicture) {
        this.urlPicture = urlPicture;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public boolean isHaveStar1() {
        return haveStar1;
    }

    public void setHaveStar1(boolean haveStar1) {
        this.haveStar1 = haveStar1;
    }

    public boolean isHaveStar2() {
        return haveStar2;
    }

    public void setHaveStar2(boolean haveStar2) {
        this.haveStar2 = haveStar2;
    }

    public boolean isHaveStar3() {
        return haveStar3;
    }

    public void setHaveStar3(boolean haveStar3) {
        this.haveStar3 = haveStar3;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public List<String> getWorkmates() {
        return workmates;
    }

    public void setWorkmates(List<String> workmates) {
        this.workmates = workmates;
    }

    public List<String> getUrlPhotos() {
        return urlPhotos;
    }

    public void setUrlPhotos(List<String> urlPhotos) {
        this.urlPhotos = urlPhotos;
    }

    public int getCountLike() {
        return countLike;
    }

    public void setCountLike(int countLike) {
        this.countLike = countLike;
    }

    public int getStar1Color() {
        return star1Color;
    }

    public void setStar1Color(int star1Color) {
        this.star1Color = star1Color;
    }

    public int getStar2Color() {
        return star2Color;
    }

    public void setStar2Color(int star2Color) {
        this.star2Color = star2Color;
    }

    public int getStar3Color() {
        return star3Color;
    }

    public void setStar3Color(int star3Color) {
        this.star3Color = star3Color;
    }
}
