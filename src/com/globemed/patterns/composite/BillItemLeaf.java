/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.composite;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author Hansana
 */
public class BillItemLeaf implements BillComponent {

    private String description;
    private BigDecimal cost;
    private String itemType; // CONSULTATION, TREATMENT, MEDICATION, DIAGNOSTIC

    public BillItemLeaf(String description, BigDecimal cost, String itemType) {
        this.description = description;
        this.cost = cost;
        this.itemType = itemType;
    }

    @Override
    public BigDecimal getCost() {
        return cost;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void print(String indent) {
        System.out.println(indent + "- " + description + ": $" + cost + " (" + itemType + ")");
    }

    @Override
    public List<BillComponent> getChildren() {
        return new ArrayList<>(); // Leaf has no children
    }

    @Override
    public void add(BillComponent component) {
        throw new UnsupportedOperationException("Cannot add to leaf component");
    }

    @Override
    public void remove(BillComponent component) {
        throw new UnsupportedOperationException("Cannot remove from leaf component");
    }

    @Override
    public boolean isComposite() {
        return false;
    }

    // Getters
    public String getItemType() {
        return itemType;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }
}
