package com.globmed.model;

/**
 *
 * @author Hansana
 */
public class Permission {

    private Long id;
    private String name;
    private String description;

    // Default constructor
    public Permission() {
    }

    // Getters/Setters
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
