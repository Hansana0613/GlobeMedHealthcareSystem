/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.decorator;

import com.globemed.patterns.composite.MasterBill;
import java.math.BigDecimal;

/**
 *
 * @author Hansana
 */
public abstract class BillServiceDecorator implements BillService {

    protected BillService wrappedService;

    public BillServiceDecorator(BillService service) {
        this.wrappedService = service;
    }

    @Override
    public MasterBill processBill(MasterBill bill) {
        return wrappedService.processBill(bill);
    }

    @Override
    public BigDecimal calculateTotal(MasterBill bill) {
        return wrappedService.calculateTotal(bill);
    }

    @Override
    public void applyModifications(MasterBill bill) {
        wrappedService.applyModifications(bill);
    }
}
