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
public class CompositeRole implements RoleComponent {

    private String roleName;
    private String description;
    private List<RoleComponent> subRoles;
    private Set<String> directPermissions;
    private PermissionImplementor permissionImpl; // Bridge pattern integration
    private int roleLevel;

    public CompositeRole(String roleName, String description, PermissionImplementor permissionImpl, int roleLevel) {
        this.roleName = roleName;
        this.description = description;
        this.subRoles = new ArrayList<>();
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
        Set<String> allPermissions = new HashSet<>(directPermissions);

        // Add permissions from bridge implementation
        if (permissionImpl != null) {
            allPermissions.addAll(permissionImpl.getPermissionsForRole(roleName));
        }

        // Add permissions from sub-roles (inheritance)
        for (RoleComponent subRole : subRoles) {
            allPermissions.addAll(subRole.getAllPermissions());
        }

        return allPermissions;
    }

    @Override
    public List<RoleComponent> getSubRoles() {
        return new ArrayList<>(subRoles);
    }

    @Override
    public void addSubRole(RoleComponent role) {
        // Ensure role level hierarchy is maintained
        if (role.getRoleLevel() > this.roleLevel) {
            subRoles.add(role);
            //System.out.println("Added sub-role: " + role.getRoleName() + " to " + this.roleName);
        } else {
            System.out.println("Cannot add role " + role.getRoleName()
                    + " (Level " + role.getRoleLevel() + ") to " + this.roleName
                    + " (Level " + this.roleLevel + ") - Invalid hierarchy");
        }
    }

    @Override
    public void removeSubRole(RoleComponent role) {
        subRoles.remove(role);
        System.out.println("Removed sub-role: " + role.getRoleName() + " from " + this.roleName);
    }

    @Override
    public boolean hasPermission(String permission) {
        return getAllPermissions().contains(permission);
    }

    @Override
    public void display(String indent) {
        System.out.println(indent + "🏢 Composite Role: " + roleName + " (Level " + roleLevel + ")");
        System.out.println(indent + "   Description: " + description);
        System.out.println(indent + "   Direct Permissions: " + directPermissions);
        System.out.println(indent + "   All Permissions: " + getAllPermissions());
        System.out.println(indent + "   Sub-roles:");

        for (RoleComponent subRole : subRoles) {
            subRole.display(indent + "     ");
        }
    }

    @Override
    public boolean isComposite() {
        return true;
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

    public int getSubRoleCount() {
        return subRoles.size();
    }

    public int getTotalRoleCount() {
        int count = 1; // Self
        for (RoleComponent subRole : subRoles) {
            if (subRole instanceof CompositeRole) {
                count += ((CompositeRole) subRole).getTotalRoleCount();
            } else {
                count += 1;
            }
        }
        return count;
    }
}
