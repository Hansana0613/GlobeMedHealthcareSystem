package com.globmed.patterns.decorator;

/**
 *
 * @author Hansana
 */
public abstract class ServiceDecorator implements Service {

    protected Service service;

    public ServiceDecorator(Service service) {
        this.service = service;
    }

    @Override
    public String execute(String input) {
        return service.execute(input);
    }
}
