package com.globmed.service;

import com.globmed.patterns.composite.PermissionLeaf;
import com.globmed.patterns.composite.RoleComponent;
import com.globmed.patterns.composite.RoleComposite;

/**
 *
 * @author Hansana
 */
public class RoleService {

    public RoleComponent createRole(String name) {
        RoleComposite role = new RoleComposite(name);
        role.add(new PermissionLeaf("View Patients"));
        role.add(new PermissionLeaf("Edit Patients"));
        return role;
    }

    public boolean checkPermission(RoleComponent role, String permission) {
        return role.hasPermission(permission);
    }
}
