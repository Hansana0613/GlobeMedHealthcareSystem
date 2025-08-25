package com.globmed.patterns.mediator;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Hansana
 */
public class SchedulingMediator implements Mediator {

    private List<Colleague> colleagues = new ArrayList<>();

    @Override
    public void bookAppointment(String patient, String staff, String time, String location) {
        if (!checkConflict(time, location)) {
            System.out.println("Conflict detected, booking failed.");
            return;
        }
        for (Colleague colleague : colleagues) {
            colleague.receive("Booking: " + patient + " with " + staff + " at " + time + " in " + location);
        }
        System.out.println("Appointment booked successfully.");
    }

    @Override
    public boolean checkConflict(String time, String location) {
        // Simulate conflict check (e.g., against existing appointments)
        return true; // Placeholder, implement DB check later
    }

    @Override
    public void addColleague(Colleague colleague) {
        colleagues.add(colleague);
    }
}
