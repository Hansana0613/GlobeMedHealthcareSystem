/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.decorator;

import com.globemed.patterns.composite.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 *
 * @author Hansana
 */
public class LateFeeDecorator extends BillServiceDecorator {

    private int gracePeriodDays;
    private BigDecimal lateFeeRate; // Daily rate

    public LateFeeDecorator(BillService service, int gracePeriodDays, BigDecimal lateFeeRate) {
        super(service);
        this.gracePeriodDays = gracePeriodDays;
        this.lateFeeRate = lateFeeRate;
    }

    @Override
    public MasterBill processBill(MasterBill bill) {
        MasterBill processedBill = super.processBill(bill);
        applyLateFeeIfApplicable(processedBill);
        return processedBill;
    }

    @Override
    public void applyModifications(MasterBill bill) {
        super.applyModifications(bill);
        applyLateFeeIfApplicable(bill);
    }

    private void applyLateFeeIfApplicable(MasterBill bill) {
        LocalDateTime billDate = bill.getCreatedAt();
        LocalDateTime now = LocalDateTime.now();

        long daysSinceBill = ChronoUnit.DAYS.between(billDate, now);

        if (daysSinceBill > gracePeriodDays && !"PAID".equals(bill.getStatus())) {
            long lateDays = daysSinceBill - gracePeriodDays;
            BigDecimal originalAmount = calculateOriginalAmount(bill);
            BigDecimal lateFeeAmount = originalAmount
                    .multiply(lateFeeRate.multiply(new BigDecimal(lateDays)))
                    .divide(new BigDecimal("100"));

            // Add late fee item
            BillItemLeaf lateFeeItem = new BillItemLeaf(
                    "Late Payment Fee (" + lateDays + " days @ " + lateFeeRate + "%/day)",
                    lateFeeAmount,
                    "LATE_FEE"
            );

            bill.add(lateFeeItem);

            System.out.println("Late Fee Applied: $" + lateFeeAmount + " for " + lateDays + " late days");
        }
    }

    private BigDecimal calculateOriginalAmount(MasterBill bill) {
        BigDecimal originalAmount = BigDecimal.ZERO;

        for (BillComponent child : bill.getChildren()) {
            if (child instanceof BillItemLeaf) {
                BillItemLeaf item = (BillItemLeaf) child;
                // Include only original charges
                if (!"LATE_FEE".equals(item.getItemType())
                        && !"TAX".equals(item.getItemType())
                        && !"DISCOUNT".equals(item.getItemType())) {
                    originalAmount = originalAmount.add(item.getCost());
                }
            } else if (child instanceof BillComposite) {
                originalAmount = originalAmount.add(child.getCost());
            }
        }

        return originalAmount;
    }
}
