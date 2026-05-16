/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.bus;

import jakarta.inject.Inject;
import com.google.inject.persist.Transactional;
import com.sgu.admissor.dao.RoleDetailDAO;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.RoleDetail;
import com.sgu.admissor.entity.RoleDetailId;
import java.util.List;

/**
 *
 * @author Admin
 */
public class RoleDetailBUS {

    private final RoleDetailDAO roleDetailDAO;

    @Inject
    public RoleDetailBUS(RoleDetailDAO roleDetailDAO) {
        this.roleDetailDAO = roleDetailDAO;
    }

    public BUSResult<List<RoleDetail>> getAllRoleDetail() {
        return BUSResult.successWithData("Lấy toàn bộ chi tiết vai trò thành công!", roleDetailDAO.findAll());
    }

    public BUSResult<RoleDetail> getRoleDetailById(RoleDetailId id) {
        if (id == null) {
            return BUSResult.error("ID chi tiết vai trò không hợp lệ!");
        }
        return BUSResult.successWithData("Lấy chi tiết vai trò thành công!", roleDetailDAO.findByRoleDetailId(id));
    }

    public BUSResult<List<RoleDetail>> getRoleDetailsByRoleId(Integer roleId) {
        if (roleId == null || roleId <= 0) {
            return BUSResult.error("Role ID không hợp lệ!");
        }
        return BUSResult.successWithData("Lấy chi tiết vai trò thành công!", roleDetailDAO.findByRoleId(roleId));
    }

    public BUSResult<List<RoleDetail>> getRoleDetailsByFunctionId(Integer functionId) {
        if (functionId == null || functionId <= 0) {
            return BUSResult.error("Function ID không hợp lệ!");
        }
        return BUSResult.successWithData("Lấy chi tiết vai trò thành công!", roleDetailDAO.findByFunctionId(functionId));
    }

    @Transactional
    public BUSResult<RoleDetail> addRoleDetail(RoleDetail detail) {
        if (detail == null || detail.getId() == null) {
            return BUSResult.error("Thông tin chi tiết vai trò không hợp lệ!");
        }
        if (detail.getId().getRoleId() == null || detail.getId().getRoleId() <= 0) {
            return BUSResult.error("Role ID không hợp lệ!");
        }
        if (detail.getId().getFunctionId() == null || detail.getId().getFunctionId() <= 0) {
            return BUSResult.error("Function ID không hợp lệ!");
        }
        if (detail.getAction() == null || detail.getAction().trim().isEmpty()) {
            return BUSResult.error("Action không được để trống!");
        }
        if (roleDetailDAO.findByRoleDetailId(detail.getId()) != null) {
            return BUSResult.error("Chi tiết vai trò này đã tồn tại!");
        }
        if (roleDetailDAO.insert(detail)) {
            return BUSResult.success("Thêm chi tiết vai trò thành công!");
        }
        return BUSResult.error("Thêm chi tiết vai trò thất bại!");
    }

    @Transactional
    public BUSResult<RoleDetail> updateRoleDetail(RoleDetail detail) {
        if (detail == null || detail.getId() == null) {
            return BUSResult.error("Thông tin chi tiết vai trò không hợp lệ!");
        }
        RoleDetail existing = roleDetailDAO.findByRoleDetailId(detail.getId());
        if (existing == null) {
            return BUSResult.error("Không tìm thấy chi tiết vai trò này trong hệ thống!");
        }
        if (detail.getAction() == null || detail.getAction().trim().isEmpty()) {
            return BUSResult.error("Action không được để trống!");
        }
        existing.setAction(detail.getAction());
        if (roleDetailDAO.update(existing)) {
            return BUSResult.success("Cập nhật chi tiết vai trò thành công!");
        }
        return BUSResult.error("Cập nhật chi tiết vai trò thất bại!");
    }

    @Transactional
    public BUSResult<RoleDetail> deleteRoleDetail(RoleDetail detail) {
        if (detail == null || detail.getId() == null) {
            return BUSResult.error("Thông tin chi tiết vai trò không hợp lệ!");
        }
        RoleDetail existing = roleDetailDAO.findByRoleDetailId(detail.getId());
        if (existing == null) {
            return BUSResult.error("Không tìm thấy chi tiết vai trò này trong hệ thống!");
        }
        if (roleDetailDAO.delete(existing)) {
            return BUSResult.success("Xóa chi tiết vai trò thành công!");
        }
        return BUSResult.error("Xóa chi tiết vai trò thất bại!");
    }
}
