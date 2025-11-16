package com.utsa.advprog.roy_2207027_gpacalculator;

public class Course {
    protected String name;
    protected String code;
    protected double credit;

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public double getCredit() {
        return credit;
    }

    public String getTeacher1Name() {
        return teacher1Name;
    }

    public double getGrade() {
        return grade;
    }

    public String getGradeLetter() {
        return gradeLetter;
    }

    public String getTeacher2Name() {
        return teacher2Name;
    }

    protected String teacher1Name;
    protected String teacher2Name;
    protected double grade;
    protected String gradeLetter;
    Course(String name, String code, double credit, String teacher1Name, String teacher2Name, String gradeLetter) {
        this.name = name;
        this.code = code;
        this.credit = credit;
        this.teacher1Name = teacher1Name;
        this.teacher2Name = teacher2Name;
        this.gradeLetter = gradeLetter;
        this.grade = convertGradeToPoint(gradeLetter);
    }

    public static String calculateGrade(Course[] courses) {
        if (courses == null || courses.length == 0) return "0.0";

        double totalCredits = 0.0;
        double totalWeightedPoints = 0.0;

        for (Course course : courses) {
            if (course == null) continue;
            double credit = sanitizeCredit(course.credit);
            if (credit == 0.0) continue;
            totalCredits += credit;
            totalWeightedPoints += calculateWeightedPoint(course);
        }

        if (totalCredits == 0.0) return "0.0";
        double d = totalWeightedPoints/totalCredits;
        return String.valueOf(d);
    }

    public static int totalCourses(Course[] courses) {
        return courses.length;
    }

    public static double earnedCredits(Course[] courses) {
        double sum = 0.0;
        for (Course course : courses) {
            if (course == null) continue;
            sum += course.credit;
        }
        return sum;
    }

    public static double totalWeightedPoints(Course[] courses) {
        if (courses == null || courses.length == 0) return 0.0;

        double sum = 0.0;
        for (Course course : courses) {
            sum += calculateWeightedPoint(course);
        }
        return sum;
    }

    public static double calculateWeightedPoint(Course course) {
        if (course == null) return 0.0;

        double credit = sanitizeCredit(course.credit);
        if (credit == 0.0) return 0.0;

        double gradePoint = sanitizeGrade(course.grade);
        return gradePoint * credit;
    }

    public static double convertGradeToPoint(String letterGrade) {
        if (letterGrade == null) return 0.0;
        String normalized = letterGrade.trim().toUpperCase();
        if (normalized.isEmpty()) return 0.0;

        return switch (normalized) {
            case "A+" -> 4.0;
            case "A" -> 3.75;
            case "A-" -> 3.5;
            case "B+" -> 3.25;
            case "B" -> 3.0;
            case "B-" -> 2.75;
            case "C+" -> 2.5;
            case "C" -> 2.25;
            case "D" -> 2.0;
            default -> 0.0;
        };
    }

    private static double sanitizeGrade(double grade) {
        return Math.max(0.0, Math.min(grade, 4.0));
    }

    private static double sanitizeCredit(double credit) {
        return Math.max(0.0, credit);
    }
}
