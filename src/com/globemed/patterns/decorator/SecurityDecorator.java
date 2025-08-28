/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.decorator;

/**
 *
 * @author Hansana
 */
public abstract class SecurityDecorator<T> implements SecureService<T> {

    protected SecureService<T> wrappedService;
    protected String decoratorName;

    public SecurityDecorator(SecureService<T> service, String decoratorName) {
        this.wrappedService = service;
        this.decoratorName = decoratorName;
    }

    @Override
    public T execute(T data) throws SecurityException {
        return wrappedService.execute(data);
    }

    @Override
    public String getServiceName() {
        return wrappedService.getServiceName() + " + " + decoratorName;
    }

    @Override
    public void logAccess(String operation, String details) {
        wrappedService.logAccess(operation, details + " [" + decoratorName + "]");
    }
}
