package com.globmed.patterns.chain;

/**
 *
 * @author Hansana
 */
public class Request {

    private String username;
    private String role;
    private Long patientId;

    public Request(String username, String role, Long patientId) {
        this.username = username;
        this.role = role;
        this.patientId = patientId;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public Long getPatientId() {
        return patientId;
    }
}
