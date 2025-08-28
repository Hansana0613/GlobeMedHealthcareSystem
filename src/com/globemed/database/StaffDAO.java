/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.database;

import com.globemed.models.Staff;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Hansana
 */
public class StaffDAO {

    public List<Staff> getAllStaff() throws SQLException {
        List<Staff> staff = new ArrayList<>();
        String sql = "SELECT * FROM staff ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                staff.add(mapResultSetToStaff(rs));
            }
        }

        return staff;
    }

    public Staff getStaffById(Long id) throws SQLException {
        String sql = "SELECT * FROM staff WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStaff(rs);
                }
            }
        }

        return null;
    }

    public Staff getStaffByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM staff WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStaff(rs);
                }
            }
        }

        return null;
    }

    public Long insertStaff(Staff staff) throws SQLException {
        String sql = "INSERT INTO staff (name, role_id, username, password, email) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, staff.getName());
            stmt.setLong(2, staff.getRoleId());
            stmt.setString(3, staff.getUsername());
            stmt.setString(4, staff.getPassword());
            stmt.setString(5, staff.getEmail());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating staff failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Long id = generatedKeys.getLong(1);
                    staff.setId(id);
                    return id;
                } else {
                    throw new SQLException("Creating staff failed, no ID obtained.");
                }
            }
        }
    }

    public boolean updateStaff(Staff staff) throws SQLException {
        String sql = "UPDATE staff SET name = ?, role_id = ?, username = ?, password = ?, email = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, staff.getName());
            stmt.setLong(2, staff.getRoleId());
            stmt.setString(3, staff.getUsername());
            stmt.setString(4, staff.getPassword());
            stmt.setString(5, staff.getEmail());
            stmt.setLong(6, staff.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    private Staff mapResultSetToStaff(ResultSet rs) throws SQLException {
        Staff staff = new Staff();
        staff.setId(rs.getLong("id"));
        staff.setName(rs.getString("name"));
        staff.setRoleId(rs.getLong("role_id"));
        staff.setUsername(rs.getString("username"));
        staff.setPassword(rs.getString("password"));
        staff.setEmail(rs.getString("email"));
        return staff;
    }
}
