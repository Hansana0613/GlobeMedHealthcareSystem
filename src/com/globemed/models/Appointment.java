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
public class Appointment {

    private Long id;
    private Long patientId;
    private Long staffId;
    private LocalDateTime appointmentTime;
    private String location;
    private String status; // SCHEDULED, COMPLETED, CANCELLED

    // Constructors
    public Appointment() {
    }

    public Appointment(Long patientId, Long staffId, LocalDateTime appointmentTime, String location) {
        this.patientId = patientId;
        this.staffId = staffId;
        this.appointmentTime = appointmentTime;
        this.location = location;
        this.status = "SCHEDULED";
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Appointment{id=" + id + ", patientId=" + patientId
                + ", staffId=" + staffId + ", appointmentTime=" + appointmentTime
                + ", location='" + location + "', status='" + status + "'}";
    }
}
