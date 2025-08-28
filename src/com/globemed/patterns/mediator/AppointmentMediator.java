/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.globemed.patterns.mediator;

import com.globemed.models.Appointment;
import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author Hansana
 */
public interface AppointmentMediator {

    boolean scheduleAppointment(Long patientId, Long staffId, LocalDateTime appointmentTime, String location);

    boolean cancelAppointment(Long appointmentId);

    boolean rescheduleAppointment(Long appointmentId, LocalDateTime newTime, String newLocation);

    List<Appointment> getAvailableSlots(Long staffId, LocalDateTime date);

    boolean checkRoomAvailability(String location, LocalDateTime time);

    boolean checkStaffAvailability(Long staffId, LocalDateTime time);

    void registerComponent(AppointmentComponent component);

    void removeComponent(AppointmentComponent component);
}
