/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.composite;

import com.globemed.models.Appointment;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 *
 * @author Hansana
 */
public class MasterBill extends BillComposite {

    private Long billId;
    private Long appointmentId;
    private LocalDateTime createdAt;
    private String status; // PENDING, APPROVED, PAID
    private String insuranceDetails;

    public MasterBill(Long billId, Long appointmentId, String insuranceDetails) {
        super("Medical Bill #" + billId);
        this.billId = billId;
        this.appointmentId = appointmentId;
        this.createdAt = LocalDateTime.now();
        this.status = "PENDING";
        this.insuranceDetails = insuranceDetails;
    }

    @Override
    public void print(String indent) {
        System.out.println(indent + "=================================");
        System.out.println(indent + "MEDICAL BILL #" + billId);
        System.out.println(indent + "Appointment ID: " + appointmentId);
        System.out.println(indent + "Created: " + createdAt);
        System.out.println(indent + "Status: " + status);
        System.out.println(indent + "Insurance: " + (insuranceDetails != null ? insuranceDetails : "Direct Pay"));
        System.out.println(indent + "=================================");
        super.print(indent);
        System.out.println(indent + "---------------------------------");
        System.out.println(indent + "TOTAL AMOUNT: $" + getCost());
        System.out.println(indent + "=================================");
    }

    // Getters and Setters
    public Long getBillId() {
        return billId;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInsuranceDetails() {
        return insuranceDetails;
    }

    public void setInsuranceDetails(String insuranceDetails) {
        this.insuranceDetails = insuranceDetails;
    }
}
