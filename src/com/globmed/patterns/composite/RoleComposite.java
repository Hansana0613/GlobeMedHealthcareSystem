package com.globmed.patterns.composite;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Hansana
 */
public class RoleComposite implements RoleComponent {

    private String name;
    private List<RoleComponent> children = new ArrayList<>();

    public RoleComposite(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void add(RoleComponent component) {
        children.add(component);
    }

    @Override
    public void remove(RoleComponent component) {
        children.remove(component);
    }

    @Override
    public boolean hasPermission(String permission) {
        return children.stream().anyMatch(c -> c.hasPermission(permission));
    }
}
