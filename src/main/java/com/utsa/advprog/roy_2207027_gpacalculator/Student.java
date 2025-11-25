package com.utsa.advprog.roy_2207027_gpacalculator;

public class Student {
    String name, university, mobile, email, roll;
    double gpa, totalCredits, earnedCredits;
    Course[] courses;
    public Student(String name, String roll, String mobile, String email, String university) {
        this.name = name;
        this.roll = roll;
        this.mobile = mobile;
        this.email = email;
        this.university = university;
    }
}
