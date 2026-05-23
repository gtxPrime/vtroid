package com.gtxprime.vtroid.model;

public class movieModel {

    public String imgLink;
    public String link;

    public movieModel(String imgLink, String link) {
        this.imgLink = imgLink;
        this.link = link;
    }

    public String getImgLink() {
        return imgLink;
    }

    public void setImgLink(String imgLink) {
        this.imgLink = imgLink;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public movieModel(){}


}
