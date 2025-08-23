package com.globmed.patterns.chain;

import com.globmed.dao.RoleDAO;
import com.globmed.model.Role;

/**
 *
 * @author Hansana
 */
public class RoleHandler extends Handler {

    private RoleDAO roleDAO = new RoleDAO();

    @Override
    public boolean handle(Request request) {
        Role role = roleDAO.findByName(request.getRole());
        if (role == null || !role.getName().equals("Admin") && !role.getName().equals("Doctor")) {
            System.out.println("Insufficient role permissions.");
            return false;
        }
        System.out.println("Role check passed.");
        if (nextHandler != null) {
            return nextHandler.handle(request);
        }
        return true;
    }
}
