package com.globmed.model;

import com.globmed.patterns.prototype.Prototype;
import com.globmed.patterns.visitor.Element;
import com.globmed.patterns.visitor.Visitor;
import java.util.Date;

/**
 *
 * @author Hansana
 */
public class Patient implements Element, Prototype {

    private Long id;
    private String name;
    private Date dob;
    private String address;
    private String phone;
    private String medicalHistory;

    // Default constructor
    public Patient() {
    }

    // Getters/Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitPatient(this);
    }

    @Override
    public Prototype clone() {
        Patient clone = new Patient();
        clone.setId(this.id);
        clone.setName(this.name);
        clone.setDob(this.dob != null ? new Date(this.dob.getTime()) : null);
        clone.setAddress(this.address);
        clone.setPhone(this.phone);
        clone.setMedicalHistory(this.medicalHistory);
        return clone;
    }
}
