package com.globmed.patterns.memento;

/**
 *
 * @author Hansana
 */
public class Memento {

    private final String state;

    public Memento(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }
}
