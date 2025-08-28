/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.memento;

import com.globemed.models.Patient;

/**
 *
 * @author Hansana
 */
public class PatientOriginator {

    private Patient patient;

    public PatientOriginator(Patient patient) {
        this.patient = patient;
    }

    public PatientMemento createMemento(String changeReason) {
        return new PatientMemento(patient, changeReason);
    }

    public void restoreFromMemento(PatientMemento memento) {
        patient.setId(memento.getId());
        patient.setName(memento.getName());
        patient.setDob(memento.getDob());
        patient.setAddress(memento.getAddress());
        patient.setPhone(memento.getPhone());
        patient.setMedicalHistory(memento.getMedicalHistory());
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
}
