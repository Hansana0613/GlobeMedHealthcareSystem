package com.globmed.patterns.visitor;

import com.globmed.model.Bill;
import com.globmed.model.Patient;

/**
 *
 * @author Hansana
 */
public class SummaryVisitor implements Visitor {

    @Override
    public void visitPatient(Patient patient) {
        System.out.println("Patient Summary: " + patient.getName() + ", DOB: " + patient.getDob());
    }

    @Override
    public void visitBill(Bill bill) {
        System.out.println("Bill Summary: Total $" + bill.getTotalAmount() + ", Status: " + bill.getClaimStatus());
    }
}
