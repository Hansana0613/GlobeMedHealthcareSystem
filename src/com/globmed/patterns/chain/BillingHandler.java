package com.globmed.patterns.chain;

/**
 *
 * @author Hansana
 */
public class BillingHandler extends Handler {

    @Override
    public boolean handle(Request request) {
        System.out.println("Validating bill data.");
        if (nextHandler != null) {
            return nextHandler.handle(request);
        }
        return true;
    }
}
