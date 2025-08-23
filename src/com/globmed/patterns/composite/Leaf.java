package com.globmed.patterns.composite;

/**
 *
 * @author Hansana
 */
public class Leaf implements Component {

    private String description;
    private double cost;

    public Leaf(String description, double cost) {
        this.description = description;
        this.cost = cost;
    }

    @Override
    public double getCost() {
        return cost;
    }

    @Override
    public void add(Component component) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(Component component) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getDescription() {
        return description;
    }
}
