/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.chainofresponsibility;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 * @author Hansana
 */
public class InsuranceValidationHandler extends ClaimHandler {

    @Override
    public ClaimResult handle(ClaimRequest request) {
        System.out.println("Insurance Validation: Processing claim for " + request.getInsuranceProvider());

        if ("DIRECT_PAY".equals(request.getClaimType())) {
            // Skip insurance validation for direct pay
            return passToNext(request);
        }

        // Simulate insurance validation
        if (!validateInsurancePolicy(request)) {
            return new ClaimResult(false, "Insurance validation failed: Invalid policy", BigDecimal.ZERO);
        }

        // Calculate coverage based on insurance type
        ClaimResult result = calculateInsuranceCoverage(request);

        if (result.isApproved()) {
            System.out.println("Insurance Validation: Approved $" + result.getApprovedAmount()
                    + " (Patient responsibility: $" + result.getPatientResponsibility() + ")");
            return passToNext(request);
        }

        return result;
    }

    private boolean validateInsurancePolicy(ClaimRequest request) {
        // Simulate policy validation
        if (request.getPolicyNumber() == null || request.getPolicyNumber().isEmpty()) {
            System.out.println("Policy validation failed: No policy number");
            return false;
        }

        // Check if policy is active (simulation)
        if (request.getPolicyNumber().startsWith("EXPIRED")) {
            System.out.println("Policy validation failed: Policy expired");
            return false;
        }

        System.out.println("Policy validation passed for policy: " + request.getPolicyNumber());
        return true;
    }

    private ClaimResult calculateInsuranceCoverage(ClaimRequest request) {
        BigDecimal totalAmount = request.getBill().getCost();
        BigDecimal approvedAmount = BigDecimal.ZERO;
        BigDecimal patientResponsibility = totalAmount;

        // Different coverage rates based on insurance provider
        switch (request.getInsuranceProvider().toUpperCase()) {
            case "PREMIUM_INSURANCE":
                // 90% coverage, 10% copay
                approvedAmount = totalAmount.multiply(new BigDecimal("0.90")).setScale(2, RoundingMode.HALF_UP);
                patientResponsibility = totalAmount.subtract(approvedAmount);
                break;

            case "STANDARD_INSURANCE":
                // 80% coverage, 20% copay
                approvedAmount = totalAmount.multiply(new BigDecimal("0.80")).setScale(2, RoundingMode.HALF_UP);
                patientResponsibility = totalAmount.subtract(approvedAmount);
                break;

            case "BASIC_INSURANCE":
                // 70% coverage, 30% copay
                approvedAmount = totalAmount.multiply(new BigDecimal("0.70")).setScale(2, RoundingMode.HALF_UP);
                patientResponsibility = totalAmount.subtract(approvedAmount);
                break;

            default:
                // Unknown insurance - reject
                return new ClaimResult(false, "Unknown insurance provider", BigDecimal.ZERO);
        }

        ClaimResult result = new ClaimResult(true, "Insurance validation passed", approvedAmount);
        result.setPatientResponsibility(patientResponsibility);
        result.setProcessingNotes("Coverage calculated based on " + request.getInsuranceProvider() + " policy");

        return result;
    }
}
