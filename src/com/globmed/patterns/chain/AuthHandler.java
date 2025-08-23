package com.globmed.patterns.chain;

import com.globmed.dao.StaffDAO;
import com.globmed.model.Staff;

/**
 *
 * @author Hansana
 */
public class AuthHandler extends Handler {

    private StaffDAO staffDAO = new StaffDAO();

    @Override
    public boolean handle(Request request) {
        Staff staff = staffDAO.findByUsername(request.getUsername());
        if (staff == null || !staff.getPassword().equals("hashedpass")) { // Replace with real auth
            System.out.println("Authentication failed.");
            return false;
        }
        System.out.println("Authentication passed.");
        if (nextHandler != null) {
            return nextHandler.handle(request);
        }
        return true;
    }
}
