/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.globemed.patterns.decorator;

import com.globemed.models.Patient;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Hansana
 */
public interface PatientRecordService {

    Patient getPatientById(Long id) throws SQLException;

    List<Patient> getAllPatients() throws SQLException;

    Long createPatient(Patient patient) throws SQLException;

    boolean updatePatient(Patient patient) throws SQLException;

    boolean deletePatient(Long id) throws SQLException;
}
