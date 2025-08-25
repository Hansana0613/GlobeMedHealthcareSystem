package com.globmed.patterns.memento;

import java.util.Stack;

/**
 *
 * @author Hansana
 */
public class Caretaker {

    private Stack<Memento> history = new Stack<>();

    public void save(Originator originator) {
        history.push(originator.saveStateToMemento());
    }

    public void revert(Originator originator) {
        if (!history.isEmpty()) {
            originator.restoreStateFromMemento(history.pop());
        }
    }
}
