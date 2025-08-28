/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.observer;

import com.globemed.models.Appointment;
import com.globemed.models.Staff;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Hansana
 */
public class DoctorObserver implements Observer {

    private Staff doctor;

    public DoctorObserver(Staff doctor) {
        this.doctor = doctor;
    }

    @Override
    public void onAppointmentCreated(Appointment appointment) {
        if (appointment.getStaffId().equals(doctor.getId())) {
            sendNotificationToDoctor("NEW_APPOINTMENT",
                    "New appointment scheduled for "
                    + appointment.getAppointmentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                    + " at " + appointment.getLocation()
                    + " with Patient ID: " + appointment.getPatientId());
        }
    }

    @Override
    public void onAppointmentUpdated(Appointment appointment, LocalDateTime oldTime, String oldLocation) {
        if (appointment.getStaffId().equals(doctor.getId())) {
            sendNotificationToDoctor("APPOINTMENT_RESCHEDULED",
                    "Appointment moved from "
                    + oldTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + " at " + oldLocation
                    + " to " + appointment.getAppointmentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                    + " at " + appointment.getLocation());
        }
    }

    @Override
    public void onAppointmentCancelled(Appointment appointment) {
        if (appointment.getStaffId().equals(doctor.getId())) {
            sendNotificationToDoctor("APPOINTMENT_CANCELLED",
                    "Appointment for "
                    + appointment.getAppointmentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                    + " has been cancelled. Time slot is now available.");
        }
    }

    private void sendNotificationToDoctor(String type, String message) {
        // In a real system, this would send email/internal messaging
        System.out.println("👩‍⚕️ DOCTOR NOTIFICATION to " + doctor.getName() + " (" + doctor.getEmail() + "): " + message);

        // Could also update doctor's calendar/schedule system
        updateDoctorCalendar(type, message);
    }

    private void updateDoctorCalendar(String type, String message) {
        // Update doctor's calendar system
        System.out.println("📅 CALENDAR UPDATE: Doctor ID " + doctor.getId()
                + " - Type: " + type + " - Update: " + message);
    }

    public Staff getDoctor() {
        return doctor;
    }
}
