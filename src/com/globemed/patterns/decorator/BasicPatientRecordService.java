/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.globemed.patterns.decorator;

import com.globemed.database.PatientDAO;
import com.globemed.models.Patient;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Hansana
 */
public class BasicPatientRecordService implements PatientRecordService {

    private PatientDAO patientDAO;

    public BasicPatientRecordService() {
        this.patientDAO = new PatientDAO();
    }

    @Override
    public Patient getPatientById(Long id) throws SQLException {
        return patientDAO.getPatientById(id);
    }

    @Override
    public List<Patient> getAllPatients() throws SQLException {
        return patientDAO.getAllPatients();
    }

    @Override
    public Long createPatient(Patient patient) throws SQLException {
        return patientDAO.insertPatient(patient);
    }

    @Override
    public boolean updatePatient(Patient patient) throws SQLException {
        return patientDAO.updatePatient(patient);
    }

    @Override
    public boolean deletePatient(Long id) throws SQLException {
        return patientDAO.deletePatient(id);
    }
}
