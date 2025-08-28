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
public class TreatmentReportVisitor implements ReportVisitor {

    private StringBuilder reportContent;
    private List<String> treatments;
    private List<String> medications;
    private String patientInfo;
    private String appointmentInfo;
    private PatientDAO patientDAO;
    private StaffDAO staffDAO;

    public TreatmentReportVisitor() {
        this.reportContent = new StringBuilder();
        this.treatments = new ArrayList<>();
        this.medications = new ArrayList<>();
        this.patientDAO = new PatientDAO();
        this.staffDAO = new StaffDAO();
        reset();
    }

    @Override
    public void visitPatient(Patient patient) {
        patientInfo = String.format(
                "Patient: %s (ID: %d)\n"
                + "DOB: %s\n"
                + "Address: %s\n"
                + "Phone: %s\n"
                + "Medical History: %s\n",
                patient.getName(),
                patient.getId(),
                patient.getDob() != null ? patient.getDob().toString() : "N/A",
                patient.getAddress() != null ? patient.getAddress() : "N/A",
                patient.getPhone() != null ? patient.getPhone() : "N/A",
                patient.getMedicalHistory() != null ? patient.getMedicalHistory() : "No history available"
        );
    }

    @Override
    public void visitAppointment(Appointment appointment) {
        try {
            Staff doctor = staffDAO.getStaffById(appointment.getStaffId());
            String doctorName = doctor != null ? doctor.getName() : "Unknown Doctor";

            appointmentInfo = String.format(
                    "Appointment Details:\n"
                    + "Date & Time: %s\n"
                    + "Location: %s\n"
                    + "Doctor: %s\n"
                    + "Status: %s\n",
                    appointment.getAppointmentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    appointment.getLocation(),
                    doctorName,
                    appointment.getStatus()
            );
        } catch (SQLException e) {
            appointmentInfo = "Error retrieving appointment details: " + e.getMessage();
        }
    }

    @Override
    public void visitBill(Bill bill) {
        // Bill information is not typically included in treatment reports
        // but we can note if there were any billing issues
        if (!"PAID".equals(bill.getClaimStatus())) {
            reportContent.append("Note: Outstanding billing matters exist for this treatment\n");
        }
    }

    @Override
    public void visitBillItem(BillItem billItem) {
        switch (billItem.getItemType()) {
            case "TREATMENT":
                treatments.add(billItem.getDescription() + " - $" + billItem.getCost());
                break;
            case "MEDICATION":
                medications.add(billItem.getDescription() + " - $" + billItem.getCost());
                break;
            case "CONSULTATION":
                treatments.add("Consultation: " + billItem.getDescription() + " - $" + billItem.getCost());
                break;
        }
    }

    @Override
    public void visitStaff(Staff staff) {
        // Staff visit can add provider information to the report
        reportContent.append("Healthcare Provider: ").append(staff.getName())
                .append(" (ID: ").append(staff.getId()).append(")\n");
    }

    @Override
    public String generateReport() {
        StringBuilder finalReport = new StringBuilder();

        finalReport.append("=== TREATMENT SUMMARY REPORT ===\n");
        finalReport.append("Generated on: ").append(java.time.LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n\n");

        if (patientInfo != null) {
            finalReport.append(patientInfo).append("\n");
        }

        if (appointmentInfo != null) {
            finalReport.append(appointmentInfo).append("\n");
        }

        if (!treatments.isEmpty()) {
            finalReport.append("TREATMENTS PROVIDED:\n");
            for (String treatment : treatments) {
                finalReport.append("- ").append(treatment).append("\n");
            }
            finalReport.append("\n");
        }

        if (!medications.isEmpty()) {
            finalReport.append("MEDICATIONS PRESCRIBED:\n");
            for (String medication : medications) {
                finalReport.append("- ").append(medication).append("\n");
            }
            finalReport.append("\n");
        }

        finalReport.append(reportContent.toString());
        finalReport.append("=== END OF TREATMENT REPORT ===\n");

        return finalReport.toString();
    }

    @Override
    public void reset() {
        reportContent.setLength(0);
        treatments.clear();
        medications.clear();
        patientInfo = null;
        appointmentInfo = null;
    }
}
