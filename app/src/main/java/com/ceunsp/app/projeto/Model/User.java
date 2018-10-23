package com.ceunsp.app.projeto.Model;

import android.graphics.Bitmap;

import java.io.Serializable;

public class User implements Serializable {

    private String name, lastName, nickname, dateOfBirth, userType;
    private Bitmap ImgProfile;

    public User(String name, String lastName, String nickname, String dateOfBirth, String userType){
        this.name = name;
        this.lastName = lastName;
        this.nickname = nickname;
        this.dateOfBirth = dateOfBirth;
        this.userType = userType;
    }

    public User(String name, String lastName, String userType) {
        this.name = name;
        this.lastName = lastName;
        this.userType = userType;
    }

    public Bitmap getImgProfile() {
        return ImgProfile;
    }

    public void setImgProfile(Bitmap imgProfile) {
        ImgProfile = imgProfile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
