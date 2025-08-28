/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.mediator;

/**
 *
 * @author Hansana
 */
public abstract class AppointmentComponent {

    protected AppointmentMediator mediator;
    protected String componentId;

    public AppointmentComponent(AppointmentMediator mediator, String componentId) {
        this.mediator = mediator;
        this.componentId = componentId;
    }

    public abstract void notify(String event, Object data);

    public String getComponentId() {
        return componentId;
    }
}
