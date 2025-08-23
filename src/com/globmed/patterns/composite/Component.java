package com.globmed.patterns.composite;

/**
 *
 * @author Hansana
 */
public interface Component {

    double getCost();

    void add(Component component);

    void remove(Component component);

    String getDescription();
}
