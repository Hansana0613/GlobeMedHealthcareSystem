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
public class BasicBillService implements BillService {

    @Override
    public MasterBill processBill(MasterBill bill) {
        System.out.println("Basic Bill Processing: Processing bill #" + bill.getBillId());
        return bill;
    }

    @Override
    public BigDecimal calculateTotal(MasterBill bill) {
        return bill.getCost();
    }

    @Override
    public void applyModifications(MasterBill bill) {
        // Basic service applies no modifications
        System.out.println("Basic Bill Service: No modifications applied");
    }
}
