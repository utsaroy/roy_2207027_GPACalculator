package com.utsa.advprog.roy_2207027_gpacalculator;

public class SavedResult {
    private final int id;
    private String name;
    private String roll;
    private String university;
    private String mobile;
    private String email;
    private double gpa;
    private double totalCredits;
    private double earnedCredits;
    private String coursesData;

    public SavedResult(int id,
                       String name,
                       String roll,
                       String university,
                       String mobile,
                       String email,
                       double gpa,
                       double totalCredits,
                       double earnedCredits,
                       String coursesData) {
        this.id = id;
        this.name = name;
        this.roll = roll;
        this.university = university;
        this.mobile = mobile;
        this.email = email;
        this.gpa = gpa;
        this.totalCredits = totalCredits;
        this.earnedCredits = earnedCredits;
        this.coursesData = coursesData;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRoll() {
        return roll;
    }

    public String getUniversity() {
        return university;
    }

    public String getMobile() {
        return mobile;
    }

    public String getEmail() {
        return email;
    }

    public double getGpa() {
        return gpa;
    }

    public double getTotalCredits() {
        return totalCredits;
    }

    public double getEarnedCredits() {
        return earnedCredits;
    }

    public String getCoursesData() {
        return coursesData;
    }

    public String getDisplayDate() {
        return String.format("#%04d", id);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }

    public int getCourseCount() {
        if (coursesData == null || coursesData.isEmpty()) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i < coursesData.length(); i++) {
            if (coursesData.charAt(i) == '{') {
                count++;
            }
        }
        return count;
    }
}
