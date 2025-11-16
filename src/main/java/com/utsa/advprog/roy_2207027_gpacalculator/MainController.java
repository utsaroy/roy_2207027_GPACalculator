package com.utsa.advprog.roy_2207027_gpacalculator;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {
    @FXML
    TableView<Course>  courseTable;
    @FXML
    TableColumn<Course,String> nameColumn;
    @FXML
    TableColumn<Course,String> codeColumn;
    @FXML
    TableColumn<Course,Double> creditColumn;
    @FXML
    TableColumn<Course,String> teachersColumn;
    @FXML
    TableColumn<Course,Double> gradeColumn;
    @FXML
    TextField totalCreditField;
    @FXML
    Label totalCreditLabel;
    @FXML
    Label takenCreditLabel;
    @FXML
    TextField courseNameField;
    @FXML
    TextField courseCodeField;
    @FXML
    TextField courseCreditField;
    @FXML
    TextField teacherOneField;
    @FXML
    TextField teacherTwoField;
    @FXML
    ComboBox<String> gradeComboBox;
    double totalCredits = 0;
    double creditSum = 0;

    private final ObservableList<Course> courses = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        gradeComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Select Grade");
                } else {
                    setText(item);
                }
            }
        });

        totalCreditLabel.setText("Total Credits: Not Set");
        takenCreditLabel.setText(creditSum+" / "+totalCredits);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        creditColumn.setCellValueFactory(new PropertyValueFactory<>("credit"));
        teachersColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().teacher1Name + " " + cell.getValue().teacher2Name));
        gradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));
        courses.add(new Course("Data Structure and Algorithm", "CSE 1234", 12.0, "hello", "hello2", "A+"));
        courseTable.setItems(courses);
    }

    public void calculateGPA(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ResultPage.fxml"));
        Parent root = fxmlLoader.load();
        Scene prevScene = ((Node)event.getSource()).getScene();
        ResultController resultController = fxmlLoader.getController();
        resultController.storeScene(prevScene);
        resultController.setCourseList(courses);
        Stage stage = (Stage)prevScene.getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void addCourse(ActionEvent event) {
        String courseName = courseNameField.getText();
        String courseCode = courseCodeField.getText();
        String teacherOne = teacherOneField.getText();
        String teacherTwo = teacherTwoField.getText();
        String grade = gradeComboBox.getSelectionModel().getSelectedItem();
        String credit = courseCreditField.getText();
//        System.out.println("credit: " + credit);
        if(courseName.isEmpty() || courseCode.isEmpty() || teacherOne.isEmpty() || credit.isEmpty() || grade==null || grade.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please fill all the fields");
            alert.showAndWait();
            return;
        }
        double courseCredit;
        try{
            courseCredit = Double.parseDouble(credit);
            if(totalCredits==0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Please set total credits first");
                alert.showAndWait();
                return;
            }
            if(totalCredits < creditSum+courseCredit){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Credit Cannot Exceed total credits");
                alert.showAndWait();
                return;
            }
            creditSum += courseCredit;
            takenCreditLabel.setText(creditSum+" / "+totalCredits);
            courses.add(new Course(courseName, courseCode, courseCredit, teacherOne, teacherTwo, grade));
        } catch (Exception _){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Invalid Credit Input");
        }
    }

    public void clearForm() {
        courseNameField.setText("");
        courseCodeField.setText("");
        courseCreditField.setText("");
        teacherOneField.setText("");
        teacherTwoField.setText("");
        gradeComboBox.getSelectionModel().clearSelection();
        gradeComboBox.setPromptText("Select Grade");
        gradeComboBox.setValue(null);
    }



    public void setCredit(ActionEvent event) {
        try{
            double cc = Double.parseDouble(totalCreditField.getText());
            if(cc<=0) throw new IllegalArgumentException("Illegal credit");
            totalCredits = cc;
            totalCreditField.setText("");
            totalCreditLabel.setText("Total Credits: " + totalCredits);
            takenCreditLabel.setText(creditSum+" / "+totalCredits);
        } catch(Exception _){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please Enter Valid Credit");
            alert.showAndWait();
        }
    }

    public void resetAll(ActionEvent event) {
        clearForm();
        totalCredits = 0;
        creditSum = 0;
        totalCreditLabel.setText("Total Credits: " + totalCredits);
        takenCreditLabel.setText(creditSum+" / "+totalCredits);
        courses.clear();
    }
}
