package com.ceunsp.app.projeto.Model;

import java.io.Serializable;

public class User implements Serializable {

    private int half;
    private String name, lastName, nickname, dateOfBirth, college, course, collegeClassID;

    public User(String name, String lastName, String nickname, String dateOfBirth
               ,String college, String course, String collegeClassID) {
        this.name = name;
        this.lastName = lastName;
        this.nickname = nickname;
        this.dateOfBirth = dateOfBirth;
        this.college = college;
        this.course = course;
        this.collegeClassID = collegeClassID;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public int getHalf() {
        return half;
    }

    public void setHalf(int half) {
        this.half = half;
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

    public void setCollegeClassID(String collegeClass) {
        this.collegeClassID = collegeClassID;
    }
}
