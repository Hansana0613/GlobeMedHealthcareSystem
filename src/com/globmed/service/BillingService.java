package com.globmed.service;

import com.globmed.dao.BillDAO;
import com.globmed.model.Appointment;
import com.globmed.model.Bill;
import com.globmed.patterns.builder.BillBuilder;
import com.globmed.patterns.chain.ApproverHandler;
import com.globmed.patterns.chain.BillingHandler;
import com.globmed.patterns.chain.Handler;
import com.globmed.patterns.chain.InsuranceValidator;
import com.globmed.patterns.chain.Request;
import com.globmed.patterns.composite.Composite;
import com.globmed.patterns.composite.Leaf;

/**
 *
 * @author Hansana
 */
public class BillingService {

    private BillDAO billDAO = new BillDAO();
    private Handler claimChain;

    public BillingService() {
        claimChain = new BillingHandler();
        claimChain.setNext(new InsuranceValidator());
        claimChain.setNext(new ApproverHandler());
    }

    public Bill createBill(Appointment appointment) {
        BillBuilder builder = new BillBuilder()
                .setAppointment(appointment)
                .addItem("Consultation", 50.0)
                .addItem("Treatment", 100.0)
                .setTotalAmount(150.0)
                .setInsuranceDetails("Insurance Co X");
        return builder.build();
    }

    public boolean processClaim(Bill bill) {
        Request request = new Request("user", "Admin", bill.getId()); // Placeholder
        return claimChain.handle(request);
    }
}
