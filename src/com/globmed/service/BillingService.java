package com.globmed.service;

import com.globmed.dao.BillDAO;
import com.globmed.model.Bill;
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

    public Bill createBill(String description) {
        Composite bill = new Composite(description);
        // Add sample items
        bill.add(new Leaf("Consultation", 50.0));
        bill.add(new Leaf("Treatment", 100.0));
        Bill model = new Bill();
        model.setTotalAmount(bill.getCost());
        model.setInsuranceDetails("Insurance Co X");
        billDAO.save(model);
        return model;
    }

    public boolean processClaim(Bill bill) {
        Request request = new Request("user", "Admin", bill.getId()); // Placeholder
        return claimChain.handle(request);
    }
}
