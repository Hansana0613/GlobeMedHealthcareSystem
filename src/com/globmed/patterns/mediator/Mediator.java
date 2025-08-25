package com.globmed.patterns.mediator;

/**
 *
 * @author Hansana
 */
public interface Mediator {

    void bookAppointment(String patient, String staff, String time, String location);

    boolean checkConflict(String time, String location);

    void addColleague(Colleague colleague);
}
