package com.utsa.advprog.roy_2207027_gpacalculator;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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
    public Label universityField;
    public Label rollField;
    public Label nameField;
    Scene prevScene;
    Student student;

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
    @FXML
    private Button saveButton;

    private final ObservableList<Course> courseList = FXCollections.observableArrayList();
    private Course[] courseArray;
    private boolean updateMode;

    @FXML
    public void initialize() {
        configureColumns();
        courseTable.setItems(courseList);
        applySaveButtonLabel();
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
        courseEnrolledText.setText(": " + Course.totalCourses(courseArray));
        weightedPointText.setText(": "+ Course.totalWeightedPoints(courseArray));
        earnedCreditText.setText(": "+ Course.earnedCredits(courseArray));
        if (student != null) {
            student.courses = this.courseArray.clone();
        }
    }

    public void setStudent(Student student) {
        this.student = student;
        universityField.setText(student.university);
        rollField.setText(student.roll);
        nameField.setText(student.name);
        if (courseArray != null) {
            student.courses = courseArray.clone();
        }
    }

    public void setUpdateMode(boolean updateMode) {
        this.updateMode = updateMode;
        applySaveButtonLabel();
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
        //FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainPage.fxml"));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(prevScene);
        stage.show();
    }

    public void saveToDatabase(ActionEvent event) {
        if (student == null) {
            showAlert("Missing Student", "Please enter student information before saving.", Alert.AlertType.WARNING);
            return;
        }

        if (courseArray == null || courseArray.length == 0) {
            showAlert("No Courses", "Add at least one course before saving to the database.", Alert.AlertType.WARNING);
            return;
        }

        double earnedCredits = Course.earnedCredits(courseArray);
        double gpaValue;
        try {
            gpaValue = Double.parseDouble(Course.calculateGrade(courseArray));
        } catch (NumberFormatException ex) {
            gpaValue = 0.0;
        }

        student.earnedCredits = earnedCredits;
        student.totalCredits = earnedCredits;
        student.gpa = gpaValue;
        student.courses = courseArray.clone();

        Course[] payload = student.courses.clone();
        WriteData task = new WriteData(student, payload);

        Node trigger = event.getSource() instanceof Node ? (Node) event.getSource() : null;
        if (trigger != null) {
            trigger.setDisable(true);
        }

        task.setOnSucceeded(e -> {
            if (trigger != null) {
                trigger.setDisable(false);
            }
            String message = student.getId() > 0 ? "Student record updated." : "Student data saved to the database.";
            showAlert("Saved", message, Alert.AlertType.INFORMATION);
        });

        task.setOnFailed(e -> {
            if (trigger != null) {
                trigger.setDisable(false);
            }
            Throwable ex = task.getException();
            String message = ex != null && ex.getMessage() != null
                    ? ex.getMessage()
                    : "Failed to save student data.";
            showAlert("Save Failed", message, Alert.AlertType.ERROR);
        });

        Thread background = new Thread(task, "write-data-task");
        background.setDaemon(true);
        background.start();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void applySaveButtonLabel() {
        if (saveButton != null) {
            saveButton.setText(updateMode ? "Update" : "Save");
        }
    }
}
