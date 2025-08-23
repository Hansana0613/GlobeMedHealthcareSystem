package com.globmed.patterns.chain;

import com.globmed.dao.PatientDAO;
import com.globmed.model.Patient;

/**
 *
 * @author Hansana
 */
public class RetrieveHandler extends Handler {

    private PatientDAO patientDAO = new PatientDAO();

    @Override
    public boolean handle(Request request) {
        Patient patient = patientDAO.findById(request.getPatientId());
        if (patient == null) {
            System.out.println("Patient not found.");
            return false;
        }
        System.out.println("Patient retrieved: " + patient.getName());
        return true;
    }
}
