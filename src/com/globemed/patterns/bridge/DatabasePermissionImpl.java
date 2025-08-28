/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.bridge;

import com.globemed.database.DatabaseConnection;
import com.globemed.database.RoleDAO;
import com.globemed.models.Permission;
import java.sql.SQLException;
import java.util.*;

/**
 *
 * @author Hansana
 */
public class DatabasePermissionImpl implements PermissionImplementor {

    private RoleDAO roleDAO;
    private Map<String, Set<String>> permissionCache; // Cache for performance
    private boolean cacheEnabled;

    public DatabasePermissionImpl() {
        this.roleDAO = new RoleDAO();
        this.permissionCache = new HashMap<>();
        this.cacheEnabled = true;
        loadPermissions();
    }

    @Override
    public Set<String> getPermissionsForRole(String roleName) {
        if (cacheEnabled && permissionCache.containsKey(roleName)) {
            return new HashSet<>(permissionCache.get(roleName));
        }

        try {
            List<Permission> permissions = roleDAO.getPermissionsByRoleName(roleName);
            Set<String> permissionNames = new HashSet<>();

            for (Permission permission : permissions) {
                permissionNames.add(permission.getName());
            }

            // Update cache
            if (cacheEnabled) {
                permissionCache.put(roleName, new HashSet<>(permissionNames));
            }

            return permissionNames;

        } catch (SQLException e) {
            System.err.println("Error retrieving permissions for role " + roleName + ": " + e.getMessage());
            return new HashSet<>();
        }
    }

    @Override
    public boolean hasPermission(String roleName, String permission) {
        return getPermissionsForRole(roleName).contains(permission);
    }

    @Override
    public void addPermissionToRole(String roleName, String permission) {
        try {
            roleDAO.addPermissionToRole(roleName, permission);

            // Update cache
            if (cacheEnabled) {
                permissionCache.computeIfAbsent(roleName, k -> new HashSet<>()).add(permission);
            }

            System.out.println("Added permission '" + permission + "' to role '" + roleName + "' in database");

        } catch (SQLException e) {
            System.err.println("Error adding permission to role: " + e.getMessage());
        }
    }

    @Override
    public void removePermissionFromRole(String roleName, String permission) {
        try {
            roleDAO.removePermissionFromRole(roleName, permission);

            // Update cache
            if (cacheEnabled && permissionCache.containsKey(roleName)) {
                permissionCache.get(roleName).remove(permission);
            }

            System.out.println("Removed permission '" + permission + "' from role '" + roleName + "' in database");

        } catch (SQLException e) {
            System.err.println("Error removing permission from role: " + e.getMessage());
        }
    }

    @Override
    public List<String> getAllRoles() {
        try {
            return roleDAO.getAllRoleNames();
        } catch (SQLException e) {
            System.err.println("Error retrieving all roles: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public Set<String> getAllPermissions() {
        try {
            List<Permission> permissions = roleDAO.getAllPermissions();
            Set<String> permissionNames = new HashSet<>();

            for (Permission permission : permissions) {
                permissionNames.add(permission.getName());
            }

            return permissionNames;

        } catch (SQLException e) {
            System.err.println("Error retrieving all permissions: " + e.getMessage());
            return new HashSet<>();
        }
    }

    @Override
    public void savePermissions() {
        // In database implementation, data is saved immediately
        System.out.println("Database permissions are automatically saved");
    }

    @Override
    public void loadPermissions() {
        try {
            // Load all role-permission mappings into cache
            if (cacheEnabled) {
                List<String> roles = roleDAO.getAllRoleNames();
                for (String roleName : roles) {
                    Set<String> permissions = getPermissionsForRole(roleName);
                    permissionCache.put(roleName, permissions);
                }
                System.out.println("Loaded " + permissionCache.size() + " roles into permission cache");
            }
        } catch (SQLException e) {
            System.err.println("Error loading permissions: " + e.getMessage());
        }
    }

    public void clearCache() {
        permissionCache.clear();
        System.out.println("Permission cache cleared");
    }

    public void setCacheEnabled(boolean enabled) {
        this.cacheEnabled = enabled;
        if (!enabled) {
            clearCache();
        }
    }
}
