/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.memento;

import com.globemed.models.Patient;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 *
 * @author Hansana
 */
public class PatientMemento {

    private final Long id;
    private final String name;
    private final LocalDate dob;
    private final String address;
    private final String phone;
    private final String medicalHistory;
    private final LocalDateTime snapshotTime;
    private final String changeReason;

    public PatientMemento(Patient patient, String changeReason) {
        this.id = patient.getId();
        this.name = patient.getName();
        this.dob = patient.getDob();
        this.address = patient.getAddress();
        this.phone = patient.getPhone();
        this.medicalHistory = patient.getMedicalHistory();
        this.snapshotTime = LocalDateTime.now();
        this.changeReason = changeReason;
    }

    // Package-private getters for originator access
    Long getId() {
        return id;
    }

    String getName() {
        return name;
    }

    LocalDate getDob() {
        return dob;
    }

    String getAddress() {
        return address;
    }

    String getPhone() {
        return phone;
    }

    String getMedicalHistory() {
        return medicalHistory;
    }

    // Public getters for metadata
    public LocalDateTime getSnapshotTime() {
        return snapshotTime;
    }

    public String getChangeReason() {
        return changeReason;
    }

    @Override
    public String toString() {
        return "PatientMemento{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", snapshotTime=" + snapshotTime
                + ", changeReason='" + changeReason + '\''
                + '}';
    }
}
