package com.globmed.patterns.mediator;

/**
 *
 * @author Hansana
 */
public class LocationColleague extends Colleague {

    public LocationColleague(Mediator mediator) {
        super(mediator);
    }

    @Override
    public void receive(String message) {
        System.out.println("Location reserved: " + message);
    }
}
