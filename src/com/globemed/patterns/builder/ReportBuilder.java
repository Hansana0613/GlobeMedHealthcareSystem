/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.builder;

import com.globemed.models.Report;
import com.globemed.models.Patient;
import com.globemed.models.Appointment;
import com.globemed.models.Bill;
import java.util.List;

/**
 *
 * @author Hansana
 */
public interface ReportBuilder {

    ReportBuilder setTitle(String title);

    ReportBuilder setPatient(Patient patient);

    ReportBuilder setAppointment(Appointment appointment);

    ReportBuilder addBill(Bill bill);

    ReportBuilder addBills(List<Bill> bills);

    ReportBuilder setSummary(String summary);

    ReportBuilder setFooter(String footer);

    ReportBuilder addCustomSection(String sectionTitle, String content);

    Report build();

    void reset();
}
