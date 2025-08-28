/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.visitor;

import com.globemed.models.*;
import com.globemed.database.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Hansana
 */
public class DiagnosticReportVisitor implements ReportVisitor {

    private StringBuilder reportContent;
    private List<String> diagnosticTests;
    private List<String> consultations;
    private String patientInfo;
    private String appointmentInfo;
    private StaffDAO staffDAO;

    public DiagnosticReportVisitor() {
        this.reportContent = new StringBuilder();
        this.diagnosticTests = new ArrayList<>();
        this.consultations = new ArrayList<>();
        this.staffDAO = new StaffDAO();
        reset();
    }

    @Override
    public void visitPatient(Patient patient) {
        patientInfo = String.format(
                "Patient Information:\n"
                + "Name: %s (ID: %d)\n"
                + "Date of Birth: %s\n"
                + "Medical History: %s\n",
                patient.getName(),
                patient.getId(),
                patient.getDob() != null ? patient.getDob().toString() : "N/A",
                patient.getMedicalHistory() != null ? patient.getMedicalHistory() : "No previous history"
        );
    }

    @Override
    public void visitAppointment(Appointment appointment) {
        try {
            Staff doctor = staffDAO.getStaffById(appointment.getStaffId());
            String doctorName = doctor != null ? doctor.getName() : "Unknown Doctor";

            appointmentInfo = String.format(
                    "Examination Details:\n"
                    + "Date: %s\n"
                    + "Examining Physician: %s\n"
                    + "Location: %s\n"
                    + "Appointment Status: %s\n",
                    appointment.getAppointmentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    doctorName,
                    appointment.getLocation(),
                    appointment.getStatus()
            );
        } catch (SQLException e) {
            appointmentInfo = "Error retrieving appointment details: " + e.getMessage();
        }
    }

    @Override
    public void visitBill(Bill bill) {
        // Bills can indicate what services were provided
        reportContent.append("Services billed on: ")
                .append(java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .append("\n");
    }

    @Override
    public void visitBillItem(BillItem billItem) {
        switch (billItem.getItemType()) {
            case "DIAGNOSTIC":
                diagnosticTests.add(billItem.getDescription() + " - Result pending/completed");
                break;
            case "CONSULTATION":
                consultations.add(billItem.getDescription() + " - Clinical assessment provided");
                break;
        }
    }

    @Override
    public void visitStaff(Staff staff) {
        reportContent.append("Report prepared under supervision of: ")
                .append(staff.getName())
                .append(" (Staff ID: ").append(staff.getId()).append(")\n");
    }

    @Override
    public String generateReport() {
        StringBuilder finalReport = new StringBuilder();

        finalReport.append("=== DIAGNOSTIC RESULTS REPORT ===\n");
        finalReport.append("Generated on: ").append(java.time.LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n\n");

        if (patientInfo != null) {
            finalReport.append(patientInfo).append("\n");
        }

        if (appointmentInfo != null) {
            finalReport.append(appointmentInfo).append("\n");
        }

        if (!consultations.isEmpty()) {
            finalReport.append("CLINICAL CONSULTATIONS:\n");
            for (String consultation : consultations) {
                finalReport.append("- ").append(consultation).append("\n");
            }
            finalReport.append("\n");
        }

        if (!diagnosticTests.isEmpty()) {
            finalReport.append("DIAGNOSTIC TESTS PERFORMED:\n");
            for (String test : diagnosticTests) {
                finalReport.append("- ").append(test).append("\n");
            }
            finalReport.append("\n");
        }

        finalReport.append("CLINICAL FINDINGS:\n");
        finalReport.append("(To be completed by examining physician)\n\n");

        finalReport.append("RECOMMENDATIONS:\n");
        finalReport.append("(To be completed by examining physician)\n\n");

        finalReport.append(reportContent.toString());
        finalReport.append("=== END OF DIAGNOSTIC REPORT ===\n");

        return finalReport.toString();
    }

    @Override
    public void reset() {
        reportContent.setLength(0);
        diagnosticTests.clear();
        consultations.clear();
        patientInfo = null;
        appointmentInfo = null;
    }
}
