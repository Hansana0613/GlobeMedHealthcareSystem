package com.globmed.patterns.decorator;

/**
 *
 * @author Hansana
 */
public class BaseService implements Service {

    @Override
    public String execute(String input) {
        return "Processed: " + input;
    }
}
