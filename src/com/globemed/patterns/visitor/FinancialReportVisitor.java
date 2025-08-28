/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.visitor;

import com.globemed.models.*;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Hansana
 */
public class FinancialReportVisitor implements ReportVisitor {

    private StringBuilder reportContent;
    private BigDecimal totalRevenue;
    private BigDecimal totalPending;
    private Map<String, BigDecimal> revenueByCategory;
    private Map<String, Integer> itemCounts;
    private String patientInfo;
    private String appointmentInfo;

    public FinancialReportVisitor() {
        this.reportContent = new StringBuilder();
        this.revenueByCategory = new HashMap<>();
        this.itemCounts = new HashMap<>();
        reset();
    }

    @Override
    public void visitPatient(Patient patient) {
        patientInfo = String.format("Patient: %s (ID: %d)", patient.getName(), patient.getId());
    }

    @Override
    public void visitAppointment(Appointment appointment) {
        appointmentInfo = String.format("Appointment: %s at %s",
                appointment.getAppointmentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                appointment.getLocation());
    }

    @Override
    public void visitBill(Bill bill) {
        totalRevenue = totalRevenue.add(bill.getTotalAmount());

        if (!"PAID".equals(bill.getClaimStatus())) {
            totalPending = totalPending.add(bill.getTotalAmount());
        }

        reportContent.append("Bill #").append(bill.getId())
                .append(" - Amount: $").append(bill.getTotalAmount())
                .append(" - Status: ").append(bill.getClaimStatus()).append("\n");
    }

    @Override
    public void visitBillItem(BillItem billItem) {
        String category = String.valueOf(billItem.getItemType());
        BigDecimal cost = billItem.getCost();

        // Update category totals
        revenueByCategory.merge(category, cost, BigDecimal::add);
        itemCounts.merge(category, 1, Integer::sum);

        reportContent.append("  - ").append(billItem.getDescription())
                .append(" (").append(category).append("): $").append(cost).append("\n");
    }

    @Override
    public void visitStaff(Staff staff) {
        // Staff information for financial reports
        reportContent.append("Service Provider: ").append(staff.getName()).append("\n");
    }

    @Override
    public String generateReport() {
        StringBuilder finalReport = new StringBuilder();

        finalReport.append("=== FINANCIAL REPORT ===\n");
        finalReport.append("Generated on: ").append(java.time.LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n\n");

        if (patientInfo != null) {
            finalReport.append(patientInfo).append("\n");
        }

        if (appointmentInfo != null) {
            finalReport.append(appointmentInfo).append("\n");
        }

        finalReport.append("\nFINANCIAL SUMMARY:\n");
        finalReport.append("\nFINANCIAL SUMMARY:\n");
        finalReport.append("Total Revenue: $").append(totalRevenue).append("\n");
        finalReport.append("Total Pending: $").append(totalPending).append("\n");
        finalReport.append("Total Collected: $").append(totalRevenue.subtract(totalPending)).append("\n\n");

        finalReport.append("REVENUE BY CATEGORY:\n");
        for (Map.Entry<String, BigDecimal> entry : revenueByCategory.entrySet()) {
            finalReport.append("- ").append(entry.getKey()).append(": $")
                    .append(entry.getValue()).append(" (")
                    .append(itemCounts.get(entry.getKey())).append(" items)\n");
        }

        finalReport.append("\nDETAILED BREAKDOWN:\n");
        finalReport.append(reportContent.toString());
        finalReport.append("=== END OF FINANCIAL REPORT ===\n");

        return finalReport.toString();
    }

    @Override
    public void reset() {
        reportContent.setLength(0);
        totalRevenue = BigDecimal.ZERO;
        totalPending = BigDecimal.ZERO;
        revenueByCategory.clear();
        itemCounts.clear();
        patientInfo = null;
        appointmentInfo = null;
    }
}
