/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.decorator;

import com.globemed.models.Staff;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Hansana
 */
public class AuthenticationDecorator<T> extends SecurityDecorator<T> {

    private Map<String, AuthSession> activeSessions;
    private int sessionTimeoutMinutes;

    public AuthenticationDecorator(SecureService<T> service, int sessionTimeoutMinutes) {
        super(service, "Authentication");
        this.activeSessions = new HashMap<>();
        this.sessionTimeoutMinutes = sessionTimeoutMinutes;
    }

    @Override
    public T execute(T data) throws SecurityException {
        String sessionToken = getCurrentSessionToken();

        if (!isValidSession(sessionToken)) {
            logAccess("AUTHENTICATION_FAILED", "Invalid or expired session");
            throw new SecurityException("Authentication required - invalid session");
        }

        AuthSession session = activeSessions.get(sessionToken);
        session.updateLastAccess();

        logAccess("AUTHENTICATION_SUCCESS", "User: " + session.getUsername());
        return super.execute(data);
    }

    public String authenticateUser(String username, String password) {
        // Simulate authentication
        if (isValidCredentials(username, password)) {
            String sessionToken = generateSessionToken(username);
            AuthSession session = new AuthSession(username, sessionToken, LocalDateTime.now());
            activeSessions.put(sessionToken, session);

            logAccess("LOGIN_SUCCESS", "User authenticated: " + username);
            return sessionToken;
        } else {
            logAccess("LOGIN_FAILED", "Invalid credentials for: " + username);
            throw new SecurityException("Invalid credentials");
        }
    }

    public void logout(String sessionToken) {
        AuthSession session = activeSessions.remove(sessionToken);
        if (session != null) {
            logAccess("LOGOUT", "User logged out: " + session.getUsername());
        }
    }

    private boolean isValidSession(String sessionToken) {
        if (sessionToken == null || !activeSessions.containsKey(sessionToken)) {
            return false;
        }

        AuthSession session = activeSessions.get(sessionToken);
        return !session.isExpired(sessionTimeoutMinutes);
    }

    private boolean isValidCredentials(String username, String password) {
        // Simulate credential validation
        // In real implementation, this would check against database with hashed passwords
        return username != null && password != null
                && username.length() > 0 && password.length() >= 6;
    }

    private String generateSessionToken(String username) {
        return "SESSION_" + username + "_" + System.currentTimeMillis();
    }

    private String getCurrentSessionToken() {
        // In real implementation, this would get token from HTTP session or context
        // For demo, we'll use a thread-local or return the last created session
        return activeSessions.keySet().stream().findFirst().orElse(null);
    }

    // Inner class for session management
    private static class AuthSession {

        private String username;
        private String sessionToken;
        private LocalDateTime lastAccess;

        public AuthSession(String username, String sessionToken, LocalDateTime lastAccess) {
            this.username = username;
            this.sessionToken = sessionToken;
            this.lastAccess = lastAccess;
        }

        public void updateLastAccess() {
            this.lastAccess = LocalDateTime.now();
        }

        public boolean isExpired(int timeoutMinutes) {
            return LocalDateTime.now().isAfter(lastAccess.plusMinutes(timeoutMinutes));
        }

        public String getUsername() {
            return username;
        }

        public String getSessionToken() {
            return sessionToken;
        }

        public LocalDateTime getLastAccess() {
            return lastAccess;
        }
    }
}
