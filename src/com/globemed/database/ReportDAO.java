/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.database;

import com.globemed.models.Report;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Hansana
 */
public class ReportDAO {

    public List<Report> getAllReports() throws SQLException {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT * FROM reports ORDER BY generated_at DESC";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                reports.add(mapResultSetToReport(rs));
            }
        }

        return reports;
    }

    public Report getReportById(Long id) throws SQLException {
        String sql = "SELECT * FROM reports WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReport(rs);
                }
            }
        }

        return null;
    }

    public List<Report> getReportsByType(String type) throws SQLException {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT * FROM reports WHERE type = ? ORDER BY generated_at DESC";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, type);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapResultSetToReport(rs));
                }
            }
        }

        return reports;
    }

    public List<Report> getReportsByPatientId(Long patientId) throws SQLException {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT * FROM reports WHERE patient_id = ? ORDER BY generated_at DESC";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, patientId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapResultSetToReport(rs));
                }
            }
        }

        return reports;
    }

    public List<Report> getReportsByDateRange(LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT * FROM reports WHERE generated_at BETWEEN ? AND ? ORDER BY generated_at DESC";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(startDate));
            stmt.setTimestamp(2, Timestamp.valueOf(endDate));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapResultSetToReport(rs));
                }
            }
        }

        return reports;
    }

    public Long insertReport(Report report) throws SQLException {
        String sql = "INSERT INTO reports (type, content, patient_id, bill_id, title, summary, footer, generated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, report.getType());
            stmt.setString(2, report.getContent());
            stmt.setObject(3, report.getPatientId());
            stmt.setObject(4, report.getBillId());
            stmt.setString(5, report.getTitle());
            stmt.setString(6, report.getSummary());
            stmt.setString(7, report.getFooter());
            stmt.setTimestamp(8, Timestamp.valueOf(report.getGeneratedAt()));

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating report failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Long id = generatedKeys.getLong(1);
                    report.setId(id);
                    return id;
                } else {
                    throw new SQLException("Creating report failed, no ID obtained.");
                }
            }
        }
    }

    public boolean updateReport(Report report) throws SQLException {
        String sql = "UPDATE reports SET type = ?, content = ?, patient_id = ?, bill_id = ?, title = ?, summary = ?, footer = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, report.getType());
            stmt.setString(2, report.getContent());
            stmt.setObject(3, report.getPatientId());
            stmt.setObject(4, report.getBillId());
            stmt.setString(5, report.getTitle());
            stmt.setString(6, report.getSummary());
            stmt.setString(7, report.getFooter());
            stmt.setLong(8, report.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteReport(Long id) throws SQLException {
        String sql = "DELETE FROM reports WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private Report mapResultSetToReport(ResultSet rs) throws SQLException {
        Report report = new Report();
        report.setId(rs.getLong("id"));
        report.setType(rs.getString("type"));
        report.setContent(rs.getString("content"));
        report.setPatientId(rs.getObject("patient_id", Long.class));
        report.setBillId(rs.getObject("bill_id", Long.class));
        report.setTitle(rs.getString("title"));
        report.setSummary(rs.getString("summary"));
        report.setFooter(rs.getString("footer"));
        report.setGeneratedAt(rs.getTimestamp("generated_at").toLocalDateTime());
        return report;
    }
}
