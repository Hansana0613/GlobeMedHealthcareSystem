/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.composite;

import com.globemed.patterns.bridge.PermissionImplementor;
import java.util.*;

/**
 *
 * @author Hansana
 */
public class SimpleRole implements RoleComponent {

    private String roleName;
    private String description;
    private Set<String> directPermissions;
    private PermissionImplementor permissionImpl; // Bridge pattern integration
    private int roleLevel;

    public SimpleRole(String roleName, String description, PermissionImplementor permissionImpl, int roleLevel) {
        this.roleName = roleName;
        this.description = description;
        this.directPermissions = new HashSet<>();
        this.permissionImpl = permissionImpl;
        this.roleLevel = roleLevel;
    }

    @Override
    public String getRoleName() {
        return roleName;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Set<String> getAllPermissions() {
        // Get permissions from bridge implementation + direct permissions
        Set<String> allPermissions = new HashSet<>(directPermissions);
        if (permissionImpl != null) {
            allPermissions.addAll(permissionImpl.getPermissionsForRole(roleName));
        }
        return allPermissions;
    }

    @Override
    public List<RoleComponent> getSubRoles() {
        return new ArrayList<>(); // Leaf has no sub-roles
    }

    @Override
    public void addSubRole(RoleComponent role) {
        throw new UnsupportedOperationException("Cannot add sub-role to simple role");
    }

    @Override
    public void removeSubRole(RoleComponent role) {
        throw new UnsupportedOperationException("Cannot remove sub-role from simple role");
    }

    @Override
    public boolean hasPermission(String permission) {
        return getAllPermissions().contains(permission);
    }

    @Override
    public void display(String indent) {
        System.out.println(indent + "📋 Role: " + roleName + " (Level " + roleLevel + ")");
        System.out.println(indent + "   Description: " + description);
        System.out.println(indent + "   Permissions: " + getAllPermissions());
    }

    @Override
    public boolean isComposite() {
        return false;
    }

    @Override
    public int getRoleLevel() {
        return roleLevel;
    }

    // Methods to manage direct permissions
    public void addDirectPermission(String permission) {
        directPermissions.add(permission);
    }

    public void removeDirectPermission(String permission) {
        directPermissions.remove(permission);
    }

    public Set<String> getDirectPermissions() {
        return new HashSet<>(directPermissions);
    }
}
