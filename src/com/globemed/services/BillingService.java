package com.globemed.services;

import com.globemed.database.BillDAO;
import com.globemed.database.AppointmentDAO;
import com.globemed.models.Bill;
import com.globemed.models.BillItem;
import com.globemed.models.Appointment;
import com.globemed.patterns.composite.*;
import com.globemed.patterns.chainofresponsibility.*;
import com.globemed.patterns.decorator.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;

public class BillingService {

    private BillDAO billDAO;
    private AppointmentDAO appointmentDAO;
    private ClaimHandler claimChain;

    public BillingService() {
        this.billDAO = new BillDAO();
        this.appointmentDAO = new AppointmentDAO();
        initializeClaimChain();
    }

    private void initializeClaimChain() {
        // Setup Chain of Responsibility for claim processing
        HospitalApprovalHandler hospitalHandler = new HospitalApprovalHandler();
        InsuranceValidationHandler insuranceHandler = new InsuranceValidationHandler();
        FinalApprovalHandler finalHandler = new FinalApprovalHandler();

        hospitalHandler.setNext(insuranceHandler);
        insuranceHandler.setNext(finalHandler);

        this.claimChain = hospitalHandler;
    }

    public List<Bill> getAllBills() throws SQLException {
        return billDAO.getAllBills();
    }

    public Bill getBillById(Long id) throws SQLException {
        return billDAO.getBillById(id);
    }

    public List<Bill> getBillsByPatientId(Long patientId) throws SQLException {
        return billDAO.getBillsByPatientId(patientId);
    }

    public List<Bill> getBillsByDateRange(Long patientId, LocalDate startDate, LocalDate endDate) throws SQLException {
        return billDAO.getBillsByPatientIdAndDateRange(patientId, startDate, endDate);
    }

    public MasterBill createMasterBill(Long appointmentId, String insuranceDetails) throws SQLException {
        // Validate appointment ID
        if (appointmentId == null) {
            throw new SQLException("Appointment ID cannot be null");
        }
        
        // Get appointment details
        Appointment appointment = appointmentDAO.getAppointmentById(appointmentId);
        if (appointment == null) {
            throw new SQLException("Appointment not found: " + appointmentId);
        }

        // Create MasterBill using Composite pattern
        MasterBill masterBill = new MasterBill(null, appointmentId, insuranceDetails);
        
        // Debug: Print the MasterBill details after creation
        System.out.println("DEBUG MASTERBILL: Created with appointmentId: " + masterBill.getAppointmentId());
        System.out.println("DEBUG MASTERBILL: Created with status: " + masterBill.getStatus());
        System.out.println("DEBUG MASTERBILL: Created with insuranceDetails: " + masterBill.getInsuranceDetails());

        // Add default bill items based on appointment
        addDefaultBillItems(masterBill, appointment);

        // Save to database
        Bill dbBill = convertToDbBill(masterBill);
        
        // Validate that all required fields are set
        if (dbBill.getAppointmentId() == null) {
            throw new SQLException("Appointment ID is null in converted bill");
        }
        if (dbBill.getTotalAmount() == null) {
            throw new SQLException("Total amount is null in converted bill");
        }
        if (dbBill.getClaimStatus() == null) {
            throw new SQLException("Claim status is null in converted bill");
        }
        
        // Debug: Print the bill details before insertion
        System.out.println("DEBUG: Creating bill with appointment ID: " + dbBill.getAppointmentId());
        System.out.println("DEBUG: Bill total amount: " + dbBill.getTotalAmount());
        System.out.println("DEBUG: Bill status: " + dbBill.getClaimStatus());
        System.out.println("DEBUG: Bill insurance: " + dbBill.getInsuranceDetails());
        
        Long billId = billDAO.insertBill(dbBill);
        
        // Set the bill ID in the master bill
        masterBill.setBillId(billId);
        
        // The bill items are already inserted by the DAO during bill insertion
        // No need to insert them separately here

        return masterBill;
    }

