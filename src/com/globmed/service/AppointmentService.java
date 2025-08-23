package com.globmed.service;

import com.globmed.dao.AppointmentDAO;
import com.globmed.model.Appointment;
import com.globmed.patterns.mediator.LocationColleague;
import com.globmed.patterns.mediator.Mediator;
import com.globmed.patterns.mediator.PatientColleague;
import com.globmed.patterns.mediator.SchedulingMediator;
import com.globmed.patterns.mediator.StaffColleague;

/**
 *
 * @author Hansana
 */
public class AppointmentService {
    private AppointmentDAO appointmentDAO = new AppointmentDAO();
    private Mediator mediator = new SchedulingMediator();

    public AppointmentService() {
        mediator.addColleague(new PatientColleague(mediator));
        mediator.addColleague(new StaffColleague(mediator));
        mediator.addColleague(new LocationColleague(mediator));
    }

    public void bookAppointment(String patient, String staff, String time, String location) {
        mediator.bookAppointment(patient, staff, time, location);
        // Simulate saving (add DAO logic later)
        Appointment appt = new Appointment();
        // Set fields
        appointmentDAO.save(appt);
    }
}