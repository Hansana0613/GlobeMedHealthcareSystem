/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.decorator;

import com.globemed.patterns.flyweight.PermissionFlyweight;
import com.globemed.patterns.flyweight.PermissionFlyweightFactory;
import java.util.Set;

/**
 *
 * @author Hansana
 */
public class AccessControlDecorator<T> extends SecurityDecorator<T> {

    private String requiredPermission;
    private Set<String> userPermissions;
    private PermissionFlyweightFactory permissionFactory;

    public AccessControlDecorator(SecureService<T> service, String requiredPermission, Set<String> userPermissions) {
        super(service, "AccessControl");
        this.requiredPermission = requiredPermission;
        this.userPermissions = userPermissions;
        this.permissionFactory = PermissionFlyweightFactory.getInstance();
    }

    @Override
    public T execute(T data) throws SecurityException {
        // Use flyweight pattern to get permission object
        PermissionFlyweight permission = permissionFactory.getPermission(requiredPermission);

        if (!hasRequiredPermission(permission)) {
            logAccess("ACCESS_DENIED",
                    "User lacks required permission: " + requiredPermission);
            throw new SecurityException("Access denied - insufficient permissions");
        }

        logAccess("ACCESS_GRANTED",
                "User has required permission: " + requiredPermission);
        return super.execute(data);
    }

    private boolean hasRequiredPermission(PermissionFlyweight permission) {
        return userPermissions != null
                && userPermissions.contains(permission.getPermissionName());
    }

    public void updateUserPermissions(Set<String> newPermissions) {
        this.userPermissions = newPermissions;
        logAccess("PERMISSIONS_UPDATED", "User permissions refreshed");
    }
}
