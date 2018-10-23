package com.ceunsp.app.projeto.Model;

import android.graphics.Bitmap;

public class Historic {

    private String userFullName, userType, userID, action, eventType, eventTitle, date;
    private Bitmap imgProfile;

    public Historic() {

    }

    public Historic(String userFullName, String userType, String userID, String action,
                    String eventType, String eventTitle, String date) {

        this.userFullName = userFullName;
        this.userType = userType;
        this.userID = userID;
        this.action = action;
        this.eventType = eventType;
        this.eventTitle = eventTitle;
        this.date = date;
    }

    public Historic(Bitmap imgProfile) {
        this.imgProfile = imgProfile;
    }

    public Bitmap getImgProfile() {
        return imgProfile;
    }

    public void setImgProfile(Bitmap imgProfile) {
        this.imgProfile = imgProfile;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
