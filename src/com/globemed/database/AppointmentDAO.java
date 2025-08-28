/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.database;

import com.globemed.models.Appointment;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Hansana
 */
public class AppointmentDAO {

    public List<Appointment> getAllAppointments() throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments ORDER BY appointment_time";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
        }

        return appointments;
    }

    public Appointment getAppointmentById(Long id) throws SQLException {
        String sql = "SELECT * FROM appointments WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAppointment(rs);
                }
            }
        }

        return null;
    }

    public List<Appointment> getAppointmentsByStaffAndDate(Long staffId, LocalDate date) throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE staff_id = ? AND DATE(appointment_time) = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, staffId);
            stmt.setDate(2, Date.valueOf(date));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(mapResultSetToAppointment(rs));
                }
            }
        }

        return appointments;
    }

    public List<Appointment> getAppointmentsByStaffAndTimeRange(Long staffId, LocalDateTime start, LocalDateTime end) throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE staff_id = ? AND appointment_time BETWEEN ? AND ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, staffId);
            stmt.setTimestamp(2, Timestamp.valueOf(start));
            stmt.setTimestamp(3, Timestamp.valueOf(end));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(mapResultSetToAppointment(rs));
                }
            }
        }

        return appointments;
    }

    public List<Appointment> getAppointmentsByLocationAndTimeRange(String location, LocalDateTime start, LocalDateTime end) throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE location = ? AND appointment_time BETWEEN ? AND ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, location);
            stmt.setTimestamp(2, Timestamp.valueOf(start));
            stmt.setTimestamp(3, Timestamp.valueOf(end));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(mapResultSetToAppointment(rs));
                }
            }
        }

        return appointments;
    }

    public Long insertAppointment(Appointment appointment) throws SQLException {
        String sql = "INSERT INTO appointments (patient_id, staff_id, appointment_time, location, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, appointment.getPatientId());
            stmt.setLong(2, appointment.getStaffId());
            stmt.setTimestamp(3, Timestamp.valueOf(appointment.getAppointmentTime()));
            stmt.setString(4, appointment.getLocation());
            stmt.setString(5, appointment.getStatus());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating appointment failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Long id = generatedKeys.getLong(1);
                    appointment.setId(id);
                    return id;
                } else {
                    throw new SQLException("Creating appointment failed, no ID obtained.");
                }
            }
        }
    }

    public boolean updateAppointment(Appointment appointment) throws SQLException {
        String sql = "UPDATE appointments SET patient_id = ?, staff_id = ?, appointment_time = ?, location = ?, status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, appointment.getPatientId());
            stmt.setLong(2, appointment.getStaffId());
            stmt.setTimestamp(3, Timestamp.valueOf(appointment.getAppointmentTime()));
            stmt.setString(4, appointment.getLocation());
            stmt.setString(5, appointment.getStatus());
            stmt.setLong(6, appointment.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteAppointment(Long id) throws SQLException {
        String sql = "DELETE FROM appointments WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private Appointment mapResultSetToAppointment(ResultSet rs) throws SQLException {
        Appointment appointment = new Appointment();
        appointment.setId(rs.getLong("id"));
        appointment.setPatientId(rs.getLong("patient_id"));
        appointment.setStaffId(rs.getLong("staff_id"));
        appointment.setAppointmentTime(rs.getTimestamp("appointment_time").toLocalDateTime());
        appointment.setLocation(rs.getString("location"));
        appointment.setStatus(rs.getString("status"));
        return appointment;
    }
}
