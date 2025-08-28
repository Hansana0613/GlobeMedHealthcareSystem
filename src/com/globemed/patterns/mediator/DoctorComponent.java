/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.mediator;

import com.globemed.models.Staff;
import com.globemed.models.Appointment;
import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author Hansana
 */
public class DoctorComponent extends AppointmentComponent {

    private Staff doctor;

    public DoctorComponent(AppointmentMediator mediator, Staff doctor) {
        super(mediator, "DOCTOR_" + doctor.getId());
        this.doctor = doctor;
        mediator.registerComponent(this);
    }

    public List<Appointment> getMySchedule(LocalDateTime date) {
        System.out.println("Doctor " + doctor.getName() + " checking schedule");
        return mediator.getAvailableSlots(doctor.getId(), date);
    }

    public boolean rescheduleAppointment(Long appointmentId, LocalDateTime newTime, String newLocation) {
        System.out.println("Doctor " + doctor.getName() + " rescheduling appointment");
        return mediator.rescheduleAppointment(appointmentId, newTime, newLocation);
    }

    @Override
    public void notify(String event, Object data) {
        switch (event) {
            case "APPOINTMENT_SCHEDULED":
                Appointment appointment = (Appointment) data;
                if (appointment.getStaffId().equals(doctor.getId())) {
                    System.out.println("Doctor " + doctor.getName()
                            + " notified: New appointment scheduled for " + appointment.getAppointmentTime());
                }
                break;
            case "APPOINTMENT_CANCELLED":
                appointment = (Appointment) data;
                if (appointment.getStaffId().equals(doctor.getId())) {
                    System.out.println("Doctor " + doctor.getName()
                            + " notified: Appointment cancelled for " + appointment.getAppointmentTime());
                }
                break;
            case "APPOINTMENT_RESCHEDULED":
                appointment = (Appointment) data;
                if (appointment.getStaffId().equals(doctor.getId())) {
                    System.out.println("Doctor " + doctor.getName()
                            + " notified: Appointment rescheduled to " + appointment.getAppointmentTime());
                }
                break;
        }
    }

    public Staff getDoctor() {
        return doctor;
    }
}
