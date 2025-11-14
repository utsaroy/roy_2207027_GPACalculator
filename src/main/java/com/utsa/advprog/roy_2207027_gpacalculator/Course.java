package com.utsa.advprog.roy_2207027_gpacalculator;

public class Course {
    protected String name;
    protected int code;
    protected int credit;
    protected String teacher1Name;
    protected String teacher2Name;
    protected String grade;
    Course(String name, int code, int credit, String teacher1Name, String teacher2Name, String grade) {
        this.name = name;
        this.code = code;
        this.credit = credit;
        this.teacher1Name = teacher1Name;
        this.teacher2Name = teacher2Name;
        this.grade = grade;
    }
}
