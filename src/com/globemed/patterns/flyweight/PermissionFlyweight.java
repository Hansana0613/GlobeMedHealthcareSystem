/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.flyweight;

/**
 *
 * @author Hansana
 */
public interface PermissionFlyweight {

    String getPermissionName();

    String getDescription();

    void executePermissionCheck(String context, String userRole);

    boolean isValidForRole(String role);
}
