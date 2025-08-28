/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.decorator;

import com.globemed.models.Patient;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Hansana
 */
public abstract class PatientRecordServiceDecorator implements PatientRecordService {

    protected PatientRecordService wrappedService;

    public PatientRecordServiceDecorator(PatientRecordService service) {
        this.wrappedService = service;
    }

    @Override
    public Patient getPatientById(Long id) throws SQLException {
        return wrappedService.getPatientById(id);
    }

    @Override
    public List<Patient> getAllPatients() throws SQLException {
        return wrappedService.getAllPatients();
    }

    @Override
    public Long createPatient(Patient patient) throws SQLException {
        return wrappedService.createPatient(patient);
    }

    @Override
    public boolean updatePatient(Patient patient) throws SQLException {
        return wrappedService.updatePatient(patient);
    }

    @Override
    public boolean deletePatient(Long id) throws SQLException {
        return wrappedService.deletePatient(id);
    }
}
