/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.observer;

import com.globemed.models.Appointment;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Hansana
 */
public class AppointmentSubject implements Subject {

    private List<Observer> observers;
    private String lastEvent;
    private Appointment lastAppointment;
    private LocalDateTime lastOldTime;
    private String lastOldLocation;

    public AppointmentSubject() {
        this.observers = new ArrayList<>();
    }

    @Override
    public void attach(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void detach(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            switch (lastEvent) {
                case "CREATED":
                    observer.onAppointmentCreated(lastAppointment);
                    break;
                case "UPDATED":
                    observer.onAppointmentUpdated(lastAppointment, lastOldTime, lastOldLocation);
                    break;
                case "CANCELLED":
                    observer.onAppointmentCancelled(lastAppointment);
                    break;
            }
        }
    }

    public void notifyAppointmentCreated(Appointment appointment) {
        this.lastEvent = "CREATED";
        this.lastAppointment = appointment;
        notifyObservers();
    }

    public void notifyAppointmentUpdated(Appointment appointment, LocalDateTime oldTime, String oldLocation) {
        this.lastEvent = "UPDATED";
        this.lastAppointment = appointment;
        this.lastOldTime = oldTime;
        this.lastOldLocation = oldLocation;
        notifyObservers();
    }

    public void notifyAppointmentCancelled(Appointment appointment) {
        this.lastEvent = "CANCELLED";
        this.lastAppointment = appointment;
        notifyObservers();
    }
}
