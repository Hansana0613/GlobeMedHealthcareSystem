package com.globmed.patterns.composite;

/**
 *
 * @author Hansana
 */
public class PermissionLeaf implements RoleComponent {

    private String name;

    public PermissionLeaf(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void add(RoleComponent component) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(RoleComponent component) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.name.equals(permission);
    }
}
