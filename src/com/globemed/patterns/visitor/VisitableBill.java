/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.visitor;

import com.globemed.models.Bill;

/**
 *
 * @author Hansana
 */
public class VisitableBill implements Visitable {

    private Bill bill;

    public VisitableBill(Bill bill) {
        this.bill = bill;
    }

    @Override
    public void accept(ReportVisitor visitor) {
        visitor.visitBill(bill);
    }

    public Bill getBill() {
        return bill;
    }
}