    private void addDefaultBillItems(MasterBill masterBill, Appointment appointment) {
        // Add consultation fee
        BillItemLeaf consultation = new BillItemLeaf(
                "Medical Consultation",
                new BigDecimal("150.00"),
                "CONSULTATION"
        );
        masterBill.add(consultation);

        // Add basic examination fee
        BillItemLeaf examination = new BillItemLeaf(
                "Physical Examination",
                new BigDecimal("75.00"),
                "TREATMENT"
        );
        masterBill.add(examination);
        
        // Debug: Print the MasterBill details after adding items
        System.out.println("DEBUG ITEMS: MasterBill cost after adding items: " + masterBill.getCost());
        System.out.println("DEBUG ITEMS: MasterBill children count: " + masterBill.getChildren().size());
    }

    public MasterBill processComplexBill(MasterBill masterBill, boolean applyDiscount,
            boolean addTax, boolean checkLateFees) {
        // Use Decorator pattern for dynamic bill processing
        BillService service = new BasicBillService();

        // Apply discount if needed
        if (applyDiscount) {
            service = new DiscountDecorator(service, new BigDecimal("10.0"), "Senior Citizen Discount");
        }

        // Apply tax if needed
        if (addTax) {
            service = new TaxDecorator(service, new BigDecimal("8.25"), "State Tax");
        }

        // Apply late fees if needed
        if (checkLateFees) {
            service = new LateFeeDecorator(service, 30, new BigDecimal("1.5"));
        }

        // Process the bill
        MasterBill processedBill = service.processBill(masterBill);
        
        // Update the database with the new total amount
        try {
            if (processedBill.getBillId() != null) {
                Bill dbBill = billDAO.getBillById(processedBill.getBillId());
                if (dbBill != null) {
                    dbBill.setTotalAmount(processedBill.getCost());
                    billDAO.updateBill(dbBill);
                }
            }
        } catch (SQLException e) {
            // Log the error but don't fail the operation
            System.err.println("Warning: Could not update bill total in database: " + e.getMessage());
        }
        
        return processedBill;
    }

    public ClaimResult processClaim(MasterBill masterBill, String claimType,
            String insuranceProvider, String policyNumber) {
        ClaimRequest request = new ClaimRequest(masterBill, claimType, insuranceProvider, policyNumber);
        return claimChain.handle(request);
    }

    public void addBillItem(Long billId, String itemType, String description, BigDecimal cost) throws SQLException {
        // Create and insert the bill item
        BillItem item = new BillItem(itemType, description, cost);
        billDAO.insertBillItem(billId, item);
        
        // Get the updated bill with all items
        Bill bill = billDAO.getBillById(billId);
        if (bill != null && bill.getBillItems() != null) {
            // Calculate new total from all items
            BigDecimal newTotal = BigDecimal.ZERO;
            for (BillItem billItem : bill.getBillItems()) {
                newTotal = newTotal.add(billItem.getCost());
            }
            
            // Update the bill's total amount in the database
            bill.setTotalAmount(newTotal);
            billDAO.updateBill(bill);
        }
    }

    public boolean updateBillStatus(Long billId, String status) throws SQLException {
        Bill bill = billDAO.getBillById(billId);
        if (bill != null) {
            bill.setClaimStatus(status);
            return billDAO.updateBill(bill);
        }
        return false;
    }

    public MasterBill convertFromDbBill(Bill dbBill) {
        MasterBill masterBill = new MasterBill(
                dbBill.getId(),
                dbBill.getAppointmentId(),
                dbBill.getInsuranceDetails()
        );

        // Convert bill items to composite structure
        for (BillItem item : dbBill.getBillItems()) {
            BillItemLeaf leafItem = new BillItemLeaf(
                    item.getDescription(),
                    item.getCost(),
                    item.getItemType()
            );
            masterBill.add(leafItem);
        }

        masterBill.setStatus(dbBill.getClaimStatus());
        return masterBill;
    }

