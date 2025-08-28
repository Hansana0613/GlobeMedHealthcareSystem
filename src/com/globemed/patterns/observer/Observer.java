/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.globemed.patterns.observer;

import com.globemed.models.Appointment;
import java.time.LocalDateTime;

/**
 *
 * @author Hansana
 */
public interface Observer {

    void onAppointmentCreated(Appointment appointment);

    void onAppointmentUpdated(Appointment appointment, LocalDateTime oldTime, String oldLocation);

    void onAppointmentCancelled(Appointment appointment);
}
