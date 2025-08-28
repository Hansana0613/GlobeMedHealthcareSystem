/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.observer;

import com.globemed.models.Appointment;
import com.globemed.models.Patient;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Hansana
 */
public class PatientObserver implements Observer {

    private Patient patient;

    public PatientObserver(Patient patient) {
        this.patient = patient;
    }

    @Override
    public void onAppointmentCreated(Appointment appointment) {
        if (appointment.getPatientId().equals(patient.getId())) {
            sendNotificationToPatient("APPOINTMENT_CONFIRMATION",
                    "Your appointment has been scheduled for "
                    + appointment.getAppointmentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                    + " at " + appointment.getLocation());
        }
    }

    @Override
    public void onAppointmentUpdated(Appointment appointment, LocalDateTime oldTime, String oldLocation) {
        if (appointment.getPatientId().equals(patient.getId())) {
            sendNotificationToPatient("APPOINTMENT_RESCHEDULED",
                    "Your appointment has been moved from "
                    + oldTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + " at " + oldLocation
                    + " to " + appointment.getAppointmentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                    + " at " + appointment.getLocation());
        }
    }

    @Override
    public void onAppointmentCancelled(Appointment appointment) {
        if (appointment.getPatientId().equals(patient.getId())) {
            sendNotificationToPatient("APPOINTMENT_CANCELLED",
                    "Your appointment scheduled for "
                    + appointment.getAppointmentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                    + " has been cancelled.");
        }
    }

    private void sendNotificationToPatient(String type, String message) {
        // In a real system, this would send email/SMS/push notification
        System.out.println("📱 NOTIFICATION to " + patient.getName() + " (" + patient.getPhone() + "): " + message);

        // Could also log to database for notification history
        logNotification(type, message);
    }

    private void logNotification(String type, String message) {
        // Log notification for audit trail
        System.out.println("📝 NOTIFICATION LOG: Patient ID " + patient.getId()
                + " - Type: " + type + " - Message: " + message);
    }

    public Patient getPatient() {
        return patient;
    }
}
