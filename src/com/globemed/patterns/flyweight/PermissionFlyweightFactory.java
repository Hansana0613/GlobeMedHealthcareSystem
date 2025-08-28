/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.flyweight;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Hansana
 */
public class PermissionFlyweightFactory {

    private static PermissionFlyweightFactory instance;
    private Map<String, PermissionFlyweight> permissions;

    private PermissionFlyweightFactory() {
        this.permissions = new HashMap<>();
        initializeDefaultPermissions();
    }

    public static synchronized PermissionFlyweightFactory getInstance() {
        if (instance == null) {
            instance = new PermissionFlyweightFactory();
        }
        return instance;
    }

    public PermissionFlyweight getPermission(String permissionName) {
        PermissionFlyweight permission = permissions.get(permissionName);

        if (permission == null) {
            // Create default permission if not found
            permission = createDefaultPermission(permissionName);
            permissions.put(permissionName, permission);
        }

        return permission;
    }

    public void addPermission(String name, String description, Set<String> roles, int securityLevel) {
        if (!permissions.containsKey(name)) {
            ConcretePermissionFlyweight permission = new ConcretePermissionFlyweight(
                    name, description, roles, securityLevel);
            permissions.put(name, permission);
            System.out.println("Added new permission flyweight: " + name);
        }
    }

    public int getCreatedPermissionsCount() {
        return permissions.size();
    }

    public void printStatistics() {
        System.out.println("Permission Flyweight Statistics:");
        System.out.println("Total permissions created: " + permissions.size());
        System.out.println("Memory saved by sharing: Significant for " + permissions.size() + " permissions");

        for (PermissionFlyweight permission : permissions.values()) {
            System.out.println("  - " + permission.getPermissionName()
                    + " (valid for " + ((ConcretePermissionFlyweight) permission).getValidRoles().size() + " roles)");
        }
    }

    private void initializeDefaultPermissions() {
        // Initialize commonly used permissions
        addPermission("VIEW_PATIENT_RECORDS", "View patient information",
                Set.of("Doctor", "Nurse", "Administrator"), 2);

        addPermission("EDIT_PATIENT_RECORDS", "Edit patient information",
                Set.of("Doctor", "Administrator"), 3);

        addPermission("SCHEDULE_APPOINTMENTS", "Schedule patient appointments",
                Set.of("Doctor", "Nurse", "Administrator"), 2);

        addPermission("PROCESS_BILLS", "Process billing and payments",
                Set.of("Administrator", "Pharmacist"), 3);

        addPermission("GENERATE_REPORTS", "Generate medical reports",
                Set.of("Doctor", "Administrator"), 3);

        addPermission("SYSTEM_ADMIN", "System administration",
                Set.of("Administrator"), 5);

        addPermission("PRESCRIBE_MEDICATION", "Prescribe medications",
                Set.of("Doctor"), 4);

        addPermission("DISPENSE_MEDICATION", "Dispense medications",
                Set.of("Pharmacist"), 3);
    }

    private PermissionFlyweight createDefaultPermission(String permissionName) {
        return new ConcretePermissionFlyweight(
                permissionName,
                "Auto-generated permission: " + permissionName,
                Set.of("Administrator"), // Default to admin only
                1 // Low security level for auto-generated
        );
    }
}
