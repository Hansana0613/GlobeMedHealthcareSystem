package com.globmed.patterns.composite;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Hansana
 */
public class Composite implements Component {

    private List<Component> children = new ArrayList<>();
    private String description;

    public Composite(String description) {
        this.description = description;
    }

    @Override
    public double getCost() {
        return children.stream().mapToDouble(Component::getCost).sum();
    }

    @Override
    public void add(Component component) {
        children.add(component);
    }

    @Override
    public void remove(Component component) {
        children.remove(component);
    }

    @Override
    public String getDescription() {
        return description + " (Total Items: " + children.size() + ")";
    }

    public List<Component> getChildren() {
        return children;
    }
}
