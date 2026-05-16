/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.bus;

import jakarta.inject.Inject;
import com.google.inject.persist.Transactional;
import com.sgu.admissor.dao.SysFunctionDAO;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.SysFunction;
import java.util.List;

/**
 *
 * @author Admin
 */
public class SysFunctionBUS {

    private final SysFunctionDAO sysFunctionDAO;

    @Inject
    public SysFunctionBUS(SysFunctionDAO sysFunctionDAO) {
        this.sysFunctionDAO = sysFunctionDAO;
    }

    public BUSResult<List<SysFunction>> getAllSysFunction() {
        return BUSResult.successWithData("Lấy toàn bộ function thành công!", sysFunctionDAO.findAll());
    }

    public BUSResult<SysFunction> getSysFunctionByID(Integer id) {
        if (id == null || id <= 0) {
            return BUSResult.error("Function ID không hợp lê!");
        }
        return BUSResult.successWithData("Lấy function thành công!", sysFunctionDAO.findById(id));
    }

    @Transactional
    public BUSResult<SysFunction> addSysFunction(SysFunction function) {
        if (function == null || function.getName() == null || function.getName().trim().isEmpty()) {
            return BUSResult.error("Tên Function không được để trống!");
        }
        if (sysFunctionDAO.findByName(function.getName()) != null) {
            return BUSResult.error("Tên Function '" + function.getName() + "' đã tồn tại!");
        }
        if (sysFunctionDAO.insert(function)) {
            return BUSResult.success("Thêm Function '" + function.getName() + "' thành công!");
        }
        return BUSResult.error("Thêm Function thất bại!");
    }

    @Transactional
    public BUSResult deleteSysFunction(SysFunction function) {
        if (function == null || function.getId() == null) {
            return BUSResult.error("Không tìm thấy Function!");
        }
        if (sysFunctionDAO.delete(function)) {
            return BUSResult.success("Xóa Function '" + function.getName() + "' thành công!");
        }
        return BUSResult.error("Xóa Function thất bại!");
    }
}
