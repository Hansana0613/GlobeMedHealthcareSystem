/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.chainofresponsibility;

import com.globemed.models.Staff;
import com.globemed.models.Patient;

/**
 *
 * @author Hansana
 */
public abstract class AccessHandler {

    protected AccessHandler nextHandler;

    public void setNext(AccessHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    public abstract AccessResult handle(AccessRequest request);

    protected AccessResult passToNext(AccessRequest request) {
        if (nextHandler != null) {
            return nextHandler.handle(request);
        }
        return new AccessResult(true, "Access granted");
    }
}
