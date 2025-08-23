package com.globmed.patterns.chain;

import java.time.LocalDateTime;

/**
 *
 * @author Hansana
 */
public class LogHandler extends Handler {

    @Override
    public boolean handle(Request request) {
        System.out.println("Logged access attempt at " + LocalDateTime.now() + " by " + request.getUsername());
        if (nextHandler != null) {
            return nextHandler.handle(request);
        }
        return true;
    }
}
