/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.globemed.patterns.observer;

/**
 *
 * @author Hansana
 */
public interface Subject {

    void attach(Observer observer);

    void detach(Observer observer);

    void notifyObservers();
}
