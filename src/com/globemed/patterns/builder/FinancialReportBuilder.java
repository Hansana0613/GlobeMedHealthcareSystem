/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.builder;

import com.globemed.models.*;
import com.globemed.patterns.visitor.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Hansana
 */
public class FinancialReportBuilder implements ReportBuilder {

    private Report report;
    private FinancialReportVisitor visitor;
    private List<String> customSections;

    public FinancialReportBuilder() {
        this.visitor = new FinancialReportVisitor();
        this.customSections = new ArrayList<>();
        reset();
    }

    @Override
    public ReportBuilder setTitle(String title) {
        report.setTitle(title);
        return this;
    }

    @Override
    public ReportBuilder setPatient(Patient patient) {
        report.setPatientId(patient.getId());
        VisitablePatient visitablePatient = new VisitablePatient(patient);
        visitablePatient.accept(visitor);
        return this;
    }

    @Override
    public ReportBuilder setAppointment(Appointment appointment) {
        VisitableAppointment visitableAppointment = new VisitableAppointment(appointment);
        visitableAppointment.accept(visitor);
        return this;
    }

    @Override
    public ReportBuilder addBill(Bill bill) {
        if (report.getBillId() == null) {
            report.setBillId(bill.getId());
        }
        VisitableBill visitableBill = new VisitableBill(bill);
        visitableBill.accept(visitor);
        return this;
    }

    @Override
    public ReportBuilder addBills(List<Bill> bills) {
        for (Bill bill : bills) {
            addBill(bill);
        }
        return this;
    }

    @Override
    public ReportBuilder setSummary(String summary) {
        report.setSummary(summary);
        return this;
    }

    @Override
    public ReportBuilder setFooter(String footer) {
        report.setFooter(footer);
        return this;
    }

    @Override
    public ReportBuilder addCustomSection(String sectionTitle, String content) {
        customSections.add("=== " + sectionTitle.toUpperCase() + " ===\n" + content + "\n");
        return this;
    }

    @Override
    public Report build() {
        // Generate the main content using visitor
        String mainContent = visitor.generateReport();

        // Build the complete report
        StringBuilder finalContent = new StringBuilder();

        // Add title if set
        if (report.getTitle() != null) {
            finalContent.append("*** ").append(report.getTitle()).append(" ***\n\n");
        }

        // Add main visitor-generated content
        finalContent.append(mainContent);

        // Add custom sections
        for (String section : customSections) {
            finalContent.append("\n").append(section);
        }

        // Add summary if set
        if (report.getSummary() != null) {
            finalContent.append("\nFINANCIAL SUMMARY:\n").append(report.getSummary()).append("\n");
        }

        // Add footer if set
        if (report.getFooter() != null) {
            finalContent.append("\n").append(report.getFooter()).append("\n");
        }

        report.setContent(finalContent.toString());
        report.setType("FINANCIAL");

        return report;
    }

    @Override
    public void reset() {
        this.report = new Report();
        this.visitor.reset();
        this.customSections.clear();
    }
}
