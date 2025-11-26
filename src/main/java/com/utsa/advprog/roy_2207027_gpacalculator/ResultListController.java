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
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResultListController {
    @FXML
    private TableView<SavedResult> resultsTable;
    @FXML
    private TableColumn<SavedResult, String> dateColumn;
    @FXML
    private TableColumn<SavedResult, String> studentNameColumn;
    @FXML
    private TableColumn<SavedResult, String> rollNumberColumn;
    @FXML
    private TableColumn<SavedResult, String> universityColumn;
    @FXML
    private TableColumn<SavedResult, String> coursesColumn;
    @FXML
    private TableColumn<SavedResult, String> creditsColumn;
    @FXML
    private TableColumn<SavedResult, String> gpaColumn;

    private final ObservableList<SavedResult> savedResults = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        DatabaseManager.initialize();
        configureColumns();
        resultsTable.setItems(savedResults);
        refreshResults();
    }

    private void configureColumns() {
        dateColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDisplayDate()));
        studentNameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
        rollNumberColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getRoll()));
        universityColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getUniversity()));
        coursesColumn.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getCourseCount())));
        creditsColumn.setCellValueFactory(cell -> new SimpleStringProperty(formatNumber(cell.getValue().getEarnedCredits())));
        gpaColumn.setCellValueFactory(cell -> new SimpleStringProperty(formatNumber(cell.getValue().getGpa())));
    }

    private String formatNumber(double value) {
        return String.format("%.2f", value);
    }

    private void refreshResults() {
        savedResults.setAll(ReadData.loadAllResults());
    }

    public void backButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Welcome.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.show();
        } catch (IOException _) {}
    }

    public void deleteResult(ActionEvent event) {
        SavedResult selected = getSelectedResult();
        if (selected == null) {
            showAlert("Error", "Please select row", Alert.AlertType.INFORMATION);
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Delete the row?", ButtonType.YES, ButtonType.NO);
        confirmation.setTitle("Confirm Delete");
        confirmation.setHeaderText(null);
        confirmation.initOwner(resultsTable.getScene().getWindow());

        confirmation.showAndWait().ifPresent(response -> {
            if (response != ButtonType.YES) {
                return;
            }
            String sql = "DELETE FROM students WHERE id = ?";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement ps = conn != null ? conn.prepareStatement(sql) : null) {
                ps.setInt(1, selected.getId());
                ps.executeUpdate();
                refreshResults();
            } catch (SQLException e) {
                showAlert("Delete Failed", e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    public void editResult(ActionEvent event) {
        SavedResult selected = getSelectedResult();
        if (selected == null) {
            showAlert("Error", "Select ro to edit.", Alert.AlertType.INFORMATION);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("user-info.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene previousScene = stage.getScene();
            Parent root = loader.load();
            UserInfoController controller = loader.getController();

            Student student = getSelectedStudent();
            Course[] courses = student.courses != null ? student.courses : new Course[0];
            controller.storeScene(previousScene);
            controller.loadExistingData(student, courses, true);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException _) {}
    }

    Student getSelectedStudent() {
        SavedResult selected = getSelectedResult();
        if (selected == null) {
            return null;
        }
        Course[] courses = parseCourses(selected.getCoursesData());

        Student student = new Student(
                selected.getName(),
                selected.getRoll(),
                selected.getMobile(),
                selected.getEmail(),
                selected.getUniversity()
        );
        student.setId(selected.getId());

        student.gpa = selected.getGpa();
        student.totalCredits = selected.getTotalCredits();
        student.earnedCredits = selected.getEarnedCredits();
        student.courses = courses;

        return student;
    }

    Course[] getSelectedCourses() {
        SavedResult selected = getSelectedResult();
        if (selected == null) {
            return new Course[0];
        }
        return parseCourses(selected.getCoursesData());
    }

    public void viewResult(ActionEvent event) throws IOException {
        SavedResult selected = getSelectedResult();
        if (selected == null) {
            showAlert("No Selection", "Please select a saved result to view.", Alert.AlertType.INFORMATION);
            return;
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ResultPage.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = loader.load();
        ResultController controller = loader.getController();
        Student student = getSelectedStudent();
        if (student == null) {
            return;
        }
        Course[] courses = student.courses != null ? student.courses : new Course[0];
        ObservableList<Course> observableCourses = FXCollections.observableArrayList();
        observableCourses.addAll(courses);
        controller.setStudent(student);
        controller.setCourseList(observableCourses);
        controller.setUpdateMode(false);
        controller.storeScene(stage.getScene());
        stage.setScene(new Scene(root));
        stage.show();
    }

    private SavedResult getSelectedResult() {
        return resultsTable.getSelectionModel().getSelectedItem();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.initOwner(resultsTable.getScene().getWindow());
        alert.showAndWait();
    }

    private Course[] parseCourses(String data) {
        if (data == null || data.isEmpty() || data.equals("[]")) {
            return new Course[0];
        }

        String trimmed = data.trim();
        if (trimmed.length() < 2 || trimmed.charAt(0) != '[' || trimmed.charAt(trimmed.length() - 1) != ']') {
            return new Course[0];
        }

        trimmed = trimmed.substring(1, trimmed.length() - 1);
        if (trimmed.isEmpty()) {
            return new Course[0];
        }

        List<Course> courses = new ArrayList<>();
        String[] objects = trimmed.split("(?<=\\}),\\s*(?=\\{)");
        for (String object : objects) {
            String entry = object.trim();
            if (entry.isEmpty()) {
                continue;
            }
            if (entry.charAt(0) == '{') {
                entry = entry.substring(1);
            }
            if (!entry.isEmpty() && entry.charAt(entry.length() - 1) == '}') {
                entry = entry.substring(0, entry.length() - 1);
            }

            Map<String, String> stringValues = new HashMap<>();
            Map<String, Double> numberValues = new HashMap<>();
            Matcher matcher = COURSE_FIELD_PATTERN.matcher(entry);
            while (matcher.find()) {
                String key = matcher.group(1);
                String stringValue = matcher.group(2);
                String numberValue = matcher.group(3);

                if (stringValue != null) {
                    stringValues.put(key, unescapeJson(stringValue));
                } else if (numberValue != null) {
                    numberValues.put(key, safeParse(numberValue));
                }
            }

            String name = stringValues.getOrDefault("name", "");
            String code = stringValues.getOrDefault("code", "");
            double credit = numberValues.getOrDefault("credit", 0.0);
            String teacher1 = stringValues.getOrDefault("teacher1Name", "");
            String teacher2 = stringValues.getOrDefault("teacher2Name", "");
            String gradeLetter = stringValues.getOrDefault("gradeLetter", "");

            Course course = new Course(name, code, credit, teacher1, teacher2, gradeLetter);
            courses.add(course);
        }

        return courses.toArray(new Course[0]);
    }

    private double safeParse(String numberValue) {
        try {
            return Double.parseDouble(numberValue);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private String unescapeJson(String value) {
        StringBuilder builder = new StringBuilder();
        boolean escaping = false;
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if (escaping) {
                builder.append(switch (ch) {
                    case '\\' -> '\\';
                    case '"' -> '"';
                    case 'n' -> '\n';
                    case 'r' -> '\r';
                    default -> ch;
                });
                escaping = false;
            } else if (ch == '\\') {
                escaping = true;
            } else {
                builder.append(ch);
            }
        }
        return builder.toString();
    }

    private static final Pattern COURSE_FIELD_PATTERN = Pattern.compile("\\\"(\\w+)\\\":(?:\\\"((?:\\\\.|[^\\\"])*)\\\"|([0-9.]+))");
}
