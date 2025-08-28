/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.memento;

import com.globemed.patterns.memento.*;
import com.globemed.models.Patient;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Hansana
 */
public class VersionControlService {

    private Map<Long, PatientCaretaker> patientCaretakers;
    private int maxVersionsPerPatient;

    public VersionControlService(int maxVersionsPerPatient) {
        this.patientCaretakers = new HashMap<>();
        this.maxVersionsPerPatient = maxVersionsPerPatient;
    }

    public void savePatientVersion(Patient patient, String changeReason) {
        PatientCaretaker caretaker = patientCaretakers.computeIfAbsent(
                patient.getId(),
                k -> new PatientCaretaker(maxVersionsPerPatient)
        );

        PatientOriginator originator = new PatientOriginator(patient);
        PatientMemento memento = originator.createMemento(changeReason);
        caretaker.saveMemento(memento);
    }

    public boolean restorePatientVersion(Long patientId, int versionIndex) {
        PatientCaretaker caretaker = patientCaretakers.get(patientId);
        if (caretaker == null) {
            System.out.println("No version history found for patient ID: " + patientId);
            return false;
        }

        PatientMemento memento = caretaker.getMemento(versionIndex);
        if (memento == null) {
            System.out.println("Version index " + versionIndex + " not found for patient ID: " + patientId);
            return false;
        }

        // In real implementation, you would retrieve current patient from database
        // For demo, we'll create a new patient object
        Patient patient = new Patient();
        PatientOriginator originator = new PatientOriginator(patient);
        originator.restoreFromMemento(memento);

        System.out.println("Restored patient ID " + patientId + " to version: " + memento.getChangeReason());
        return true;
    }

    public List<PatientMemento> getPatientHistory(Long patientId) {
        PatientCaretaker caretaker = patientCaretakers.get(patientId);
        return caretaker != null ? caretaker.getAllMementos() : List.of();
    }

    public List<PatientMemento> getRecentChanges(Long patientId, LocalDateTime since) {
        PatientCaretaker caretaker = patientCaretakers.get(patientId);
        return caretaker != null ? caretaker.getMementosSince(since) : List.of();
    }
}
