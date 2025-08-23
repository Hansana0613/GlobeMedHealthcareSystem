package com.globmed.patterns.chain;

/**
 *
 * @author Hansana
 */
public class InsuranceValidator extends Handler {

    @Override
    public boolean handle(Request request) {
        System.out.println("Validating insurance details.");
        if (nextHandler != null) {
            return nextHandler.handle(request);
        }
        return true;
    }
}
