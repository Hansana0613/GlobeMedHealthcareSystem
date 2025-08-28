/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.decorator;

/**
 *
 * @author Hansana
 */
public class BasicSecureService<T> implements SecureService<T> {

    private String serviceName;

    public BasicSecureService(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public T execute(T data) throws SecurityException {
        System.out.println("BasicSecureService: Processing " + serviceName);
        return data;
    }

    @Override
    public String getServiceName() {
        return serviceName;
    }

    @Override
    public void logAccess(String operation, String details) {
        System.out.println("Basic log: " + operation + " - " + details);
    }
}