    private Bill convertToDbBill(MasterBill masterBill) {
        Bill dbBill = new Bill();
        dbBill.setId(masterBill.getBillId());
        dbBill.setAppointmentId(masterBill.getAppointmentId());
        dbBill.setTotalAmount(masterBill.getCost());
        dbBill.setClaimStatus(masterBill.getStatus());
        dbBill.setInsuranceDetails(masterBill.getInsuranceDetails());
        
        // Debug: Print the conversion details
        System.out.println("DEBUG CONVERT: MasterBill appointmentId: " + masterBill.getAppointmentId());
        System.out.println("DEBUG CONVERT: MasterBill status: " + masterBill.getStatus());
        System.out.println("DEBUG CONVERT: MasterBill cost: " + masterBill.getCost());
        System.out.println("DEBUG CONVERT: Converted Bill appointmentId: " + dbBill.getAppointmentId());
        System.out.println("DEBUG CONVERT: Converted Bill status: " + dbBill.getClaimStatus());
        System.out.println("DEBUG CONVERT: Converted Bill totalAmount: " + dbBill.getTotalAmount());

        // Convert composite items to bill items
        List<BillItem> billItems = new ArrayList<>();
        for (BillComponent component : masterBill.getChildren()) {
            if (component instanceof BillItemLeaf) {
                BillItemLeaf leaf = (BillItemLeaf) component;
                BillItem item = new BillItem(
                        leaf.getItemType(),
                        leaf.getDescription(),
                        leaf.getCost()
                );
                billItems.add(item);
            }
        }
        dbBill.setBillItems(billItems);

        return dbBill;
    }

    public List<Bill> searchBills(String claimStatus, LocalDate fromDate, LocalDate toDate) throws SQLException {
        // This would require additional DAO methods for complex searches
        List<Bill> allBills = billDAO.getAllBills();
        List<Bill> filteredBills = new ArrayList<>();

        for (Bill bill : allBills) {
            boolean matches = true;

            if (claimStatus != null && !claimStatus.isEmpty()) {
                matches = matches && claimStatus.equals(bill.getClaimStatus());
            }

            // Additional date filtering would require appointment data
            if (matches) {
                filteredBills.add(bill);
            }
        }

        return filteredBills;
    }

    public BigDecimal calculateTotalRevenue() throws SQLException {
        List<Bill> allBills = billDAO.getAllBills();
        BigDecimal total = BigDecimal.ZERO;

        for (Bill bill : allBills) {
            if ("PAID".equals(bill.getClaimStatus())) {
                total = total.add(bill.getTotalAmount());
            }
        }

        return total;
    }

    public void deleteBill(Long billId) throws SQLException {
        // Note: This would require a delete method in BillDAO
        // For now, we'll mark as cancelled
        updateBillStatus(billId, "CANCELLED");
    }

    /**
     * Refresh bill data from database to ensure UI consistency
     */
    public Bill refreshBillFromDatabase(Long billId) throws SQLException {
        return billDAO.getBillById(billId);
    }
    
    /**
     * Refresh all bill data and recalculate totals to ensure database consistency
     */
    public void refreshAllBillData() throws SQLException {
        List<Bill> allBills = billDAO.getAllBills();
        
        for (Bill bill : allBills) {
            if (bill.getBillItems() != null && !bill.getBillItems().isEmpty()) {
                // Recalculate total from bill items
                BigDecimal newTotal = BigDecimal.ZERO;
                for (BillItem item : bill.getBillItems()) {
                    newTotal = newTotal.add(item.getCost());
                }
                
                // Update if total has changed
                if (newTotal.compareTo(bill.getTotalAmount()) != 0) {
                    bill.setTotalAmount(newTotal);
                    billDAO.updateBill(bill);
                }
            }
        }
    }
}
