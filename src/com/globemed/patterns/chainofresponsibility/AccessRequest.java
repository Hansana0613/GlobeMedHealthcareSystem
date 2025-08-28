/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.chainofresponsibility;

import com.globemed.models.Staff;

/**
 *
 * @author Hansana
 */
public class AccessRequest {

    private Staff staff;
    private String action;
    private Long patientId;
    private String clientIP;

    public AccessRequest(Staff staff, String action, Long patientId, String clientIP) {
        this.staff = staff;
        this.action = action;
        this.patientId = patientId;
        this.clientIP = clientIP;
    }

    // Getters
    public Staff getStaff() {
        return staff;
    }

    public String getAction() {
        return action;
    }

    public Long getPatientId() {
        return patientId;
    }

    public String getClientIP() {
        return clientIP;
    }
}
