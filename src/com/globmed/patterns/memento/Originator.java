package com.globmed.patterns.memento;

/**
 *
 * @author Hansana
 */
public class Originator {

    private String state;

    public void setState(String state) {
        this.state = state;
    }

    public Memento saveStateToMemento() {
        return new Memento(state);
    }

    public void restoreStateFromMemento(Memento memento) {
        this.state = memento.getState();
    }
}
