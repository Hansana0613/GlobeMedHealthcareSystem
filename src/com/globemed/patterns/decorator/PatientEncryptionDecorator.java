/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.decorator;

import com.globemed.models.Patient;
import com.globemed.utils.SecurityUtils;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Hansana
 */
public class PatientEncryptionDecorator extends PatientRecordServiceDecorator {

    public PatientEncryptionDecorator(PatientRecordService service) {
        super(service);
    }

    @Override
    public Patient getPatientById(Long id) throws SQLException {
        System.out.println("ENCRYPTION: Decrypting patient data for ID: " + id);
        Patient patient = super.getPatientById(id);
        if (patient != null) {
            // Decrypt sensitive data
            patient.setMedicalHistory(SecurityUtils.decrypt(patient.getMedicalHistory()));
            patient.setAddress(SecurityUtils.decrypt(patient.getAddress()));
        }
        return patient;
    }

    @Override
    public List<Patient> getAllPatients() throws SQLException {
        System.out.println("ENCRYPTION: Decrypting all patient data");
        List<Patient> patients = super.getAllPatients();
        for (Patient patient : patients) {
            // Decrypt sensitive data
            patient.setMedicalHistory(SecurityUtils.decrypt(patient.getMedicalHistory()));
            patient.setAddress(SecurityUtils.decrypt(patient.getAddress()));
        }
        return patients;
    }

    @Override
    public Long createPatient(Patient patient) throws SQLException {
        System.out.println("ENCRYPTION: Encrypting new patient data");
        // Create a copy to avoid modifying original
        Patient encryptedPatient = new Patient();
        encryptedPatient.setName(patient.getName());
        encryptedPatient.setDob(patient.getDob());
        encryptedPatient.setPhone(patient.getPhone());
        // Encrypt sensitive data
        encryptedPatient.setMedicalHistory(SecurityUtils.encrypt(patient.getMedicalHistory()));
        encryptedPatient.setAddress(SecurityUtils.encrypt(patient.getAddress()));

        return super.createPatient(encryptedPatient);
    }

    @Override
    public boolean updatePatient(Patient patient) throws SQLException {
        System.out.println("ENCRYPTION: Encrypting updated patient data");
        // Create a copy to avoid modifying original
        Patient encryptedPatient = new Patient();
        encryptedPatient.setId(patient.getId());
        encryptedPatient.setName(patient.getName());
        encryptedPatient.setDob(patient.getDob());
        encryptedPatient.setPhone(patient.getPhone());
        // Encrypt sensitive data
        encryptedPatient.setMedicalHistory(SecurityUtils.encrypt(patient.getMedicalHistory()));
        encryptedPatient.setAddress(SecurityUtils.encrypt(patient.getAddress()));

        return super.updatePatient(encryptedPatient);
    }
}
