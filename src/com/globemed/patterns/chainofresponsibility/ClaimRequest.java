/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.chainofresponsibility;

import com.globemed.patterns.composite.MasterBill;

/**
 *
 * @author Hansana
 */
public class ClaimRequest {

    private MasterBill bill;
    private String claimType; // INSURANCE, DIRECT_PAY, PARTIAL_INSURANCE
    private String insuranceProvider;
    private String policyNumber;

    public ClaimRequest(MasterBill bill, String claimType, String insuranceProvider, String policyNumber) {
        this.bill = bill;
        this.claimType = claimType;
        this.insuranceProvider = insuranceProvider;
        this.policyNumber = policyNumber;
    }

    // Getters
    public MasterBill getBill() {
        return bill;
    }

    public String getClaimType() {
        return claimType;
    }

    public String getInsuranceProvider() {
        return insuranceProvider;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }
}
