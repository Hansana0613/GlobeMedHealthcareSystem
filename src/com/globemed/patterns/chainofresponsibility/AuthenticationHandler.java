/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.chainofresponsibility;

/**
 *
 * @author Hansana
 */
public class AuthenticationHandler extends AccessHandler {

    @Override
    public AccessResult handle(AccessRequest request) {
        // Check if user is authenticated
        if (request.getStaff() == null) {
            return new AccessResult(false, "Authentication failed: No user session",
                    "AUTH_FAIL: No user session from IP " + request.getClientIP());
        }

        if (request.getStaff().getUsername() == null || request.getStaff().getUsername().isEmpty()) {
            return new AccessResult(false, "Authentication failed: Invalid username",
                    "AUTH_FAIL: Invalid username from IP " + request.getClientIP());
        }

        System.out.println("Authentication passed for user: " + request.getStaff().getUsername());
        return passToNext(request);
    }
}
