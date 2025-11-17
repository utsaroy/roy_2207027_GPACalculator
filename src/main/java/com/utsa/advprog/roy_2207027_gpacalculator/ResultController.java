package com.utsa.advprog.roy_2207027_gpacalculator;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class ResultController {
    Scene prevScene;

    @FXML
    TableView<Course> courseTable;
    @FXML
    TableColumn<Course, String> nameColumn;
    @FXML
    TableColumn<Course, String> codeColumn;
    @FXML
    TableColumn<Course, Double> creditColumn;
    @FXML
    TableColumn<Course, String> teacherColumn;
    @FXML
    TableColumn<Course, String> gradeColumn;
    @FXML
    TableColumn<Course, Double> weightColumn;
    @FXML
    Label overallGPA;
    @FXML
    Label courseEnrolledText;
    @FXML
    Label weightedPointText;
    @FXML
    Label earnedCreditText;

    private ObservableList<Course> courseList = FXCollections.observableArrayList();
    private Course[] courseArray;

    @FXML
    public void initialize() {
        configureColumns();
        courseTable.setItems(courseList);
    }

    private void configureColumns() {
        nameColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().name));
        codeColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().code));
        creditColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().credit));
        teacherColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().teacher1Name + (cellData.getValue().teacher2Name.isEmpty() ? "" :" & " + cellData.getValue().teacher2Name)));
        gradeColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().gradeLetter));
        weightColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(Course.calculateWeightedPoint(cellData.getValue())));
    }

    public void setCourseList(ObservableList<Course> courseList) {
        this.courseList.setAll(courseList);
        this.courseArray = courseList.toArray(new Course[0]);
        overallGPA.setText(Course.calculateGrade(courseArray));
        courseEnrolledText.setText(": " + String.valueOf(Course.totalCourses(courseArray)));
        weightedPointText.setText(": "+String.valueOf(Course.totalWeightedPoints(courseArray)));
        earnedCreditText.setText(": "+String.valueOf(Course.earnedCredits(courseArray)));
    }

    @FXML
    public void storeScene(Scene scene){
        this.prevScene = scene;
    }

    public void saveToText(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save to Text");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files (*.csv)", "*.csv"));
        fileChooser.setInitialFileName("GPA Result.csv");

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);
        if (file == null) return;

        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            writer.write("Course Name,Course Code,Credit,Teacher 1,Teacher 2,Grade,Grade Point,Weighted Point");
            writer.newLine();
            for (Course course : courseList) {
                if (course == null) continue;
                double weightedPoint = Course.calculateWeightedPoint(course);
                writer.write(String.format(
                        "%s,%s,%.2f,%s,%s,%s,%.2f,%.2f",
                        course.getName(),
                        course.getCode(),
                        course.getCredit(),
                        course.getTeacher1Name(),
                        course.getTeacher2Name(),
                        course.getGradeLetter(),
                        course.getGrade(),
                        weightedPoint
                ));
                writer.newLine();
            }
            writer.write(String.format("Course Enrolled, %s", Course.totalCourses(courseArray)));
            writer.newLine();
            writer.write(String.format("Course Earned, %s", Course.totalWeightedPoints(courseArray)));
            writer.newLine();
            writer.write(String.format("GPA, %s", Course.calculateGrade(courseArray)));
        }
    }

    public void goBack(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainPage.fxml"));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(prevScene);
        stage.show();
    }
}
