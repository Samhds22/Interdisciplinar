package com.ceunsp.app.projeto.Model;

import java.io.Serializable;

public class User implements Serializable {

    private String name, lastName, nickname, dateOfBirth, college, course, collegeClassID, userType;

    public User(String name, String lastName, String nickname, String dateOfBirth
               , String college, String course, String collegeClassID, String userType){
        this.name = name;
        this.lastName = lastName;
        this.nickname = nickname;
        this.dateOfBirth = dateOfBirth;
        this.college = college;
        this.course = course;
        this.collegeClassID = collegeClassID;
        this.userType = userType;
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

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getCollegeClassID() {
        return collegeClassID;
    }

    public void setCollegeClassID(String collegeClassID) {
        this.collegeClassID = collegeClassID;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
