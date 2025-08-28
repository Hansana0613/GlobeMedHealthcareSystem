/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.chainofresponsibility;

import java.math.BigDecimal;

/**
 *
 * @author Hansana
 */
public class ClaimResult {

    private boolean approved;
    private String message;
    private BigDecimal approvedAmount;
    private BigDecimal patientResponsibility;
    private String processingNotes;

    public ClaimResult(boolean approved, String message, BigDecimal approvedAmount) {
        this.approved = approved;
        this.message = message;
        this.approvedAmount = approvedAmount;
        this.patientResponsibility = BigDecimal.ZERO;
    }

    // Getters and Setters
    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BigDecimal getApprovedAmount() {
        return approvedAmount;
    }

    public void setApprovedAmount(BigDecimal approvedAmount) {
        this.approvedAmount = approvedAmount;
    }

    public BigDecimal getPatientResponsibility() {
        return patientResponsibility;
    }

    public void setPatientResponsibility(BigDecimal patientResponsibility) {
        this.patientResponsibility = patientResponsibility;
    }

    public String getProcessingNotes() {
        return processingNotes;
    }

    public void setProcessingNotes(String processingNotes) {
        this.processingNotes = processingNotes;
    }
}
