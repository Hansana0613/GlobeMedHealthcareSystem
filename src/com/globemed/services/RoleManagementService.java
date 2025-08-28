/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.services;

import com.globemed.patterns.composite.*;
import com.globemed.patterns.bridge.*;
import com.globemed.models.Staff;
import java.util.*;

/**
 *
 * @author Hansana
 */
public class RoleManagementService {

    private Map<String, RoleComponent> roleHierarchy;
    private PermissionImplementor defaultPermissionImpl;
    private Map<String, RoleAbstraction> bridgeRoles;

    public RoleManagementService(PermissionImplementor permissionImpl) {
        this.roleHierarchy = new HashMap<>();
        this.defaultPermissionImpl = permissionImpl;
        this.bridgeRoles = new HashMap<>();
        initializeDefaultRoles();
    }

    private void initializeDefaultRoles() {
        // Create hierarchical role structure

        // Level 1: Top-level roles
        CompositeRole adminRole = new CompositeRole("Administrator",
                "System administrator with full access", defaultPermissionImpl, 1);

        CompositeRole medicalStaffRole = new CompositeRole("Medical Staff",
                "General medical staff category", defaultPermissionImpl, 1);

        // Level 2: Mid-level roles
        CompositeRole doctorRole = new CompositeRole("Doctor",
                "Medical doctor with diagnostic and treatment permissions", defaultPermissionImpl, 2);

        SimpleRole nurseRole = new SimpleRole("Nurse",
                "Nursing staff with patient care permissions", defaultPermissionImpl, 3);

        SimpleRole pharmacistRole = new SimpleRole("Pharmacist",
                "Pharmacy staff with medication management permissions", defaultPermissionImpl, 3);

        // Level 3: Specialized roles
        SimpleRole surgeonRole = new SimpleRole("Surgeon",
                "Specialized surgeon with surgical permissions", defaultPermissionImpl, 4);

        SimpleRole specialistRole = new SimpleRole("Specialist",
                "Medical specialist with advanced diagnostic permissions", defaultPermissionImpl, 4);

        // Build hierarchy
        medicalStaffRole.addSubRole(doctorRole);
        medicalStaffRole.addSubRole(nurseRole);
        medicalStaffRole.addSubRole(pharmacistRole);

        doctorRole.addSubRole(surgeonRole);
        doctorRole.addSubRole(specialistRole);

        // Add direct permissions to roles
        setupRolePermissions(adminRole, doctorRole, nurseRole, pharmacistRole, surgeonRole, specialistRole);

        // Store in hierarchy
        roleHierarchy.put("Administrator", adminRole);
        roleHierarchy.put("Medical Staff", medicalStaffRole);
        roleHierarchy.put("Doctor", doctorRole);
        roleHierarchy.put("Nurse", nurseRole);
        roleHierarchy.put("Pharmacist", pharmacistRole);
        roleHierarchy.put("Surgeon", surgeonRole);
        roleHierarchy.put("Specialist", specialistRole);

        // Create bridge roles for flexible permission management
        createBridgeRoles();

        System.out.println("Default role hierarchy initialized with " + roleHierarchy.size() + " roles");
    }

    private void setupRolePermissions(RoleComponent... roles) {
        // Administrator permissions
        if (roles[0] instanceof CompositeRole) {
            CompositeRole adminRole = (CompositeRole) roles[0];
            adminRole.addDirectPermission("VIEW_PATIENT_RECORDS");
            adminRole.addDirectPermission("EDIT_PATIENT_RECORDS");
            adminRole.addDirectPermission("SCHEDULE_APPOINTMENTS");
            adminRole.addDirectPermission("PROCESS_BILLS");
            adminRole.addDirectPermission("GENERATE_REPORTS");
            adminRole.addDirectPermission("MANAGE_USERS");
            adminRole.addDirectPermission("SYSTEM_ADMIN");
        }

        // Doctor permissions
        if (roles[1] instanceof CompositeRole) {
            CompositeRole doctorRole = (CompositeRole) roles[1];
            doctorRole.addDirectPermission("VIEW_PATIENT_RECORDS");
            doctorRole.addDirectPermission("EDIT_PATIENT_RECORDS");
            doctorRole.addDirectPermission("SCHEDULE_APPOINTMENTS");
            doctorRole.addDirectPermission("GENERATE_REPORTS");
            doctorRole.addDirectPermission("PRESCRIBE_MEDICATION");
        }

        // Nurse permissions
        if (roles[2] instanceof SimpleRole) {
            SimpleRole nurseRole = (SimpleRole) roles[2];
            nurseRole.addDirectPermission("VIEW_PATIENT_RECORDS");
            nurseRole.addDirectPermission("SCHEDULE_APPOINTMENTS");
            nurseRole.addDirectPermission("RECORD_VITALS");
        }

        // Pharmacist permissions
        if (roles[3] instanceof SimpleRole) {
            SimpleRole pharmacistRole = (SimpleRole) roles[3];
            pharmacistRole.addDirectPermission("VIEW_PRESCRIPTIONS");
            pharmacistRole.addDirectPermission("DISPENSE_MEDICATION");
            pharmacistRole.addDirectPermission("PROCESS_BILLS");
        }

        // Surgeon permissions (inherits from Doctor)
        if (roles[4] instanceof SimpleRole) {
            SimpleRole surgeonRole = (SimpleRole) roles[4];
            surgeonRole.addDirectPermission("SCHEDULE_SURGERY");
            surgeonRole.addDirectPermission("ACCESS_OR_SCHEDULE");
        }

        // Specialist permissions (inherits from Doctor)
        if (roles[5] instanceof SimpleRole) {
            SimpleRole specialistRole = (SimpleRole) roles[5];
            specialistRole.addDirectPermission("ADVANCED_DIAGNOSTICS");
            specialistRole.addDirectPermission("SPECIALIST_CONSULTATION");
        }
    }

