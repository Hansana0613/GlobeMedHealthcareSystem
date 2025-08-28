/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.services;

import com.globemed.patterns.chainofresponsibility.*;
import com.globemed.patterns.decorator.*;
import com.globemed.models.Patient;
import com.globemed.models.Staff;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Hansana
 */
public class SecurePatientService {

    private AccessHandler accessChain;
    private PatientRecordService secureService;
    private Staff currentUser;

    public SecurePatientService(Staff currentUser) {
        this.currentUser = currentUser;
        initializeAccessChain();
        initializeSecureService();
    }

    private void initializeAccessChain() {
        // Build the chain: Authentication -> Role Check -> Logging
        AuthenticationHandler authHandler = new AuthenticationHandler();
        RoleCheckHandler roleHandler = new RoleCheckHandler();
        LoggingHandler logHandler = new LoggingHandler();

        authHandler.setNext(roleHandler);
        roleHandler.setNext(logHandler);

        this.accessChain = authHandler;
    }

    private void initializeSecureService() {
        // Build the decorator chain: Basic -> Encryption -> Audit
        PatientRecordService basicService = new BasicPatientRecordService();
        PatientRecordService encryptedService = new PatientEncryptionDecorator(basicService);
        PatientRecordService auditedService = new AuditDecorator(encryptedService, currentUser.getUsername());

        this.secureService = auditedService;
    }

    public Patient getPatientById(Long patientId) throws SQLException, SecurityException {
        // First check access through chain of responsibility
        AccessRequest request = new AccessRequest(currentUser, "VIEW", patientId, "127.0.0.1");
        AccessResult accessResult = accessChain.handle(request);

        if (!accessResult.isGranted()) {
            throw new SecurityException("Access denied: " + accessResult.getMessage());
        }

        // If access granted, use decorated service
        return secureService.getPatientById(patientId);
    }

    public List<Patient> getAllPatients() throws SQLException, SecurityException {
        // Check access for viewing all patients
        AccessRequest request = new AccessRequest(currentUser, "VIEW", null, "127.0.0.1");
        AccessResult accessResult = accessChain.handle(request);

        if (!accessResult.isGranted()) {
            throw new SecurityException("Access denied: " + accessResult.getMessage());
        }

        return secureService.getAllPatients();
    }

    public Long createPatient(Patient patient) throws SQLException, SecurityException {
        AccessRequest request = new AccessRequest(currentUser, "CREATE", null, "127.0.0.1");
        AccessResult accessResult = accessChain.handle(request);

        if (!accessResult.isGranted()) {
            throw new SecurityException("Access denied: " + accessResult.getMessage());
        }

        return secureService.createPatient(patient);
    }

    public boolean updatePatient(Patient patient) throws SQLException, SecurityException {
        AccessRequest request = new AccessRequest(currentUser, "EDIT", patient.getId(), "127.0.0.1");
        AccessResult accessResult = accessChain.handle(request);

        if (!accessResult.isGranted()) {
            throw new SecurityException("Access denied: " + accessResult.getMessage());
        }

        return secureService.updatePatient(patient);
    }

    public boolean deletePatient(Long patientId) throws SQLException, SecurityException {
        AccessRequest request = new AccessRequest(currentUser, "DELETE", patientId, "127.0.0.1");
        AccessResult accessResult = accessChain.handle(request);

        if (!accessResult.isGranted()) {
            throw new SecurityException("Access denied: " + accessResult.getMessage());
        }

        return secureService.deletePatient(patientId);
    }
}
