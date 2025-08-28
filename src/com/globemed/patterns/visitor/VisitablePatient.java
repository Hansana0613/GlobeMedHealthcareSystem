/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.visitor;

import com.globemed.models.Patient;

/**
 *
 * @author Hansana
 */
public class VisitablePatient implements Visitable {

    private Patient patient;

    public VisitablePatient(Patient patient) {
        this.patient = patient;
    }

    @Override
    public void accept(ReportVisitor visitor) {
        visitor.visitPatient(patient);
    }

    public Patient getPatient() {
        return patient;
    }
}
