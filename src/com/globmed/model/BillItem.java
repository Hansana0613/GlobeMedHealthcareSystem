package com.globmed.model;

/**
 *
 * @author Hansana
 */
public class BillItem {

    private Long id;
    private Bill bill;
    private ItemType itemType;
    private String description;
    private Double cost;

    public enum ItemType {
        CONSULTATION, TREATMENT, MEDICATION, DIAGNOSTIC
    }

    // Default constructor
    public BillItem() {
    }

    // Getters/Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }
}
