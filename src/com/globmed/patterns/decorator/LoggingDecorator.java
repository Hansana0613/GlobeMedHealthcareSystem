package com.globmed.patterns.decorator;

import java.time.LocalDateTime;

/**
 *
 * @author Hansana
 */
public class LoggingDecorator extends ServiceDecorator {

    public LoggingDecorator(Service service) {
        super(service);
    }

    @Override
    public String execute(String input) {
        System.out.println("Log at " + LocalDateTime.now() + ": Processing " + input);
        return super.execute(input);
    }
}
