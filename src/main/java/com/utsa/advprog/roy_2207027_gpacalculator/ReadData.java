package com.utsa.advprog.roy_2207027_gpacalculator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class ReadData {

	private ReadData() {
	}

	public static ObservableList<SavedResult> loadAllResults() {
		ObservableList<SavedResult> results = FXCollections.observableArrayList();
		String sql = "SELECT id, name, roll, university, mobile, email, gpa, totalCredits, earnedCredits, coursesData " +
			"FROM students";

		try (Connection conn = DatabaseManager.getConnection();
			 PreparedStatement ps = conn != null ? conn.prepareStatement(sql) : null) {

			if (ps == null) {
				return results;
			}

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					SavedResult result = new SavedResult(
							rs.getInt("id"),
							rs.getString("name"),
							rs.getString("roll"),
							rs.getString("university"),
							rs.getString("mobile"),
							rs.getString("email"),
							rs.getDouble("gpa"),
							rs.getDouble("totalCredits"),
							rs.getDouble("earnedCredits"),
							rs.getString("coursesData")
					);
					results.add(result);
				}
			}
		} catch (SQLException e) {
			System.err.println("Failed to read students: " + e.getMessage());
		}

		return results;
	}
}
