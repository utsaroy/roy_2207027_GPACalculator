package com.utsa.advprog.roy_2207027_gpacalculator;

import javafx.concurrent.Task;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class WriteData extends Task<Void> {
    Student student;
    Course[] courses;

    public WriteData(Student student, Course[] courses) {
        this.student = student;
        this.courses = courses;
    }

    private String courseArrayToString(Course[] courses) {
        if (courses == null || courses.length == 0) {
            return "[]";
        }

        StringBuilder json = new StringBuilder("[");
        boolean first = true;

        for (Course course : courses) {
            if (course == null) {
                continue;
            }

            if (!first) {
                json.append(",");
            } else {
                first = false;
            }

            json.append("{")
                    .append("\"name\":\"").append(escapeJson(course.name)).append("\",")
                    .append("\"code\":\"").append(escapeJson(course.code)).append("\",")
                    .append("\"credit\":").append(course.credit).append(",")
                    .append("\"teacher1Name\":\"").append(escapeJson(course.teacher1Name)).append("\",")
                    .append("\"teacher2Name\":\"").append(escapeJson(course.teacher2Name)).append("\",")
                    .append("\"grade\":").append(course.grade).append(",")
                    .append("\"gradeLetter\":\"").append(escapeJson(course.gradeLetter)).append("\"")
                    .append("}");
        }

        if (first) {
            return "[]";
        }

        json.append("]");
        return json.toString();
    }
    
    
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r");
    }

    @Override
    protected Void call() throws Exception {
        String coursesData = courseArrayToString(courses);
        boolean updating = student != null && student.getId() > 0;
        String sql = updating
                ? "UPDATE students SET name = ?, roll = ?, university = ?, mobile = ?, email = ?, gpa = ?, totalCredits = ?, earnedCredits = ?, coursesData = ? WHERE id = ?"
                : "INSERT INTO students(name, roll, university, mobile, email, gpa, totalCredits, earnedCredits, coursesData) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?);";
        
        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn == null) {
                throw new SQLException("Failed to establish database connection");
            }
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, student.name);
                ps.setString(2, student.roll);
                ps.setString(3, student.university);
                ps.setString(4, student.mobile);
                ps.setString(5, student.email);
                
                // Convert String to double if needed (assuming you'll fix Student class)
                ps.setDouble(6, student.gpa);
                ps.setDouble(7, student.totalCredits);
                ps.setDouble(8, student.earnedCredits);
                ps.setString(9, coursesData);

                if (updating) {
                    ps.setInt(10, student.getId());
                }

                ps.executeUpdate();
                System.out.println(updating ? "Student data updated successfully!" : "Student data saved successfully!");
            }
        } catch (SQLException e) {
            System.err.println("Failed to save student data: " + e.getMessage());
//            throw e;
        }

        return null;
    }
}