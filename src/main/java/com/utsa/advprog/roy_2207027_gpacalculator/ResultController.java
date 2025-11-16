package com.utsa.advprog.roy_2207027_gpacalculator;

import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;

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
    TableColumn<Course, Double> gradeColumn;
    @FXML
    TableColumn<Course, Double> weightColumn;
    @FXML
    Label overallGPA;

    private ObservableList<Course> courseList = FXCollections.observableArrayList();
    private Course[] courseArray;

    @FXML
    public void initialize() {
        configureColumns();
        //populateSampleCourses();
        courseTable.setItems(courseList);
        overallGPA.setText(Course.calculateGrade(courseArray));

    }

    private void configureColumns() {
        nameColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().name));
        codeColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().code));
        creditColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().credit));
        teacherColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().teacher1Name));
        gradeColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().grade));
        weightColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(Course.calculateWeightedPoint(cellData.getValue())));
    }

    public void setCourseList(ObservableList<Course> courseList) {
        this.courseList.setAll(courseList);
        this.courseArray = courseList.toArray(new Course[0]);
    }

    private void populateSampleCourses() {
        courseList.setAll(
                new Course("Data Structures", "2302", 3.0, "Dr. Patel", "Dr. Singh", "A"),
                new Course("Operating Systems", "3310", 4.0, "Dr. Lin", "Dr. Lopez", "B+"),
                new Course("Software Engineering", "4390", 3.0, "Dr. Kim", "Dr. Reed", "A-")
        );
    }

    @FXML
    public void storeScene(Scene scene){
        this.prevScene = scene;
    }

    public void saveToText(ActionEvent event) throws IOException {
    }

    public void goBack(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainPage.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(prevScene);
        stage.show();
    }
}
