package com.globmed.patterns.mediator;

/**
 *
 * @author Hansana
 */
public class PatientColleague extends Colleague {

    public PatientColleague(Mediator mediator) {
        super(mediator);
    }

    @Override
    public void receive(String message) {
        System.out.println("Patient notified: " + message);
    }
}
