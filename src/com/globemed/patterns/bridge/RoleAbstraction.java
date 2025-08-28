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
public interface RoleAbstraction {

    String getRoleName();

    Set<String> getPermissions();

    boolean hasPermission(String permission);

    void addPermission(String permission);

    void removePermission(String permission);

    void setPermissionImplementor(PermissionImplementor implementor);
}
