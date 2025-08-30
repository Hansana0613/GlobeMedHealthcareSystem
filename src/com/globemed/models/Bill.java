/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Hansana
 */
public class Bill {

    private Long id;
    private Long appointmentId;
    private BigDecimal totalAmount;
    private String claimStatus; // PENDING, APPROVED, REJECTED, PAID
    private String insuranceDetails;
    private List<BillItem> billItems;

    // Constructors
    public Bill() {
        this.billItems = new ArrayList<>();
        this.totalAmount = BigDecimal.ZERO;
    }

    public Bill(Long appointmentId, String insuranceDetails) {
        this.appointmentId = appointmentId;
        this.insuranceDetails = insuranceDetails;
        this.billItems = new ArrayList<>();
        this.totalAmount = BigDecimal.ZERO;
        this.claimStatus = "PENDING";
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getClaimStatus() {
        return claimStatus;
    }

    public void setClaimStatus(String claimStatus) {
        this.claimStatus = claimStatus;
    }

    public String getInsuranceDetails() {
        return insuranceDetails;
    }

    public void setInsuranceDetails(String insuranceDetails) {
        this.insuranceDetails = insuranceDetails;
    }

    public List<BillItem> getBillItems() {
        return billItems;
    }

    public void setBillItems(List<BillItem> billItems) {
        this.billItems = billItems;
    }

    public void addBillItem(BillItem item) {
        billItems.add(item);
        recalculateTotal();
    }

    public void removeBillItem(BillItem item) {
        billItems.remove(item);
        recalculateTotal();
    }

    private void recalculateTotal() {
        totalAmount = billItems.stream()
                .map(BillItem::getCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public String toString() {
        return "Bill{id=" + id + ", appointmentId=" + appointmentId
                + ", totalAmount=" + totalAmount + ", claimStatus='" + claimStatus + "'}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Bill bill = (Bill) obj;
        return id != null && id.equals(bill.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
