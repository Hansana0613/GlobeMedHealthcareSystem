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
public class TaxDecorator extends BillServiceDecorator {

    private BigDecimal taxRate;
    private String taxType;

    public TaxDecorator(BillService service, BigDecimal taxRate, String taxType) {
        super(service);
        this.taxRate = taxRate;
        this.taxType = taxType;
    }

    @Override
    public MasterBill processBill(MasterBill bill) {
        MasterBill processedBill = super.processBill(bill);
        applyTax(processedBill);
        return processedBill;
    }

    @Override
    public void applyModifications(MasterBill bill) {
        super.applyModifications(bill);
        applyTax(bill);
    }

    private void applyTax(MasterBill bill) {
        if (taxRate.compareTo(BigDecimal.ZERO) > 0) {
            // Calculate tax on taxable items only (exclude previous tax items)
            BigDecimal taxableAmount = calculateTaxableAmount(bill);
            BigDecimal taxAmount = taxableAmount.multiply(taxRate.divide(new BigDecimal("100")))
                    .setScale(2, RoundingMode.HALF_UP);

            // Add tax as a bill item
            BillItemLeaf taxItem = new BillItemLeaf(
                    taxType + " (" + taxRate + "%)",
                    taxAmount,
                    "TAX"
            );

            bill.add(taxItem);

            System.out.println("Tax Applied: " + taxType + " " + taxRate + "% ($" + taxAmount + ")");
        }
    }

    private BigDecimal calculateTaxableAmount(MasterBill bill) {
        BigDecimal taxableAmount = BigDecimal.ZERO;

        for (BillComponent child : bill.getChildren()) {
            if (child instanceof BillItemLeaf) {
                BillItemLeaf item = (BillItemLeaf) child;
                // Don't tax previous taxes or discounts
                if (!"TAX".equals(item.getItemType()) && !"DISCOUNT".equals(item.getItemType())) {
                    taxableAmount = taxableAmount.add(item.getCost());
                }
            } else if (child instanceof BillComposite) {
                taxableAmount = taxableAmount.add(child.getCost());
            }
        }

        return taxableAmount;
    }
}
