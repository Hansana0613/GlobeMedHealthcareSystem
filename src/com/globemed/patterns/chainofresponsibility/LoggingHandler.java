/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.chainofresponsibility;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Hansana
 */
public class LoggingHandler extends AccessHandler {

    @Override
    public AccessResult handle(AccessRequest request) {
        // Log the access attempt
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String logEntry = String.format("[%s] User: %s, Action: %s, PatientID: %d, IP: %s",
                timestamp,
                request.getStaff().getUsername(),
                request.getAction(),
                request.getPatientId(),
                request.getClientIP());

        System.out.println("ACCESS LOG: " + logEntry);

        // Could write to database or file here
        // For now, just pass to next or return success
        return passToNext(request);
    }
}
