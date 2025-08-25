package com.globmed.patterns.visitor;

import com.globmed.model.Bill;
import com.globmed.model.Patient;

/**
 *
 * @author Hansana
 */
public interface Visitor {

    void visitPatient(Patient patient);

    void visitBill(Bill bill);
}
