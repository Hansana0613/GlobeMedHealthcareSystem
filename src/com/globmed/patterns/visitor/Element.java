package com.globmed.patterns.visitor;

/**
 *
 * @author Hansana
 */
public interface Element {

    void accept(Visitor visitor);
}
