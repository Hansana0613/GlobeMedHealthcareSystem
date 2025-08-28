/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.visitor;

import com.globemed.models.Appointment;

/**
 *
 * @author Hansana
 */
public class VisitableAppointment implements Visitable {

    private Appointment appointment;

    public VisitableAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    @Override
    public void accept(ReportVisitor visitor) {
        visitor.visitAppointment(appointment);
    }

    public Appointment getAppointment() {
        return appointment;
    }
}
