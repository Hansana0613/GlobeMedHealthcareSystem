/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.models;

import java.time.LocalDateTime;

/**
 *
 * @author Hansana
 */
public class Report {

    private Long id;
    private String type; // TREATMENT_SUMMARY, DIAGNOSTIC_RESULTS, FINANCIAL
    private String content;
    private LocalDateTime generatedAt;
    private Long patientId;
    private Long billId;
    private String title;
    private String summary;
    private String footer;

    // Constructors
    public Report() {
        this.generatedAt = LocalDateTime.now();
    }

    public Report(String type, String content) {
        this.type = type;
        this.content = content;
        this.generatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    @Override
    public String toString() {
        return "Report{id=" + id + ", type='" + type + "', generatedAt=" + generatedAt + "}";
    }
}
