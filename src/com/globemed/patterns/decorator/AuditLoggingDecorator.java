/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.decorator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author Hansana
 */
public class AuditLoggingDecorator<T> extends SecurityDecorator<T> {

    private ConcurrentLinkedQueue<AuditLogEntry> auditLog;
    private boolean detailedLogging;

    public AuditLoggingDecorator(SecureService<T> service, boolean detailedLogging) {
        super(service, "AuditLogging");
        this.auditLog = new ConcurrentLinkedQueue<>();
        this.detailedLogging = detailedLogging;
    }

    @Override
    public T execute(T data) throws SecurityException {
        LocalDateTime startTime = LocalDateTime.now();
        String operation = "SERVICE_EXECUTION";

        try {
            // Log start of operation
            logAuditEntry(operation + "_START", "Service execution started", data);

            // Execute the wrapped service
            T result = super.execute(data);

            // Log successful completion
            LocalDateTime endTime = LocalDateTime.now();
            long executionTime = java.time.Duration.between(startTime, endTime).toMillis();
            logAuditEntry(operation + "_SUCCESS",
                    "Service executed successfully in " + executionTime + "ms", result);

            return result;

        } catch (SecurityException e) {
            // Log security failures
            logAuditEntry(operation + "_SECURITY_FAILURE",
                    "Security violation: " + e.getMessage(), data);
            throw e;

        } catch (Exception e) {
            // Log general failures
            logAuditEntry(operation + "_ERROR",
                    "Service execution failed: " + e.getMessage(), data);
            throw new SecurityException("Service execution failed", e);
        }
    }

    private void logAuditEntry(String eventType, String description, Object data) {
        AuditLogEntry entry = new AuditLogEntry(
                LocalDateTime.now(),
                eventType,
                getServiceName(),
                description,
                detailedLogging ? data.toString() : "Data logging disabled"
        );

        auditLog.offer(entry);

        // Print to console (in real system, would write to secure log file/database)
        System.out.println("AUDIT: " + entry.toString());

        // Keep only last 1000 entries to prevent memory issues
        while (auditLog.size() > 1000) {
            auditLog.poll();
        }
    }

    public ConcurrentLinkedQueue<AuditLogEntry> getAuditLog() {
        return new ConcurrentLinkedQueue<>(auditLog);
    }

    // Inner class for audit log entries
    public static class AuditLogEntry {

        private LocalDateTime timestamp;
        private String eventType;
        private String serviceName;
        private String description;
        private String dataSnapshot;

        public AuditLogEntry(LocalDateTime timestamp, String eventType, String serviceName,
                String description, String dataSnapshot) {
            this.timestamp = timestamp;
            this.eventType = eventType;
            this.serviceName = serviceName;
            this.description = description;
            this.dataSnapshot = dataSnapshot;
        }

        @Override
        public String toString() {
            return String.format("[%s] %s | %s | %s | %s",
                    timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    eventType,
                    serviceName,
                    description,
                    dataSnapshot.length() > 100 ? dataSnapshot.substring(0, 100) + "..." : dataSnapshot);
        }

        // Getters
        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public String getEventType() {
            return eventType;
        }

        public String getServiceName() {
            return serviceName;
        }

        public String getDescription() {
            return description;
        }

        public String getDataSnapshot() {
            return dataSnapshot;
        }
    }
}
