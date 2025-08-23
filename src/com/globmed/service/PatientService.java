package com.globmed.service;

import com.globmed.dao.PatientDAO;
import com.globmed.model.Patient;
import com.globmed.patterns.chain.AuthHandler;
import com.globmed.patterns.chain.Handler;
import com.globmed.patterns.chain.LogHandler;
import com.globmed.patterns.chain.Request;
import com.globmed.patterns.chain.RetrieveHandler;
import com.globmed.patterns.chain.RoleHandler;

/**
 *
 * @author Hansana
 */
public class PatientService {

    private PatientDAO patientDAO = new PatientDAO();
    private Handler chain;

    public PatientService() {
        chain = new AuthHandler();
        chain.setNext(new RoleHandler());
        chain.setNext(new LogHandler());
        chain.setNext(new RetrieveHandler());
    }

    public Patient getPatient(String username, String role, Long patientId) {
        Request request = new Request(username, role, patientId);
        if (chain.handle(request)) {
            return patientDAO.findById(patientId);
        }
        return null;
    }
}
