package com.globmed.model;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Hansana
 */
public class Role {
    private Long id;
    private String name;
    private Role parentRole;
    private Set<Permission> permissions = new HashSet<>();

    // Default constructor
    public Role() {}

    // Getters/Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Role getParentRole() { return parentRole; }
    public void setParentRole(Role parentRole) { this.parentRole = parentRole; }
    public Set<Permission> getPermissions() { return permissions; }
    public void setPermissions(Set<Permission> permissions) { this.permissions = permissions; }
}
