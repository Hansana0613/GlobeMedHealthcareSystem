/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.chainofresponsibility;

import com.globemed.patterns.composite.BillComponent;
import java.math.BigDecimal;

/**
 *
 * @author Hansana
 */
public class HospitalApprovalHandler extends ClaimHandler {

    private static final BigDecimal MAX_HOSPITAL_APPROVAL = new BigDecimal("1000.00");

    @Override
    public ClaimResult handle(ClaimRequest request) {
        System.out.println("Hospital Approval: Processing claim for Bill #" + request.getBill().getBillId());

        BigDecimal totalAmount = request.getBill().getCost();

        // Check if bill is properly formatted and has all required items
        if (!validateBillStructure(request.getBill())) {
            return new ClaimResult(false, "Hospital Approval Failed: Invalid bill structure", BigDecimal.ZERO);
        }

        // Check if amount exceeds hospital approval limit
        if (totalAmount.compareTo(MAX_HOSPITAL_APPROVAL) > 0) {
            System.out.println("Amount exceeds hospital approval limit. Passing to insurance validation.");
            return passToNext(request);
        }

        // For amounts within limit, approve directly if direct pay
        if ("DIRECT_PAY".equals(request.getClaimType())) {
            System.out.println("Hospital Approval: Direct pay approved for $" + totalAmount);
            return new ClaimResult(true, "Hospital approved - Direct payment", totalAmount);
        }

        System.out.println("Hospital Approval: Passed initial validation, forwarding to insurance");
        return passToNext(request);
    }

    private boolean validateBillStructure(BillComponent bill) {
        // Ensure bill has proper structure and required components
        if (bill.getChildren().isEmpty()) {
            System.out.println("Validation failed: No bill items found");
            return false;
        }

        // Check for negative amounts
        if (bill.getCost().compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Validation failed: Invalid total amount");
            return false;
        }

        System.out.println("Bill structure validation passed");
        return true;
    }
}
