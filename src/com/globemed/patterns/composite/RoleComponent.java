/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.globemed.patterns.composite;

import java.util.List;
import java.util.Set;

/**
 *
 * @author Hansana
 */
public interface RoleComponent {

    String getRoleName();

    String getDescription();

    Set<String> getAllPermissions();

    List<RoleComponent> getSubRoles();

    void addSubRole(RoleComponent role);

    void removeSubRole(RoleComponent role);

    boolean hasPermission(String permission);

    void display(String indent);

    boolean isComposite();

    int getRoleLevel(); // Hierarchy level
}
