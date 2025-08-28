/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.database;

import com.globemed.models.Role;
import com.globemed.models.Permission;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Hansana
 */
public class RoleDAO {

    public List<Role> getAllRoles() throws SQLException {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT * FROM roles ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Role role = mapResultSetToRole(rs);
                role.setPermissions(getPermissionsForRole(role.getId()));
                roles.add(role);
            }
        }

        return roles;
    }

    public Role getRoleById(Long id) throws SQLException {
        String sql = "SELECT * FROM roles WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Role role = mapResultSetToRole(rs);
                    role.setPermissions(getPermissionsForRole(role.getId()));
                    return role;
                }
            }
        }

        return null;
    }

    public List<String> getAllRoleNames() throws SQLException {
        List<String> roleNames = new ArrayList<>();
        String sql = "SELECT name FROM roles ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                roleNames.add(rs.getString("name"));
            }
        }

        return roleNames;
    }

    public List<Permission> getPermissionsForRole(Long roleId) throws SQLException {
        List<Permission> permissions = new ArrayList<>();
        String sql = "SELECT p.* FROM permissions p "
                + "JOIN role_permissions rp ON p.id = rp.permission_id "
                + "WHERE rp.role_id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, roleId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    permissions.add(mapResultSetToPermission(rs));
                }
            }
        }

        return permissions;
    }

    public List<Permission> getPermissionsByRoleName(String roleName) throws SQLException {
        List<Permission> permissions = new ArrayList<>();
        String sql = "SELECT p.* FROM permissions p "
                + "JOIN role_permissions rp ON p.id = rp.permission_id "
                + "JOIN roles r ON rp.role_id = r.id "
                + "WHERE r.name = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, roleName);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    permissions.add(mapResultSetToPermission(rs));
                }
            }
        }

        return permissions;
    }

    public List<Permission> getAllPermissions() throws SQLException {
        List<Permission> permissions = new ArrayList<>();
        String sql = "SELECT * FROM permissions ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                permissions.add(mapResultSetToPermission(rs));
            }
        }

        return permissions;
    }

    public void addPermissionToRole(String roleName, String permissionName) throws SQLException {
        String sql = "INSERT INTO role_permissions (role_id, permission_id) "
                + "SELECT r.id, p.id FROM roles r, permissions p "
                + "WHERE r.name = ? AND p.name = ? "
                + "AND NOT EXISTS (SELECT 1 FROM role_permissions rp2 "
                + "WHERE rp2.role_id = r.id AND rp2.permission_id = p.id)";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, roleName);
            stmt.setString(2, permissionName);
            stmt.executeUpdate();
        }
    }

    public void removePermissionFromRole(String roleName, String permissionName) throws SQLException {
        String sql = "DELETE FROM role_permissions "
                + "WHERE role_id = (SELECT id FROM roles WHERE name = ?) "
                + "AND permission_id = (SELECT id FROM permissions WHERE name = ?)";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, roleName);
            stmt.setString(2, permissionName);
            stmt.executeUpdate();
        }
    }

    private Role mapResultSetToRole(ResultSet rs) throws SQLException {
        Role role = new Role();
        role.setId(rs.getLong("id"));
        role.setName(rs.getString("name"));
        role.setParentRoleId(rs.getObject("parent_role_id", Long.class));
        return role;
    }

    private Permission mapResultSetToPermission(ResultSet rs) throws SQLException {
        Permission permission = new Permission();
        permission.setId(rs.getLong("id"));
        permission.setName(rs.getString("name"));
        permission.setDescription(rs.getString("description"));
        return permission;
    }
}
