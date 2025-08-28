/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.database;

import com.globemed.models.Patient;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Hansana
 */
public class PatientDAO {

    public List<Patient> getAllPatients() throws SQLException {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Patient patient = new Patient();
                patient.setId(rs.getLong("id"));
                patient.setName(rs.getString("name"));
                patient.setDob(rs.getDate("dob") != null ? rs.getDate("dob").toLocalDate() : null);
                patient.setAddress(rs.getString("address"));
                patient.setPhone(rs.getString("phone"));
                patient.setMedicalHistory(rs.getString("medical_history"));
                patients.add(patient);
            }
        }

        return patients;
    }

    public Patient getPatientById(Long id) throws SQLException {
        String sql = "SELECT * FROM patients WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Patient patient = new Patient();
                    patient.setId(rs.getLong("id"));
                    patient.setName(rs.getString("name"));
                    patient.setDob(rs.getDate("dob") != null ? rs.getDate("dob").toLocalDate() : null);
                    patient.setAddress(rs.getString("address"));
                    patient.setPhone(rs.getString("phone"));
                    patient.setMedicalHistory(rs.getString("medical_history"));
                    return patient;
                }
            }
        }

        return null;
    }

    public Long insertPatient(Patient patient) throws SQLException {
        String sql = "INSERT INTO patients (name, dob, address, phone, medical_history) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, patient.getName());
            stmt.setDate(2, patient.getDob() != null ? Date.valueOf(patient.getDob()) : null);
            stmt.setString(3, patient.getAddress());
            stmt.setString(4, patient.getPhone());
            stmt.setString(5, patient.getMedicalHistory());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating patient failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Long id = generatedKeys.getLong(1);
                    patient.setId(id);
                    return id;
                } else {
                    throw new SQLException("Creating patient failed, no ID obtained.");
                }
            }
        }
    }

    public boolean updatePatient(Patient patient) throws SQLException {
        String sql = "UPDATE patients SET name = ?, dob = ?, address = ?, phone = ?, medical_history = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, patient.getName());
            stmt.setDate(2, patient.getDob() != null ? Date.valueOf(patient.getDob()) : null);
            stmt.setString(3, patient.getAddress());
            stmt.setString(4, patient.getPhone());
            stmt.setString(5, patient.getMedicalHistory());
            stmt.setLong(6, patient.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deletePatient(Long id) throws SQLException {
        String sql = "DELETE FROM patients WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
}
