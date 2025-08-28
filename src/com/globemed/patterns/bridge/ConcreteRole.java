/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.bridge;

import java.util.Set;

/**
 *
 * @author Hansana
 */
public class ConcreteRole implements RoleAbstraction {

    protected String roleName;
    protected String description;
    protected PermissionImplementor permissionImpl;

    public ConcreteRole(String roleName, String description, PermissionImplementor permissionImpl) {
        this.roleName = roleName;
        this.description = description;
        this.permissionImpl = permissionImpl;
    }

    @Override
    public String getRoleName() {
        return roleName;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public Set<String> getPermissions() {
        return permissionImpl.getPermissionsForRole(roleName);
    }

    @Override
    public boolean hasPermission(String permission) {
        return permissionImpl.hasPermission(roleName, permission);
    }

    @Override
    public void addPermission(String permission) {
        permissionImpl.addPermissionToRole(roleName, permission);
    }

    @Override
    public void removePermission(String permission) {
        permissionImpl.removePermissionFromRole(roleName, permission);
    }

    @Override
    public void setPermissionImplementor(PermissionImplementor implementor) {
        this.permissionImpl = implementor;
    }

    public void displayRoleInfo() {
        System.out.println("Role: " + roleName);
        System.out.println("Description: " + description);
        System.out.println("Permissions: " + getPermissions());
    }

    // Method to switch between different permission storage backends
    public void migrateTo(PermissionImplementor newImplementor) {
        Set<String> currentPermissions = getPermissions();
        setPermissionImplementor(newImplementor);

        // Transfer permissions to new implementor
        for (String permission : currentPermissions) {
            newImplementor.addPermissionToRole(roleName, permission);
        }

        System.out.println("Migrated role '" + roleName + "' to new permission backend");
    }
}
