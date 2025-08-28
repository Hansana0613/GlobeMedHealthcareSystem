/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.visitor;

/**
 *
 * @author Hansana
 */
public interface Visitable {

    void accept(ReportVisitor visitor);
}