    private void createBridgeRoles() {
        for (String roleName : roleHierarchy.keySet()) {
            RoleComponent roleComponent = roleHierarchy.get(roleName);
            ConcreteRole bridgeRole = new ConcreteRole(roleName,
                    roleComponent.getDescription(), defaultPermissionImpl);
            bridgeRoles.put(roleName, bridgeRole);
        }
    }

    // Public methods for role management
    public boolean checkPermission(Staff staff, String permission) {
        try {
            RoleComponent role = getRoleById(staff.getRoleId());
            if (role != null) {
                return role.hasPermission(permission);
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error checking permission: " + e.getMessage());
            return false;
        }
    }

    public Set<String> getStaffPermissions(Staff staff) {
        RoleComponent role = getRoleById(staff.getRoleId());
        if (role != null) {
            return role.getAllPermissions();
        }
        return new HashSet<>();
    }

    public RoleComponent getRoleByName(String roleName) {
        return roleHierarchy.get(roleName);
    }

    public RoleComponent getRoleById(Long roleId) {
        // In a real system, you'd query the database by ID
        // For this example, we'll use a simple mapping
        switch (roleId.intValue()) {
            case 1:
                return roleHierarchy.get("Administrator");
            case 2:
                return roleHierarchy.get("Doctor");
            case 3:
                return roleHierarchy.get("Nurse");
            case 4:
                return roleHierarchy.get("Pharmacist");
            case 5:
                return roleHierarchy.get("Surgeon");
            case 6:
                return roleHierarchy.get("Specialist");
            default:
                return null;
        }
    }

    public void displayRoleHierarchy() {
        System.out.println("\n=== ROLE HIERARCHY ===");
        for (RoleComponent role : roleHierarchy.values()) {
            if (role.getRoleLevel() == 1) { // Only display top-level roles
                role.display("");
                System.out.println();
            }
        }
    }

    public void addCustomRole(String roleName, String description, String parentRoleName, int level) {
        RoleComponent parentRole = roleHierarchy.get(parentRoleName);
        if (parentRole != null && parentRole.isComposite()) {
            CompositeRole parent = (CompositeRole) parentRole;
            SimpleRole newRole = new SimpleRole(roleName, description, defaultPermissionImpl, level);
            parent.addSubRole(newRole);
            roleHierarchy.put(roleName, newRole);

            // Create bridge role
            ConcreteRole bridgeRole = new ConcreteRole(roleName, description, defaultPermissionImpl);
            bridgeRoles.put(roleName, bridgeRole);

            System.out.println("Added custom role: " + roleName + " under " + parentRoleName);
        } else {
            System.out.println("Cannot add role: Parent role not found or not composite");
        }
    }

    public void addPermissionToRole(String roleName, String permission) {
        RoleAbstraction bridgeRole = bridgeRoles.get(roleName);
        if (bridgeRole != null) {
            bridgeRole.addPermission(permission);
            System.out.println("Added permission '" + permission + "' to role '" + roleName + "'");
        } else {
            System.out.println("Role not found: " + roleName);
        }
    }

    public void removePermissionFromRole(String roleName, String permission) {
        RoleAbstraction bridgeRole = bridgeRoles.get(roleName);
        if (bridgeRole != null) {
            bridgeRole.removePermission(permission);
            System.out.println("Removed permission '" + permission + "' from role '" + roleName + "'");
        } else {
            System.out.println("Role not found: " + roleName);
        }
    }

    public void switchPermissionBackend(String roleName, PermissionImplementor newImplementor) {
        ConcreteRole bridgeRole = (ConcreteRole) bridgeRoles.get(roleName);
        if (bridgeRole != null) {
            bridgeRole.migrateTo(newImplementor);
        } else {
            System.out.println("Role not found: " + roleName);
        }
    }

    public List<String> getAllRoleNames() {
        return new ArrayList<>(roleHierarchy.keySet());
    }

    public Map<String, Set<String>> getRolePermissionMatrix() {
        Map<String, Set<String>> matrix = new HashMap<>();
        for (Map.Entry<String, RoleComponent> entry : roleHierarchy.entrySet()) {
            matrix.put(entry.getKey(), entry.getValue().getAllPermissions());
        }
        return matrix;
    }
}
