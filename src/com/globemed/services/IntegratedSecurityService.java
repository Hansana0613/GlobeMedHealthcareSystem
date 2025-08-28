/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.services;

import com.globemed.patterns.decorator.*;
import com.globemed.patterns.memento.*;
import com.globemed.patterns.flyweight.*;
import com.globemed.models.Patient;
import java.util.Set;

/**
 *
 * @author Hansana
 */
public class IntegratedSecurityService {

    private VersionControlService versionControl;
    private PermissionFlyweightFactory permissionFactory;

    public IntegratedSecurityService() {
        this.versionControl = new VersionControlService(10); // Keep 10 versions
        this.permissionFactory = PermissionFlyweightFactory.getInstance();
    }

    public <T> SecureService<T> createSecureService(String serviceName, String requiredPermission,
            Set<String> userPermissions) {
        // Build security layers using decorator pattern
        SecureService<T> service = new BasicSecureService<>(serviceName);

        // Add authentication layer
        service = new AuthenticationDecorator<>(service, 30); // 30 min timeout

        // Add encryption layer
        service = new EncryptionDecorator<>(service, "default-key");

        // Add access control layer (using flyweight permissions)
        service = new AccessControlDecorator<>(service, requiredPermission, userPermissions);

        // Add audit logging layer
        service = new AuditLoggingDecorator<>(service, true); // Detailed logging

        return service;
    }

    public void savePatientSecurely(Patient patient, String changeReason) {
        // Use memento pattern to save version
        versionControl.savePatientVersion(patient, changeReason);
        System.out.println("Patient data saved with version control: " + changeReason);
    }

    public boolean rollbackPatient(Long patientId, int versionIndex) {
        return versionControl.restorePatientVersion(patientId, versionIndex);
    }

    public void demonstrateSecurityLayers() {
        System.out.println("\n=== SECURITY DEMONSTRATION ===");

        // Create a secure service for patient data access
        Set<String> doctorPermissions = Set.of("VIEW_PATIENT_RECORDS", "EDIT_PATIENT_RECORDS");
        SecureService<String> securePatientService = createSecureService(
                "PatientDataService",
                "VIEW_PATIENT_RECORDS",
                doctorPermissions
        );

        try {
            // Execute secure operation
            String result = securePatientService.execute("Patient data request");
            System.out.println("Secure operation completed: " + result);

        } catch (SecurityException e) {
            System.out.println("Security exception: " + e.getMessage());
        }

        // Demonstrate flyweight efficiency
        permissionFactory.printStatistics();

        // Demonstrate version control
        Patient testPatient = new Patient();
        testPatient.setId(1L);
        testPatient.setName("John Doe");
        testPatient.setMedicalHistory("Initial history");

        savePatientSecurely(testPatient, "Initial record creation");

        testPatient.setMedicalHistory("Updated with new diagnosis");
        savePatientSecurely(testPatient, "Added diagnosis");

        testPatient.setMedicalHistory("Treatment plan added");
        savePatientSecurely(testPatient, "Treatment planning");

        System.out.println("Patient versions saved: "
                + versionControl.getPatientHistory(1L).size());
    }
}
