/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.decorator;

import com.globemed.models.Patient;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 *
 * @author Hansana
 */
public class AuditDecorator extends PatientRecordServiceDecorator {

    private String currentUser;

    public AuditDecorator(PatientRecordService service, String currentUser) {
        super(service);
        this.currentUser = currentUser;
    }

    @Override
    public Patient getPatientById(Long id) throws SQLException {
        logAudit("READ", "Patient ID: " + id, "Single patient record accessed");
        return super.getPatientById(id);
    }

    @Override
    public List<Patient> getAllPatients() throws SQLException {
        List<Patient> patients = super.getAllPatients();
        logAudit("READ", "All Patients", "Retrieved " + patients.size() + " patient records");
        return patients;
    }

    @Override
    public Long createPatient(Patient patient) throws SQLException {
        Long id = super.createPatient(patient);
        logAudit("CREATE", "Patient: " + patient.getName(), "New patient record created with ID: " + id);
        return id;
    }

    @Override
    public boolean updatePatient(Patient patient) throws SQLException {
        boolean result = super.updatePatient(patient);
        logAudit("UPDATE", "Patient ID: " + patient.getId(), "Patient record updated: " + patient.getName());
        return result;
    }

    @Override
    public boolean deletePatient(Long id) throws SQLException {
        boolean result = super.deletePatient(id);
        logAudit("DELETE", "Patient ID: " + id, "Patient record deleted");
        return result;
    }

    private void logAudit(String action, String target, String details) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String auditEntry = String.format("[%s] AUDIT: User=%s, Action=%s, Target=%s, Details=%s",
                timestamp, currentUser, action, target, details);
        System.out.println(auditEntry);

        // In a real implementation, this would write to an audit database table
        // For now, we'll just log to console
    }
}
