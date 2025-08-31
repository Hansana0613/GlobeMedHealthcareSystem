/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.services;

import com.globemed.patterns.mediator.*;
import com.globemed.patterns.observer.*;
import com.globemed.models.Appointment;
import com.globemed.models.Patient;
import com.globemed.models.Staff;
import java.time.LocalDateTime;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Hansana
 */
public class AppointmentService {

    private ConcreteAppointmentMediator mediator;
    private Map<Long, PatientComponent> patientComponents;
    private Map<Long, DoctorComponent> doctorComponents;
    private Map<String, RoomComponent> roomComponents;

    public AppointmentService() {
        this.mediator = new ConcreteAppointmentMediator();
        this.patientComponents = new HashMap<>();
        this.doctorComponents = new HashMap<>();
        this.roomComponents = new HashMap<>();

        // Set up default rooms
        initializeDefaultRooms();
    }

    private void initializeDefaultRooms() {
        addRoom("Clinic A", "General", 1);
        addRoom("Hospital B", "Emergency", 2);
        addRoom("Surgery Room 1", "Surgery", 1);
        addRoom("Consultation Room 1", "Consultation", 1);
        addRoom("Consultation Room 2", "Consultation", 1);
    }

    public void registerPatient(Patient patient) {
        PatientComponent patientComponent = new PatientComponent(mediator, patient);
        patientComponents.put(patient.getId(), patientComponent);

        // Set up patient observer
        PatientObserver patientObserver = new PatientObserver(patient);
        mediator.getAppointmentSubject().attach(patientObserver);

        System.out.println("Patient " + patient.getName() + " registered in appointment system");
    }

    public void registerDoctor(Staff doctor) {
        DoctorComponent doctorComponent = new DoctorComponent(mediator, doctor);
        doctorComponents.put(doctor.getId(), doctorComponent);

        // Set up doctor observer
        DoctorObserver doctorObserver = new DoctorObserver(doctor);
        mediator.getAppointmentSubject().attach(doctorObserver);

        System.out.println("Doctor " + doctor.getName() + " registered in appointment system");
    }

    public void addRoom(String roomName, String roomType, int capacity) {
        RoomComponent roomComponent = new RoomComponent(mediator, roomName, roomType, capacity);
        roomComponents.put(roomName, roomComponent);

        //System.out.println("Room " + roomName + " (" + roomType + ") added to appointment system");
    }

    public void addAdminObserver(String adminName) {
        AdminObserver adminObserver = new AdminObserver(adminName);
        mediator.getAppointmentSubject().attach(adminObserver);

        System.out.println("Admin observer " + adminName + " added to appointment system");
    }

    // Public methods for appointment operations
    public boolean scheduleAppointment(Long patientId, Long staffId, LocalDateTime appointmentTime, String location) {
        return mediator.scheduleAppointment(patientId, staffId, appointmentTime, location);
    }

    public boolean cancelAppointment(Long appointmentId) {
        return mediator.cancelAppointment(appointmentId);
    }

    public boolean rescheduleAppointment(Long appointmentId, LocalDateTime newTime, String newLocation) {
        return mediator.rescheduleAppointment(appointmentId, newTime, newLocation);
    }

    public List<Appointment> getDoctorSchedule(Long doctorId, LocalDateTime date) {
        return mediator.getAvailableSlots(doctorId, date);
    }

    public boolean checkAvailability(Long staffId, String location, LocalDateTime time) {
        return mediator.checkStaffAvailability(staffId, time)
                && mediator.checkRoomAvailability(location, time);
    }

    public List<String> getAvailableRooms() {
        return roomComponents.keySet().stream().toList();
    }

    // Patient-specific operations
    public boolean requestAppointmentForPatient(Long patientId, Long staffId, LocalDateTime appointmentTime, String location) {
        PatientComponent patientComponent = patientComponents.get(patientId);
        if (patientComponent != null) {
            return patientComponent.requestAppointment(staffId, appointmentTime, location);
        }
        return false;
    }

    // Doctor-specific operations
    public boolean doctorRescheduleAppointment(Long doctorId, Long appointmentId, LocalDateTime newTime, String newLocation) {
        DoctorComponent doctorComponent = doctorComponents.get(doctorId);
        if (doctorComponent != null) {
            return doctorComponent.rescheduleAppointment(appointmentId, newTime, newLocation);
        }
        return false;
    }

    public List<Appointment> getDoctorScheduleByComponent(Long doctorId, LocalDateTime date) {
        DoctorComponent doctorComponent = doctorComponents.get(doctorId);
        if (doctorComponent != null) {
            return doctorComponent.getMySchedule(date);
        }
        return List.of();
    }
}
