package com.globmed.patterns.flyweight;

/**
 *
 * @author Hansana
 */
public class PermissionFlyweight {

    private String name;
    private String description;

    public PermissionFlyweight(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
