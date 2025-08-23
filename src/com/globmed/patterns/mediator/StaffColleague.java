package com.globmed.patterns.mediator;

/**
 *
 * @author Hansana
 */
public class StaffColleague extends Colleague {

    public StaffColleague(Mediator mediator) {
        super(mediator);
    }

    @Override
    public void receive(String message) {
        System.out.println("Staff notified: " + message);
    }
}
