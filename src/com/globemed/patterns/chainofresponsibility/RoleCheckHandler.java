/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.chainofresponsibility;

import com.globemed.database.RoleDAO;
import com.globemed.models.Role;
import com.globemed.models.Permission;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Hansana
 */
public class RoleCheckHandler extends AccessHandler {

    private RoleDAO roleDAO;

    public RoleCheckHandler() {
        this.roleDAO = new RoleDAO();
    }

    @Override
    public AccessResult handle(AccessRequest request) {
        try {
            Role userRole = roleDAO.getRoleById(request.getStaff().getRoleId());
            if (userRole == null) {
                return new AccessResult(false, "Role check failed: Invalid role",
                        "ROLE_FAIL: Invalid role for user " + request.getStaff().getUsername());
            }

            // Get permissions for the role
            List<Permission> permissions = roleDAO.getPermissionsForRole(userRole.getId());

            // Check if user has required permission for the action
            String requiredPermission = getRequiredPermission(request.getAction());
            boolean hasPermission = permissions.stream()
                    .anyMatch(p -> p.getName().equals(requiredPermission));

            if (!hasPermission) {
                return new AccessResult(false, "Access denied: Insufficient permissions",
                        "PERM_FAIL: User " + request.getStaff().getUsername()
                        + " lacks " + requiredPermission + " permission");
            }

            System.out.println("Role check passed for user: " + request.getStaff().getUsername()
                    + " with role: " + userRole.getName());
            return passToNext(request);

        } catch (SQLException e) {
            return new AccessResult(false, "Role check failed: Database error",
                    "DB_ERROR: " + e.getMessage());
        }
    }

    private String getRequiredPermission(String action) {
        switch (action.toUpperCase()) {
            case "VIEW":
                return "VIEW_PATIENT_RECORDS";
            case "EDIT":
                return "EDIT_PATIENT_RECORDS";
            case "CREATE":
                return "EDIT_PATIENT_RECORDS";
            case "DELETE":
                return "EDIT_PATIENT_RECORDS";
            default:
                return "VIEW_PATIENT_RECORDS";
        }
    }
}
