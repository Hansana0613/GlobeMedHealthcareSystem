package com.globmed.service;

import com.globmed.patterns.composite.PermissionLeaf;
import com.globmed.patterns.composite.RoleComponent;
import com.globmed.patterns.composite.RoleComposite;
import com.globmed.patterns.flyweight.PermissionFlyweightFactory;

/**
 *
 * @author Hansana
 */
public class RoleService {

    public RoleComponent createRole(String name) {
        RoleComposite role = new RoleComposite(name);
        role.add(new PermissionLeaf(PermissionFlyweightFactory.getPermission("View Patients", "View patient records").getName()));
        role.add(new PermissionLeaf(PermissionFlyweightFactory.getPermission("Edit Patients", "Edit patient records").getName()));
        return role;
    }

    public boolean checkPermission(RoleComponent role, String permission) {
        return role.hasPermission(permission);
    }
}
