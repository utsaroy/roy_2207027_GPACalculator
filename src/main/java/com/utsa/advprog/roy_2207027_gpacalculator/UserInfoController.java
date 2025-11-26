package com.utsa.advprog.roy_2207027_gpacalculator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class UserInfoController {
    public TextField nameField;
    public TextField rollField;
    public TextField mobileField;
    public TextField emailField;
    public TextField universityField;
    @FXML
    private Button continueButtonControl;

    private boolean updateMode;
    private Student existingStudent;
    private Course[] existingCourses = new Course[0];
    private Scene prevScene;


    public void clearForm(ActionEvent event) {
        nameField.clear();
        rollField.clear();
        mobileField.clear();
        emailField.clear();
        universityField.clear();
    }

    public void continueButton(ActionEvent event) throws IOException {
        String name = nameField.getText();
        String roll = rollField.getText();
        String mobile = mobileField.getText();
        String email = emailField.getText();
        String university = universityField.getText();
        if(name.isEmpty() || roll.isEmpty() || email.isEmpty() || university.isEmpty()) {
            showAlert("Please fill all the fields");
            return;
        }
        Student student;
        if (updateMode && existingStudent != null) {
            existingStudent.name = name;
            existingStudent.roll = roll;
            existingStudent.mobile = mobile;
            existingStudent.email = email;
            existingStudent.university = university;
            student = existingStudent;
        } else {
            student = new Student(name, roll, mobile, email, university);
            student.courses = new Course[0];
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainPage.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        MainController mainController = loader.getController();
        mainController.setStudent(student);
        if (updateMode && existingStudent != null && existingStudent.courses != null) {
            existingCourses = existingStudent.courses;
        }

        mainController.setPrevScene(stage.getScene());
        mainController.setUpdateMode(updateMode);
        mainController.loadCourses(updateMode ? existingCourses : new Course[0]);
        stage.setScene(new Scene(root));
        stage.show();
    }


    public void backButton(ActionEvent event) throws IOException {
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        if (updateMode && prevScene != null) {
            stage.setScene(prevScene);
            stage.show();
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("Welcome.fxml"));
        stage.setScene(new Scene(loader.load()));
        stage.show();
    }

    public void showAlert(String s){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(null);
        alert.setHeaderText(null);
        alert.setContentText(s);
        alert.showAndWait();
    }

    public void loadExistingData(Student student, Course[] courses, boolean update) {
        this.updateMode = update;
        this.existingStudent = student;
        this.existingCourses = courses != null ? courses : new Course[0];
        this.existingStudent.courses = this.existingCourses;

        nameField.setText(student.name);
        rollField.setText(student.roll);
        mobileField.setText(student.mobile);
        emailField.setText(student.email);
        universityField.setText(student.university);

        applyContinueButtonLabel();
    }

    public void storeScene(Scene scene) {
        this.prevScene = scene;
    }

    private void applyContinueButtonLabel() {
        if (continueButtonControl != null) {
            continueButtonControl.setText(updateMode ? "Update" : "Continue");
        }
    }
}
