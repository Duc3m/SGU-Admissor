/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.bus;

import com.google.inject.Inject;
import com.sgu.admissor.dao.RoleDAO;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.Role;
import java.util.List;

/**
 *
 * @author Admin
 */

public class RoleBUS {
    
    private final RoleDAO roleDAO;

    @Inject
    public RoleBUS(RoleDAO roleDAO) {
        this.roleDAO = roleDAO;
    }

    public BUSResult<List<Role>> getAllRoles() {
        return BUSResult.successWithData("Lấy toàn bộ role thành công!", roleDAO.findAll());
    }

    public BUSResult<Role> getRoleById(Integer id) {
        if (id == null || id <= 0) {
            return BUSResult.error("ID role không hợp lệ!");
        }
        return BUSResult.successWithData("Lấy role thành công!", roleDAO.findById(id));
    }

    public BUSResult<Role> addRole(Role role) {
        if (role.getName() == null || role.getName().trim().isEmpty()) {
            return BUSResult.error("Tên Role không được để trống!");
        }
        
        if (roleDAO.findByName(role.getName()) != null) {
            return BUSResult.error("Tên Role '" + role.getName() + "' đã tồn tại!");
        }
        
        boolean isInserted = roleDAO.insert(role);
        
        if(isInserted) {
            return BUSResult.success("Thêm role '" + role.getName() + "' thành công");
        } else {
            return BUSResult.error("Lỗi gì đó ở phương thức addRole() rồi ku");
        }
    }
    
    public BUSResult deleteRole(Role role) {
        if (role == null || role.getId() == null) {
            return BUSResult.error("Không tìm thấy role!");
        }
        boolean isDeleted = roleDAO.delete(role);
        
        if(isDeleted) {
            return BUSResult.success("Xoá role '" + role.getName() + "' thành công");
        } else {
            return BUSResult.error("Lỗi gì đó ở phương thức deleteRole() rồi ku");
        }
    }
    
}
