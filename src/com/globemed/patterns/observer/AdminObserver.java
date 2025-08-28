/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.observer;

import com.globemed.models.Appointment;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Hansana
 */
public class AdminObserver implements Observer {

    private String adminName;

    public AdminObserver(String adminName) {
        this.adminName = adminName;
    }

    @Override
    public void onAppointmentCreated(Appointment appointment) {
        logToAdminSystem("APPOINTMENT_CREATED",
                "New appointment created - ID: " + appointment.getId()
                + ", Patient: " + appointment.getPatientId()
                + ", Staff: " + appointment.getStaffId()
                + ", Time: " + appointment.getAppointmentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                + ", Location: " + appointment.getLocation());
    }

    @Override
    public void onAppointmentUpdated(Appointment appointment, LocalDateTime oldTime, String oldLocation) {
        logToAdminSystem("APPOINTMENT_UPDATED",
                "Appointment rescheduled - ID: " + appointment.getId()
                + ", From: " + oldTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + " at " + oldLocation
                + ", To: " + appointment.getAppointmentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + " at " + appointment.getLocation());
    }

    @Override
    public void onAppointmentCancelled(Appointment appointment) {
        logToAdminSystem("APPOINTMENT_CANCELLED",
                "Appointment cancelled - ID: " + appointment.getId()
                + ", Was scheduled for: " + appointment.getAppointmentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                + ", Location: " + appointment.getLocation());
    }

    private void logToAdminSystem(String event, String details) {
        // Administrative logging for reporting and analytics
        System.out.println("🏥 ADMIN LOG [" + adminName + "]: " + event + " - " + details);

        // Could update statistics, generate reports, or trigger billing processes
        updateSystemMetrics(event);
    }

    private void updateSystemMetrics(String event) {
        // Update system metrics for dashboard/reporting
        switch (event) {
            case "APPOINTMENT_CREATED":
                System.out.println("📊 METRICS: Appointment count increased");
                break;
            case "APPOINTMENT_CANCELLED":
                System.out.println("📊 METRICS: Cancellation rate updated");
                break;
            case "APPOINTMENT_UPDATED":
                System.out.println("📊 METRICS: Reschedule count increased");
                break;
        }
    }
}
