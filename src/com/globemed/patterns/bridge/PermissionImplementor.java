/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.globemed.patterns.bridge;

import java.util.Set;
import java.util.List;

/**
 *
 * @author Hansana
 */
public interface PermissionImplementor {

    Set<String> getPermissionsForRole(String roleName);

    boolean hasPermission(String roleName, String permission);

    void addPermissionToRole(String roleName, String permission);

    void removePermissionFromRole(String roleName, String permission);

    List<String> getAllRoles();

    Set<String> getAllPermissions();

    void savePermissions();

    void loadPermissions();
}
