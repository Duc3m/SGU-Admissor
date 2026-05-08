/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.bus;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
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
    
    public BUSResult<List<Role>> getAllRole() {
        return BUSResult.successWithData("Lấy toàn bộ Role thành công!", roleDAO.findAll());
    }
    
    @Transactional
    public BUSResult<Role> addRole(Role role) {
        if (role.getName() == null || role.getName().trim().isEmpty()) {
            return BUSResult.error("Tên Role không được để trống!");
        }
        if (roleDAO.findByName(role.getName()) != null) {
            return BUSResult.error("Tên Role '" + role.getName() + "' đã tồn tại!");
        }

        try {
            roleDAO.insert(role);
            return BUSResult.success("Thêm role thành công");
        } catch (Exception e) {
            e.printStackTrace();
            return BUSResult.error("Lỗi phương thức addRole()");
        }
    }
    
}
