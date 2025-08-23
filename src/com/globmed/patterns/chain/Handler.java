package com.globmed.patterns.chain;

/**
 *
 * @author Hansana
 */
public abstract class Handler {

    protected Handler nextHandler;

    public void setNext(Handler nextHandler) {
        this.nextHandler = nextHandler;
    }

    public abstract boolean handle(Request request);
}
