package com.globmed.patterns.composite;

/**
 *
 * @author Hansana
 */
public interface RoleComponent {

    String getName();

    void add(RoleComponent component);

    void remove(RoleComponent component);

    boolean hasPermission(String permission);
}
