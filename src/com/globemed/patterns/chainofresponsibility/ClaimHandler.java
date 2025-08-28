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
public abstract class ClaimHandler {

    protected ClaimHandler nextHandler;

    public void setNext(ClaimHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    public abstract ClaimResult handle(ClaimRequest request);

    protected ClaimResult passToNext(ClaimRequest request) {
        if (nextHandler != null) {
            return nextHandler.handle(request);
        }
        return new ClaimResult(true, "Claim approved", request.getBill().getCost());
    }
}
