/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.decorator;

/**
 *
 * @author Hansana
 */
public interface SecureService<T> {

    T execute(T data) throws SecurityException;

    String getServiceName();

    void logAccess(String operation, String details);
}
