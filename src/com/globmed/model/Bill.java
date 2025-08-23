package com.globmed.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Hansana
 */
public class Bill {

    private Long id;
    private Appointment appointment;
    private Double totalAmount;
    private ClaimStatus claimStatus = ClaimStatus.PENDING;
    private String insuranceDetails;
    private List<BillItem> items = new ArrayList<>();

    public enum ClaimStatus {
        PENDING, APPROVED, REJECTED, PAID
    }

    // Default constructor
    public Bill() {
    }

    // Getters/Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public ClaimStatus getClaimStatus() {
        return claimStatus;
    }

    public void setClaimStatus(ClaimStatus claimStatus) {
        this.claimStatus = claimStatus;
    }

    public String getInsuranceDetails() {
        return insuranceDetails;
    }

    public void setInsuranceDetails(String insuranceDetails) {
        this.insuranceDetails = insuranceDetails;
    }

    public List<BillItem> getItems() {
        return items;
    }

    public void setItems(List<BillItem> items) {
        this.items = items;
    }
}
