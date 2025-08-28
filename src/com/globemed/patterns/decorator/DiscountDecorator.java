/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.decorator;

import com.globemed.patterns.composite.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 * @author Hansana
 */
public class DiscountDecorator extends BillServiceDecorator {

    private BigDecimal discountPercentage;
    private String discountReason;

    public DiscountDecorator(BillService service, BigDecimal discountPercentage, String discountReason) {
        super(service);
        this.discountPercentage = discountPercentage;
        this.discountReason = discountReason;
    }

    @Override
    public MasterBill processBill(MasterBill bill) {
        MasterBill processedBill = super.processBill(bill);
        applyDiscount(processedBill);
        return processedBill;
    }

    @Override
    public void applyModifications(MasterBill bill) {
        super.applyModifications(bill);
        applyDiscount(bill);
    }

    private void applyDiscount(MasterBill bill) {
        if (discountPercentage.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal originalTotal = bill.getCost();
            BigDecimal discountAmount = originalTotal.multiply(discountPercentage.divide(new BigDecimal("100")))
                    .setScale(2, RoundingMode.HALF_UP);

            // Add discount as a negative bill item
            BillItemLeaf discountItem = new BillItemLeaf(
                    "Discount (" + discountPercentage + "%) - " + discountReason,
                    discountAmount.negate(),
                    "DISCOUNT"
            );

            bill.add(discountItem);

            System.out.println("Discount Applied: " + discountPercentage + "% ($" + discountAmount + ") - " + discountReason);
        }
    }
}
