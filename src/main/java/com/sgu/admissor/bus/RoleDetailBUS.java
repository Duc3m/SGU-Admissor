/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.bus;

import com.google.inject.Inject;
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
        return BUSResult.successWithData("Lấy chi tiết vai trò thành công!", roleDetailDAO.findById(id));
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

    public BUSResult<RoleDetail> addRoleDetail(RoleDetail newRoleDetail) {
        if (newRoleDetail == null || newRoleDetail.getId() == null) {
            return BUSResult.error("Thông tin chi tiết vai trò không hợp lệ!");
        }
        if (newRoleDetail.getId().getRoleId() == null || newRoleDetail.getId().getRoleId() <= 0) {
            return BUSResult.error("Role ID không hợp lệ!");
        }
        if (newRoleDetail.getId().getFunctionId() == null || newRoleDetail.getId().getFunctionId() <= 0) {
            return BUSResult.error("Function ID không hợp lệ!");
        }
        if (newRoleDetail.getAction() == null || newRoleDetail.getAction().trim().isEmpty()) {
            return BUSResult.error("Action không được để trống!");
        }

        if (roleDetailDAO.findById(newRoleDetail.getId()) != null) {
            return BUSResult.error("Chi tiết vai trò này đã tồn tại!");
        }

        boolean isInserted = roleDetailDAO.insert(newRoleDetail);
        if (isInserted) {
            return BUSResult.success("Thêm chi tiết vai trò thành công!");
        } else {
            return BUSResult.error("Thêm chi tiết vai trò thất bại!");
        }
    }

    public BUSResult<RoleDetail> updateRoleDetail(RoleDetail roleDetail) {
        if (roleDetail == null || roleDetail.getId() == null) {
            return BUSResult.error("Thông tin chi tiết vai trò không hợp lệ!");
        }

        RoleDetail existing = roleDetailDAO.findById(roleDetail.getId());
        if (existing == null) {
            return BUSResult.error("Không tìm thấy chi tiết vai trò này trong hệ thống!");
        }

        if (roleDetail.getAction() == null || roleDetail.getAction().trim().isEmpty()) {
            return BUSResult.error("Action không được để trống!");
        }

        existing.setAction(roleDetail.getAction());

        boolean isUpdated = roleDetailDAO.update(existing);
        if (isUpdated) {
            return BUSResult.success("Cập nhật chi tiết vai trò thành công!");
        } else {
            return BUSResult.error("Cập nhật chi tiết vai trò thất bại!");
        }
    }

    public BUSResult<RoleDetail> deleteRoleDetail(RoleDetail roleDetail) {
        if (roleDetail == null || roleDetail.getId() == null) {
            return BUSResult.error("Thông tin chi tiết vai trò không hợp lệ!");
        }

        RoleDetail toDelete = roleDetailDAO.findById(roleDetail.getId());
        if (toDelete == null) {
            return BUSResult.error("Không tìm thấy chi tiết vai trò này trong hệ thống!");
        }

        boolean isDeleted = roleDetailDAO.delete(toDelete);
        if (isDeleted) {
            return BUSResult.success("Xóa chi tiết vai trò thành công!");
        } else {
            return BUSResult.error("Xóa chi tiết vai trò thất bại!");
        }
    }
}
