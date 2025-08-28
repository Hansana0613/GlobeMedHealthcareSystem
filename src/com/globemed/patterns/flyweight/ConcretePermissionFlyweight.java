/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.flyweight;

import java.util.Set;
import java.util.HashSet;

/**
 *
 * @author Hansana
 */
public class ConcretePermissionFlyweight implements PermissionFlyweight {

    private final String permissionName;
    private final String description;
    private final Set<String> validRoles;
    private final int securityLevel;

    public ConcretePermissionFlyweight(String permissionName, String description,
            Set<String> validRoles, int securityLevel) {
        this.permissionName = permissionName;
        this.description = description;
        this.validRoles = new HashSet<>(validRoles);
        this.securityLevel = securityLevel;
    }

    @Override
    public String getPermissionName() {
        return permissionName;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void executePermissionCheck(String context, String userRole) {
        System.out.println("Checking permission '" + permissionName + "' for role '" + userRole
                + "' in context: " + context);

        if (!isValidForRole(userRole)) {
            System.out.println("Permission denied: Role '" + userRole
                    + "' not authorized for '" + permissionName + "'");
        } else {
            System.out.println("Permission granted: Access allowed");
        }
    }

    @Override
    public boolean isValidForRole(String role) {
        return validRoles.contains(role);
    }

    public int getSecurityLevel() {
        return securityLevel;
    }

    public Set<String> getValidRoles() {
        return new HashSet<>(validRoles);
    }

    @Override
    public String toString() {
        return "Permission{"
                + "name='" + permissionName + '\''
                + ", level=" + securityLevel
                + ", roles=" + validRoles
                + '}';
    }
}
