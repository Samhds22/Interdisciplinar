package com.ceunsp.app.projeto.Model;

import java.util.Date;

public class CollegeClass {
    String college, course, className, creator, creationDate, classID;
    Students students;


    public CollegeClass(String college, String course, String className,
                        String creator, String creationDate, Students students, String classID) {
        this.college = college;
        this.course = course;
        this.className = className;
        this.creator = creator;
        this.creationDate = creationDate;
        this.students = students;
        this.classID = classID;
    }

    public CollegeClass(String className, String creator, String creationDate, String classID) {
        this.className = className;
        this.creator = creator;
        this.creationDate = creationDate;
        this.classID = classID;
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

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Object getStudents() {
        return students;
    }

    public void setStudents(Students students) {
        this.students = students;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getClassID() {
        return classID;
    }

    public void setClassID(String classID) {
        this.classID = classID;
    }
}
