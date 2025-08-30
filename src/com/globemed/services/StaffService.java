/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.services;

import com.globemed.database.StaffDAO;
import com.globemed.models.Staff;
import com.globemed.patterns.decorator.SecureService;
import java.sql.SQLException;

public class StaffService {

    private SecureService<Staff> secureService;
    private StaffDAO staffDAO;

    public StaffService(SecureService<Staff> secureService) {
        this.secureService = secureService;
        this.staffDAO = new StaffDAO();
    }

    public boolean createStaff(Staff staff) throws SecurityException, SQLException {
        staff = secureService.execute(staff);
        return staffDAO.insertStaff(staff) != null;
    }

    public boolean updateStaff(Staff staff) throws SecurityException, SQLException {
        staff = secureService.execute(staff);
        return staffDAO.updateStaff(staff);
    }

    public boolean deleteStaff(Long staffId) throws SecurityException, SQLException {
        Staff staff = staffDAO.getStaffById(staffId);
        if (staff != null) {
            staff = secureService.execute(staff);
            return staffDAO.updateStaff(staff); // Soft delete or implement actual delete in DAO
        }
        return false;
    }
}
