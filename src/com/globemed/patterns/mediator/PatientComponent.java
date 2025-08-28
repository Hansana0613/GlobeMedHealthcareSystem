/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.mediator;

import com.globemed.models.Patient;
import com.globemed.models.Appointment;

/**
 *
 * @author Hansana
 */
public class PatientComponent extends AppointmentComponent {

    private Patient patient;

    public PatientComponent(AppointmentMediator mediator, Patient patient) {
        super(mediator, "PATIENT_" + patient.getId());
        this.patient = patient;
        mediator.registerComponent(this);
    }

    public boolean requestAppointment(Long staffId, java.time.LocalDateTime appointmentTime, String location) {
        System.out.println("Patient " + patient.getName() + " requesting appointment");
        return mediator.scheduleAppointment(patient.getId(), staffId, appointmentTime, location);
    }

    public boolean cancelAppointment(Long appointmentId) {
        System.out.println("Patient " + patient.getName() + " cancelling appointment");
        return mediator.cancelAppointment(appointmentId);
    }

    @Override
    public void notify(String event, Object data) {
        switch (event) {
            case "APPOINTMENT_SCHEDULED":
                Appointment appointment = (Appointment) data;
                if (appointment.getPatientId().equals(patient.getId())) {
                    System.out.println("Patient " + patient.getName()
                            + " notified: Appointment cancelled for " + appointment.getAppointmentTime());
                }
                break;
            case "APPOINTMENT_CANCELLED":
                appointment = (Appointment) data;
                if (appointment.getPatientId().equals(patient.getId())) {
                    System.out.println("Patient " + patient.getName()
                            + " notified: Appointment scheduled for " + appointment.getAppointmentTime());
                }
                break;
            case "APPOINTMENT_RESCHEDULED":
                appointment = (Appointment) data;
                if (appointment.getPatientId().equals(patient.getId())) {
                    System.out.println("Patient " + patient.getName()
                            + " notified: Appointment rescheduled to " + appointment.getAppointmentTime());
                }
                break;
        }
    }

    public Patient getPatient() {
        return patient;
    }
}
