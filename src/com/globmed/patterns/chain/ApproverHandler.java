package com.globmed.patterns.chain;

/**
 *
 * @author Hansana
 */
public class ApproverHandler extends Handler {

    @Override
    public boolean handle(Request request) {
        System.out.println("Approving claim.");
        return true;
    }
}
