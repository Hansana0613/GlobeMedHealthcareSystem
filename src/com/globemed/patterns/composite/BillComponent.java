/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.composite;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author Hansana
 */
public interface BillComponent {

    BigDecimal getCost();

    String getDescription();

    void print(String indent);

    List<BillComponent> getChildren();

    void add(BillComponent component);

    void remove(BillComponent component);

    boolean isComposite();
}
