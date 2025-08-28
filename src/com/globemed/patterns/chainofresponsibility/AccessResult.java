/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.chainofresponsibility;

/**
 *
 * @author Hansana
 */
public class AccessResult {

    private boolean granted;
    private String message;
    private String logDetails;

    public AccessResult(boolean granted, String message) {
        this.granted = granted;
        this.message = message;
    }

    public AccessResult(boolean granted, String message, String logDetails) {
        this.granted = granted;
        this.message = message;
        this.logDetails = logDetails;
    }

    // Getters and Setters
    public boolean isGranted() {
        return granted;
    }

    public void setGranted(boolean granted) {
        this.granted = granted;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLogDetails() {
        return logDetails;
    }

    public void setLogDetails(String logDetails) {
        this.logDetails = logDetails;
    }
}
