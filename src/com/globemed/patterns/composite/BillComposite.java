/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.composite;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Hansana
 */
public class BillComposite implements BillComponent {

    private String description;
    private List<BillComponent> children;

    public BillComposite(String description) {
        this.description = description;
        this.children = new ArrayList<>();
    }

    @Override
    public BigDecimal getCost() {
        BigDecimal totalCost = BigDecimal.ZERO;
        for (BillComponent child : children) {
            totalCost = totalCost.add(child.getCost());
        }
        return totalCost;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void print(String indent) {
        System.out.println(indent + "+ " + description + ": $" + getCost());
        for (BillComponent child : children) {
            child.print(indent + "  ");
        }
    }

    @Override
    public List<BillComponent> getChildren() {
        return new ArrayList<>(children);
    }

    @Override
    public void add(BillComponent component) {
        children.add(component);
    }

    @Override
    public void remove(BillComponent component) {
        children.remove(component);
    }

    @Override
    public boolean isComposite() {
        return true;
    }

    public int getChildCount() {
        return children.size();
    }
}
