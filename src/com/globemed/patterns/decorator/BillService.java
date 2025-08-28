/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.decorator;

import com.globemed.patterns.composite.MasterBill;
import java.math.BigDecimal;

/**
 *
 * @author Hansana
 */
public interface BillService {

    MasterBill processBill(MasterBill bill);

    BigDecimal calculateTotal(MasterBill bill);

    void applyModifications(MasterBill bill);
}
