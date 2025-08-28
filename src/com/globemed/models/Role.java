/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.models;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Hansana
 */
public class Role {

    private Long id;
    private String name;
    private Long parentRoleId;
    private List<Permission> permissions;

    // Constructors
    public Role() {
        this.permissions = new ArrayList<>();
    }

    public Role(String name, Long parentRoleId) {
        this.name = name;
        this.parentRoleId = parentRoleId;
        this.permissions = new ArrayList<>();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParentRoleId() {
        return parentRoleId;
    }

    public void setParentRoleId(Long parentRoleId) {
        this.parentRoleId = parentRoleId;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public void addPermission(Permission permission) {
        this.permissions.add(permission);
    }

    @Override
    public String toString() {
        return "Role{id=" + id + ", name='" + name + "'}";
    }
}
