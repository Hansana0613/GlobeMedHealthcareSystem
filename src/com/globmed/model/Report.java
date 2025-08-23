package com.globmed.model;

import java.util.Date;

/**
 *
 * @author Hansana
 */
public class Report {

    private Long id;
    private ReportType type;
    private String content;
    private Date generatedAt = new Date();
    private Patient patient;
    private Bill bill;

    public enum ReportType {
        TREATMENT_SUMMARY, DIAGNOSTIC_RESULTS, FINANCIAL
    }

    // Default constructor
    public Report() {
    }

    // Getters/Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReportType getType() {
        return type;
    }

    public void setType(ReportType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(Date generatedAt) {
        this.generatedAt = generatedAt;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }
}
