/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.mediator;

import com.globemed.models.Appointment;
import java.time.LocalDateTime;

/**
 *
 * @author Hansana
 */
public class RoomComponent extends AppointmentComponent {

    private String roomName;
    private String roomType;
    private int capacity;

    public RoomComponent(AppointmentMediator mediator, String roomName, String roomType, int capacity) {
        super(mediator, "ROOM_" + roomName);
        this.roomName = roomName;
        this.roomType = roomType;
        this.capacity = capacity;
        mediator.registerComponent(this);
    }

    public boolean isAvailable(LocalDateTime time) {
        return mediator.checkRoomAvailability(roomName, time);
    }

    @Override
    public void notify(String event, Object data) {
        switch (event) {
            case "APPOINTMENT_SCHEDULED":
                Appointment appointment = (Appointment) data;
                if (appointment.getLocation().equals(roomName)) {
                    System.out.println("Room " + roomName
                            + " reserved for appointment at " + appointment.getAppointmentTime());
                }
                break;
            case "APPOINTMENT_CANCELLED":
                appointment = (Appointment) data;
                if (appointment.getLocation().equals(roomName)) {
                    System.out.println("Room " + roomName
                            + " released from cancelled appointment at " + appointment.getAppointmentTime());
                }
                break;
            case "APPOINTMENT_RESCHEDULED":
                appointment = (Appointment) data;
                if (appointment.getLocation().equals(roomName)) {
                    System.out.println("Room " + roomName
                            + " updated for rescheduled appointment at " + appointment.getAppointmentTime());
                }
                break;
        }
    }

    // Getters
    public String getRoomName() {
        return roomName;
    }

    public String getRoomType() {
        return roomType;
    }

    public int getCapacity() {
        return capacity;
    }
}
