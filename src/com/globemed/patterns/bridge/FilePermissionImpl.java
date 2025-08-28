/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.bridge;

import java.io.*;
import java.util.*;

/**
 *
 * @author Hansana
 */
public class FilePermissionImpl implements PermissionImplementor {

    private String fileName;
    private Map<String, Set<String>> rolePermissions;

    public FilePermissionImpl(String fileName) {
        this.fileName = fileName;
        this.rolePermissions = new HashMap<>();
        loadPermissions();
    }

    @Override
    public Set<String> getPermissionsForRole(String roleName) {
        return rolePermissions.getOrDefault(roleName, new HashSet<>());
    }

    @Override
    public boolean hasPermission(String roleName, String permission) {
        return getPermissionsForRole(roleName).contains(permission);
    }

    @Override
    public void addPermissionToRole(String roleName, String permission) {
        rolePermissions.computeIfAbsent(roleName, k -> new HashSet<>()).add(permission);
        savePermissions();
        System.out.println("Added permission '" + permission + "' to role '" + roleName + "' in file");
    }

    @Override
    public void removePermissionFromRole(String roleName, String permission) {
        Set<String> permissions = rolePermissions.get(roleName);
        if (permissions != null) {
            permissions.remove(permission);
            if (permissions.isEmpty()) {
                rolePermissions.remove(roleName);
            }
            savePermissions();
            System.out.println("Removed permission '" + permission + "' from role '" + roleName + "' in file");
        }
    }

    @Override
    public List<String> getAllRoles() {
        return new ArrayList<>(rolePermissions.keySet());
    }

    @Override
    public Set<String> getAllPermissions() {
        Set<String> allPermissions = new HashSet<>();
        for (Set<String> permissions : rolePermissions.values()) {
            allPermissions.addAll(permissions);
        }
        return allPermissions;
    }

    @Override
    public void savePermissions() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (Map.Entry<String, Set<String>> entry : rolePermissions.entrySet()) {
                String roleName = entry.getKey();
                Set<String> permissions = entry.getValue();

                for (String permission : permissions) {
                    writer.println(roleName + "=" + permission);
                }
            }
            System.out.println("Permissions saved to file: " + fileName);
        } catch (IOException e) {
            System.err.println("Error saving permissions to file: " + e.getMessage());
        }
    }

    @Override
    public void loadPermissions() {
        rolePermissions.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    String roleName = parts[0].trim();
                    String permission = parts[1].trim();
                    rolePermissions.computeIfAbsent(roleName, k -> new HashSet<>()).add(permission);
                }
            }
            System.out.println("Permissions loaded from file: " + fileName);
        } catch (FileNotFoundException e) {
            System.out.println("Permission file not found, starting with empty permissions: " + fileName);
        } catch (IOException e) {
            System.err.println("Error loading permissions from file: " + e.getMessage());
        }
    }
}
