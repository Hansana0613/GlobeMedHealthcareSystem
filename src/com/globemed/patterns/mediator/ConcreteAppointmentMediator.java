/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.mediator;

import com.globemed.database.AppointmentDAO;
import com.globemed.models.Appointment;
import com.globemed.patterns.observer.AppointmentSubject;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Hansana
 */
public class ConcreteAppointmentMediator implements AppointmentMediator {

    private List<AppointmentComponent> components;
    private AppointmentDAO appointmentDAO;
    private AppointmentSubject appointmentSubject; // Observer pattern integration

    public ConcreteAppointmentMediator() {
        this.components = new ArrayList<>();
        this.appointmentDAO = new AppointmentDAO();
        this.appointmentSubject = new AppointmentSubject();
    }

    public AppointmentSubject getAppointmentSubject() {
        return appointmentSubject;
    }

    @Override
    public boolean scheduleAppointment(Long patientId, Long staffId, LocalDateTime appointmentTime, String location) {
        try {
            // Check staff availability
            if (!checkStaffAvailability(staffId, appointmentTime)) {
                notifyComponents("SCHEDULING_FAILED", "Staff not available at requested time");
                return false;
            }

            // Check room availability
            if (!checkRoomAvailability(location, appointmentTime)) {
                notifyComponents("SCHEDULING_FAILED", "Room not available at requested time");
                return false;
            }

            // Create appointment
            Appointment appointment = new Appointment();
            appointment.setPatientId(patientId);
            appointment.setStaffId(staffId);
            appointment.setAppointmentTime(appointmentTime);
            appointment.setLocation(location);
            appointment.setStatus("SCHEDULED");

            Long appointmentId = appointmentDAO.insertAppointment(appointment);
            appointment.setId(appointmentId);

            // Notify components about successful scheduling
            notifyComponents("APPOINTMENT_SCHEDULED", appointment);

            // Notify observers
            appointmentSubject.notifyAppointmentCreated(appointment);

            return true;

        } catch (SQLException e) {
            System.err.println("Error scheduling appointment: " + e.getMessage());
            notifyComponents("SCHEDULING_ERROR", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean cancelAppointment(Long appointmentId) {
        try {
            Appointment appointment = appointmentDAO.getAppointmentById(appointmentId);
            if (appointment == null) {
                notifyComponents("CANCELLATION_FAILED", "Appointment not found");
                return false;
            }

            // Update appointment status
            appointment.setStatus("CANCELLED");
            boolean success = appointmentDAO.updateAppointment(appointment);

            if (success) {
                notifyComponents("APPOINTMENT_CANCELLED", appointment);
                appointmentSubject.notifyAppointmentCancelled(appointment);
            }

            return success;

        } catch (SQLException e) {
            System.err.println("Error cancelling appointment: " + e.getMessage());
            notifyComponents("CANCELLATION_ERROR", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean rescheduleAppointment(Long appointmentId, LocalDateTime newTime, String newLocation) {
        try {
            Appointment appointment = appointmentDAO.getAppointmentById(appointmentId);
            if (appointment == null) {
                return false;
            }

            // Check availability for new time and location
            if (!checkStaffAvailability(appointment.getStaffId(), newTime)) {
                notifyComponents("RESCHEDULING_FAILED", "Staff not available at new time");
                return false;
            }

            if (!checkRoomAvailability(newLocation, newTime)) {
                notifyComponents("RESCHEDULING_FAILED", "Room not available at new time");
                return false;
            }

            LocalDateTime oldTime = appointment.getAppointmentTime();
            String oldLocation = appointment.getLocation();

            // Update appointment
            appointment.setAppointmentTime(newTime);
            appointment.setLocation(newLocation);
            appointment.setStatus("SCHEDULED");
            boolean success = appointmentDAO.updateAppointment(appointment);

            if (success) {
                notifyComponents("APPOINTMENT_RESCHEDULED", appointment);
                appointmentSubject.notifyAppointmentUpdated(appointment, oldTime, oldLocation);
            }

            return success;

        } catch (SQLException e) {
            System.err.println("Error rescheduling appointment: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Appointment> getAvailableSlots(Long staffId, LocalDateTime date) {
        try {
            return appointmentDAO.getAppointmentsByStaffAndDate(staffId, date.toLocalDate());
        } catch (SQLException e) {
            System.err.println("Error getting available slots: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public boolean checkRoomAvailability(String location, LocalDateTime time) {
        try {
            // Check if room is available at the specified time (30-min slots)
            LocalDateTime startTime = time.minusMinutes(15);
            LocalDateTime endTime = time.plusMinutes(15);

            List<Appointment> conflictingAppointments = appointmentDAO.getAppointmentsByLocationAndTimeRange(
                    location, startTime, endTime);

            return conflictingAppointments.isEmpty();

        } catch (SQLException e) {
            System.err.println("Error checking room availability: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean checkStaffAvailability(Long staffId, LocalDateTime time) {
        try {
            // Check if staff member is available at the specified time (30-min slots)
            LocalDateTime startTime = time.minusMinutes(15);
            LocalDateTime endTime = time.plusMinutes(15);

            List<Appointment> conflictingAppointments = appointmentDAO.getAppointmentsByStaffAndTimeRange(
                    staffId, startTime, endTime);

            return conflictingAppointments.isEmpty();

        } catch (SQLException e) {
            System.err.println("Error checking staff availability: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void registerComponent(AppointmentComponent component) {
        components.add(component);
    }

    @Override
    public void removeComponent(AppointmentComponent component) {
        components.remove(component);
    }

    private void notifyComponents(String event, Object data) {
        for (AppointmentComponent component : components) {
            component.notify(event, data);
        }
    }
}
