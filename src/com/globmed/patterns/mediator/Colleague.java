package com.globmed.patterns.mediator;

/**
 *
 * @author Hansana
 */
public abstract class Colleague {

    protected Mediator mediator;

    public Colleague(Mediator mediator) {
        this.mediator = mediator;
    }

    public abstract void receive(String message);
}
