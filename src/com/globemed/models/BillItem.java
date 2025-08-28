/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.models;

import java.math.BigDecimal;

/**
 *
 * @author Hansana
 */
public class BillItem {

    private Long id;
    private Long billId;
    private String itemType; // CONSULTATION, TREATMENT, MEDICATION, DIAGNOSTIC
    private String description;
    private BigDecimal cost;

    // Constructors
    public BillItem() {
    }

    public BillItem(String itemType, String description, BigDecimal cost) {
        this.itemType = itemType;
        this.description = description;
        this.cost = cost;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "BillItem{id=" + id + ", itemType='" + itemType
                + "', description='" + description + "', cost=" + cost + "}";
    }
}
