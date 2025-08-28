/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.chainofresponsibility;

import java.math.BigDecimal;

/**
 *
 * @author Hansana
 */
public class FinalApprovalHandler extends ClaimHandler {

    private static final BigDecimal HIGH_VALUE_THRESHOLD = new BigDecimal("5000.00");

    @Override
    public ClaimResult handle(ClaimRequest request) {
        System.out.println("Final Approval: Processing final approval for Bill #" + request.getBill().getBillId());

        BigDecimal totalAmount = request.getBill().getCost();

        // High-value claims require special approval
        if (totalAmount.compareTo(HIGH_VALUE_THRESHOLD) > 0) {
            System.out.println("High-value claim detected: $" + totalAmount);
            return processHighValueClaim(request);
        }

        // Standard approval
        ClaimResult result = new ClaimResult(true, "Final approval granted", totalAmount);

        if ("DIRECT_PAY".equals(request.getClaimType())) {
            result.setPatientResponsibility(totalAmount);
            result.setProcessingNotes("Direct payment - full patient responsibility");
        } else {
            // Use previous handler's calculation for insurance claims
            result.setProcessingNotes("Claim approved and ready for processing");
        }

        // Update bill status
        request.getBill().setStatus("APPROVED");

        System.out.println("Final Approval: Claim approved for $" + result.getApprovedAmount());
        return result;
    }

    private ClaimResult processHighValueClaim(ClaimRequest request) {
        // High-value claims require additional verification
        System.out.println("Processing high-value claim - additional verification required");

        // Simulate manager approval (in real system, this would trigger workflow)
        boolean managerApproved = simulateManagerApproval(request);

        if (!managerApproved) {
            return new ClaimResult(false, "High-value claim rejected by management", BigDecimal.ZERO);
        }

        ClaimResult result = new ClaimResult(true, "High-value claim approved", request.getBill().getCost());
        result.setProcessingNotes("High-value claim approved with manager authorization");

        request.getBill().setStatus("APPROVED_HIGH_VALUE");

        return result;
    }

    private boolean simulateManagerApproval(ClaimRequest request) {
        // Simulate manager approval logic
        System.out.println("Simulating manager approval for high-value claim...");

        // In a real system, this would:
        // 1. Send notification to manager
        // 2. Wait for approval
        // 3. Log the approval decision
        // For simulation, approve if not suspicious patterns
        return !hasSuspiciousPatterns(request);
    }

    private boolean hasSuspiciousPatterns(ClaimRequest request) {
        // Check for suspicious patterns
        BigDecimal totalAmount = request.getBill().getCost();

        // Reject if amount is suspiciously round (might indicate fraud)
        if (totalAmount.remainder(new BigDecimal("1000")).equals(BigDecimal.ZERO)
                && totalAmount.compareTo(new BigDecimal("10000")) > 0) {
            System.out.println("Suspicious pattern detected: Round high-value amount");
            return true;
        }

        return false;
    }
}
