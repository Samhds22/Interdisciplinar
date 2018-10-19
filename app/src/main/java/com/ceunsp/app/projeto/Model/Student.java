package com.ceunsp.app.projeto.Model;

public class Student {

    String college, course, classID;

    public Student(String college, String course, String classID) {
        this.college = college;
        this.course = course;
        this.classID = classID;
    }

    public Student(String college, String course) {
        this.college = college;
        this.course  = course;
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
        return classID;
    }

    public void setCollegeClassID(String collegeClassID) {
        this.classID = collegeClassID;
    }
}