package com.globmed.patterns.builder;

import com.globmed.model.Appointment;
import com.globmed.model.Bill;
import com.globmed.model.BillItem;
import java.util.ArrayList;

/**
 *
 * @author Hansana
 */
public class BillBuilder {

    private Bill bill = new Bill();

    public BillBuilder setAppointment(Appointment appointment) {
        bill.setAppointment(appointment);
        return this;
    }

    public BillBuilder addItem(String description, double cost) {
        BillItem item = new BillItem();
        item.setDescription(description);
        item.setCost(cost);
        if (bill.getItems() == null) {
            bill.setItems(new ArrayList<>());
        }
        bill.getItems().add(item);
        return this;
    }

    public BillBuilder setTotalAmount(double total) {
        bill.setTotalAmount(total);
        return this;
    }

    public BillBuilder setInsuranceDetails(String details) {
        bill.setInsuranceDetails(details);
        return this;
    }

    public Bill build() {
        return bill;
    }
}
