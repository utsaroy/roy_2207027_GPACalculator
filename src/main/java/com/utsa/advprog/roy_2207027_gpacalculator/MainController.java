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
import java.security.InvalidParameterException;

public class MainController {
    public Label coursesForId;
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
    Button submitButton;
    @FXML
    ComboBox<String> gradeComboBox;
    double totalCredits = 0;
    double creditSum = 0;
    Student student;
    Scene prevScene;
    private boolean updateMode;

    private final ObservableList<Course> courses = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        //this was added to show promptText after clearing the form
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

        //disable the submit button
        submitButton.setDisable(true);
        applySubmitButtonLabel();

        totalCreditLabel.setText("Total Credits: Not Set");
        takenCreditLabel.setText(creditSum+" / "+totalCredits);

        //configure table columns with Course class
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        creditColumn.setCellValueFactory(new PropertyValueFactory<>("credit"));
        teachersColumn.setCellValueFactory(cell -> new SimpleStringProperty( cell.getValue().teacher1Name + ((cell.getValue().teacher2Name.isEmpty() ? "": " & " + cell.getValue().teacher2Name))));
        gradeColumn.setCellValueFactory(new PropertyValueFactory<>("gradeLetter"));
        //courses.add(new Course("Data Structure and Algorithm", "CSE 1234", 12.0, "hello", "hello2", "A+"));
        courseTable.setItems(courses);

    }

    public void setStudent(Student student) {
        this.student = student;
        //set Name
        coursesForId.setText("Added Courses for " + student.name);
        if (student.totalCredits > 0) {
            totalCredits = student.totalCredits;
            totalCreditLabel.setText("Total Credits: " + totalCredits);
        }
        updateTakenCreditLabel();
    }
    public void calculateGPA(ActionEvent event) throws IOException {
        if (student == null) {
            showAlert("Missing Student", "Please provide student information first.");
            return;
        }
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ResultPage.fxml"));
        Parent root = fxmlLoader.load();
        Scene prevScene = ((Node)event.getSource()).getScene();
        ResultController resultController = fxmlLoader.getController();
        resultController.storeScene(prevScene);
        student.totalCredits = totalCredits;
        student.earnedCredits = creditSum;
        student.courses = courses.toArray(new Course[0]);
        resultController.setStudent(student);
        resultController.setCourseList(FXCollections.observableArrayList(courses));
        resultController.setUpdateMode(updateMode);
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
            showAlert("Error","Please fill all the fields");
            return;
        }
        double courseCredit;
        try{
            courseCredit = Double.parseDouble(credit);
            if(courseCredit<0) throw new InvalidParameterException("Credit cannot be less than 0");
            if(totalCredits==0) {
                showAlert("Set Credit", "Please enter the total credits first");
                return;

            }
            if(totalCredits < creditSum+courseCredit){
                showAlert("Error","Credit cannot Exceed total credits");
                return;
            }
            creditSum += courseCredit;
            updateTakenCreditLabel();
            courses.add(new Course(courseName, courseCode, courseCredit, teacherOne, teacherTwo, grade));
            clearForm();
        } catch (Exception e){
            showAlert("Invalid Input", "Invalid Credit Input");
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
            updateTakenCreditLabel();
        } catch(Exception _){
            showAlert("Invalid Input", "Please Enter Valid Credit");
        }
    }

    public void resetAll(ActionEvent event) {
        clearForm();
        submitButton.setDisable(true);
        totalCredits = 0;
        creditSum = 0;
        totalCreditLabel.setText("Total Credits: Not Set");
        updateTakenCreditLabel();
        courses.clear();
    }

    public void deleteCourse(ActionEvent event) {
            int i = courseTable.getSelectionModel().getSelectedIndex();
            if(i==-1) {
                showAlert("Error","Please select a course on table");
                return;
            }
            creditSum -= courses.get(i).getCredit();
                updateTakenCreditLabel();
            courses.remove(i);
            courseTable.getSelectionModel().clearSelection();
    }


    public void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public void editCourse(ActionEvent event) {
        int i = courseTable.getSelectionModel().getSelectedIndex();
        if(i==-1) {
            showAlert("Error","Please select a course on table");
            return;
        }
        Course c = courses.get(i);
        courseNameField.setText(c.getName());
        courseCodeField.setText(c.getCode());
        courseCreditField.setText(String.valueOf(c.getCredit()));
        teacherOneField.setText(c.teacher1Name);
        teacherTwoField.setText(c.teacher2Name);
        gradeComboBox.setValue(c.gradeLetter);
        creditSum -= c.getCredit();
        updateTakenCreditLabel();
        courses.remove(i);
        courseNameField.requestFocus();
        courseTable.getSelectionModel().clearSelection();
    }

    public void setPrevScene(Scene prevScene) {
        this.prevScene = prevScene;
    }

    public void backButton(ActionEvent event) {
        Stage stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        stage.setScene(prevScene);
        stage.show();
    }

    public void setUpdateMode(boolean updateMode) {
        this.updateMode = updateMode;
        applySubmitButtonLabel();
    }

    public void loadCourses(Course[] existingCourses) {
        courses.clear();
        creditSum = 0;
        if (!updateMode) {
            totalCredits = 0;
            totalCreditLabel.setText("Total Credits: Not Set");
        }
        if (existingCourses != null) {
            for (Course course : existingCourses) {
                if (course == null) continue;
                courses.add(course);
                creditSum += course.getCredit();
            }
        }

        if (student != null && student.totalCredits > 0) {
            totalCredits = student.totalCredits;
            totalCreditLabel.setText("Total Credits: " + totalCredits);
        } else if (creditSum > 0 && totalCredits == 0) {
            totalCredits = creditSum;
            totalCreditLabel.setText("Total Credits: " + totalCredits);
        }

        updateTakenCreditLabel();
        submitButton.setDisable(totalCredits == 0 || creditSum != totalCredits);
    }

    private void updateTakenCreditLabel() {
        takenCreditLabel.setText(creditSum + " / " + totalCredits);
        submitButton.setDisable(totalCredits == 0 || creditSum != totalCredits);
    }

    private void applySubmitButtonLabel() {
        if (submitButton != null) {
            submitButton.setText(updateMode ? "Update GPA" : "Calculate GPA");
        }
    }
}
