/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.database;

import com.globemed.models.Bill;
import com.globemed.models.BillItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Hansana
 */
public class BillDAO {

    public List<Bill> getAllBills() throws SQLException {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM bills ORDER BY id";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Bill bill = mapResultSetToBill(rs);
                //bill.setBillItems(getBillItemsByBillId(bill.getId()));
                bills.add(bill);
            }
            for (Bill bill : bills) {
                bill.setBillItems(getBillItemsByBillId(bill.getId()));
            }
        }

        return bills;
    }

    public Bill getBillById(Long id) throws SQLException {
        String sql = "SELECT * FROM bills WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Bill bill = mapResultSetToBill(rs);
                    bill.setBillItems(getBillItemsByBillId(bill.getId()));
                    return bill;
                }
            }
        }

        return null;
    }

    public List<Bill> getBillsByPatientId(Long patientId) throws SQLException {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT b.* FROM bills b JOIN appointments a ON b.appointment_id = a.id WHERE a.patient_id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, patientId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Bill bill = mapResultSetToBill(rs);
                    bill.setBillItems(getBillItemsByBillId(bill.getId()));
                    bills.add(bill);
                }
            }
        }

        return bills;
    }

    public Long insertBill(Bill bill) throws SQLException {
        String sql = "INSERT INTO bills (appointment_id, total_amount, claim_status, insurance_details) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, bill.getAppointmentId());
            stmt.setBigDecimal(2, bill.getTotalAmount());
            stmt.setString(3, bill.getClaimStatus());
            stmt.setString(4, bill.getInsuranceDetails());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating bill failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Long id = generatedKeys.getLong(1);
                    bill.setId(id);

                    // Insert bill items
                    for (BillItem item : bill.getBillItems()) {
                        insertBillItem(id, item);
                    }

                    return id;
                } else {
                    throw new SQLException("Creating bill failed, no ID obtained.");
                }
            }
        }
    }

    public boolean updateBill(Bill bill) throws SQLException {
        String sql = "UPDATE bills SET appointment_id = ?, total_amount = ?, claim_status = ?, insurance_details = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, bill.getAppointmentId());
            stmt.setBigDecimal(2, bill.getTotalAmount());
            stmt.setString(3, bill.getClaimStatus());
            stmt.setString(4, bill.getInsuranceDetails());
            stmt.setLong(5, bill.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    public List<BillItem> getBillItemsByBillId(Long billId) throws SQLException {
        List<BillItem> items = new ArrayList<>();
        String sql = "SELECT * FROM bill_items WHERE bill_id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, billId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToBillItem(rs));
                }
            }
        }

        return items;
    }

    public Long insertBillItem(Long billId, BillItem item) throws SQLException {
        String sql = "INSERT INTO bill_items (bill_id, item_type, description, cost) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, billId);
            stmt.setString(2, item.getItemType());
            stmt.setString(3, item.getDescription());
            stmt.setBigDecimal(4, item.getCost());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating bill item failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Long id = generatedKeys.getLong(1);
                    item.setId(id);
                    item.setBillId(billId);
                    return id;
                } else {
                    throw new SQLException("Creating bill item failed, no ID obtained.");
                }
            }
        }
    }

    private Bill mapResultSetToBill(ResultSet rs) throws SQLException {
        Bill bill = new Bill();
        bill.setId(rs.getLong("id"));
        bill.setAppointmentId(rs.getLong("appointment_id"));
        bill.setTotalAmount(rs.getBigDecimal("total_amount"));
        bill.setClaimStatus(rs.getString("claim_status"));
        bill.setInsuranceDetails(rs.getString("insurance_details"));
        return bill;
    }

    private BillItem mapResultSetToBillItem(ResultSet rs) throws SQLException {
        BillItem item = new BillItem();
        item.setId(rs.getLong("id"));
        item.setBillId(rs.getLong("bill_id"));
        item.setItemType(rs.getString("item_type"));
        item.setDescription(rs.getString("description"));
        item.setCost(rs.getBigDecimal("cost"));
        return item;
    }
}
