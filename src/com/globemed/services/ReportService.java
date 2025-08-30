/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.services;

import com.globemed.models.*;
import com.globemed.database.*;
import com.globemed.patterns.visitor.*;
import com.globemed.patterns.builder.*;
import com.globemed.patterns.bridge.PermissionImplementor;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ReportService {

    private ReportDAO reportDAO;
    private PatientDAO patientDAO;
    private AppointmentDAO appointmentDAO;
    private BillDAO billDAO;
    private StaffDAO staffDAO;
    private ReportDirector reportDirector;

    public ReportService() {
        this.reportDAO = new ReportDAO();
        this.patientDAO = new PatientDAO();
        this.appointmentDAO = new AppointmentDAO();
        this.billDAO = new BillDAO();
        this.staffDAO = new StaffDAO();
        this.reportDirector = new ReportDirector();
    }

    // Generate Treatment Report
    public Report generateTreatmentReport(Long patientId, Long appointmentId) throws SQLException {
        Patient patient = patientDAO.getPatientById(patientId);
        if (patient == null) {
            throw new IllegalArgumentException("Patient not found with ID: " + patientId);
        }

        Appointment appointment = appointmentDAO.getAppointmentById(appointmentId);
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment not found with ID: " + appointmentId);
        }

        List<Bill> bills = billDAO.getBillsByPatientId(patientId);

        Report report = reportDirector.buildTreatmentReport(patient, appointment, bills);

        // Save to database
        Long reportId = reportDAO.insertReport(report);
        report.setId(reportId);

        return report;
    }

    // Generate Financial Report
    public Report generateFinancialReport(Long patientId, LocalDate startDate, LocalDate endDate) throws SQLException {
        Patient patient = patientDAO.getPatientById(patientId);
        if (patient == null) {
            throw new IllegalArgumentException("Patient not found with ID: " + patientId);
        }

        List<Bill> bills = billDAO.getBillsByPatientIdAndDateRange(patientId, startDate, endDate);

        Report report = reportDirector.buildFinancialReport(patient, bills);

        // Save to database
        Long reportId = reportDAO.insertReport(report);
        report.setId(reportId);

        return report;
    }

    // Generate Diagnostic Report
    public Report generateDiagnosticReport(Long patientId, Long appointmentId, Long billId) throws SQLException {
        Patient patient = patientDAO.getPatientById(patientId);
        if (patient == null) {
            throw new IllegalArgumentException("Patient not found with ID: " + patientId);
        }

        Appointment appointment = appointmentDAO.getAppointmentById(appointmentId);
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment not found with ID: " + appointmentId);
        }

        Bill bill = billDAO.getBillById(billId);
        if (bill == null) {
            throw new IllegalArgumentException("Bill not found with ID: " + billId);
        }

        Report report = reportDirector.buildDiagnosticReport(patient, appointment, bill);

        // Save to database
        Long reportId = reportDAO.insertReport(report);
        report.setId(reportId);

        return report;
    }

    // Get all reports
    public List<Report> getAllReports() throws SQLException {
        return reportDAO.getAllReports();
    }

    // Get reports by type
    public List<Report> getReportsByType(String type) throws SQLException {
        return reportDAO.getReportsByType(type);
    }

    // Get reports by patient
    public List<Report> getReportsByPatient(Long patientId) throws SQLException {
        return reportDAO.getReportsByPatientId(patientId);
    }

    // Get reports by date range
    public List<Report> getReportsByDateRange(LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        return reportDAO.getReportsByDateRange(startDate, endDate);
    }

    // Delete report
    public boolean deleteReport(Long reportId) throws SQLException {
        return reportDAO.deleteReport(reportId);
    }

    // Check if user has permission
    public boolean hasPermission(Staff staff, String permission) {
        // Handle null staff parameter
        if (staff == null) {
            return false;
        }
        
        // Simple role-based check - in full implementation would use Bridge pattern
        String role = getRoleNameById(staff.getRoleId());

        switch (permission) {
            case "GENERATE_REPORTS":
                return "Doctor".equals(role) || "Administrator".equals(role);
            case "DELETE_REPORTS":
                return "Administrator".equals(role);
            case "VIEW_REPORTS":
                return true; // All staff can view reports
            default:
                return false;
        }
    }

    private String getRoleNameById(Long roleId) {
        // Simple mapping - in full implementation would query database
        switch (roleId.intValue()) {
            case 1:
                return "Administrator";
            case 2:
                return "Doctor";
            case 3:
                return "Nurse";
            case 4:
                return "Pharmacist";
            default:
                return "Unknown";
        }
    }
}
