/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.visitor;

import com.globemed.models.*;

/**
 *
 * @author Hansana
 */
public interface ReportVisitor {

    void visitPatient(Patient patient);

    void visitAppointment(Appointment appointment);

    void visitBill(Bill bill);

    void visitBillItem(BillItem billItem);

    void visitStaff(Staff staff);

    String generateReport();

    void reset();
}
