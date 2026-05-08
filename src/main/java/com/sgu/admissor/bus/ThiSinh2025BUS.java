/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.bus;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.sgu.admissor.dao.ThiSinh2025DAO;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.ThiSinh2025;
import java.util.List;

/**
 *
 * @author Admin
 */
public class ThiSinh2025BUS {

    private final ThiSinh2025DAO thiSinh2025DAO;

    @Inject
    public ThiSinh2025BUS(ThiSinh2025DAO thiSinh2025DAO) {
        this.thiSinh2025DAO = thiSinh2025DAO;
    }

    public BUSResult<List<ThiSinh2025>> getAllThiSinh() {
        return BUSResult.successWithData("Lấy toàn bộ thí sinh thành công!", thiSinh2025DAO.findAll());
    }

    public BUSResult<ThiSinh2025> getThiSinhById(Integer id) {
        if (id == null || id <= 0) {
            return BUSResult.error("ID thí sinh không hợp lệ!");
        }
        return BUSResult.successWithData("Lấy thí sinh thành công!", thiSinh2025DAO.findById(id));
    }

    public BUSResult<ThiSinh2025> getThiSinhByCccd(String cccd) {
        if (cccd == null || cccd.trim().isEmpty()) {
            return BUSResult.error("CCCD không hợp lệ!");
        }
        return BUSResult.successWithData("Lấy thí sinh thành công!", thiSinh2025DAO.findByCccd(cccd));
    }

    public BUSResult<ThiSinh2025> getThiSinhBySoBaoDanh(String soBaoDanh) {
        if (soBaoDanh == null || soBaoDanh.trim().isEmpty()) {
            return BUSResult.error("Số báo danh không hợp lệ!");
        }
        return BUSResult.successWithData("Lấy thí sinh thành công!", thiSinh2025DAO.findBySoBaoDanh(soBaoDanh));
    }

    public BUSResult<List<ThiSinh2025>> getThiSinhByHoTen(String hoTen) {
        if (hoTen == null || hoTen.trim().isEmpty()) {
            return BUSResult.error("Họ tên không hợp lệ!");
        }
        return BUSResult.successWithData("Lấy thí sinh thành công!", thiSinh2025DAO.findByHoTen(hoTen));
    }

    @Transactional
    public BUSResult<ThiSinh2025> addThiSinh(ThiSinh2025 thiSinh) {
        if (thiSinh == null) {
            return BUSResult.error("Thông tin thí sinh không hợp lệ!");
        }
        if (thiSinh.getCccd() == null || thiSinh.getCccd().trim().isEmpty()) {
            return BUSResult.error("CCCD không được để trống!");
        }
        if (thiSinh.getSoBaoDanh() == null || thiSinh.getSoBaoDanh().trim().isEmpty()) {
            return BUSResult.error("Số báo danh không được để trống!");
        }
        if (thiSinh.getHoTen() == null || thiSinh.getHoTen().trim().isEmpty()) {
            return BUSResult.error("Họ tên không được để trống!");
        }
        if (thiSinh2025DAO.findByCccd(thiSinh.getCccd()) != null) {
            return BUSResult.error("CCCD đã tồn tại trong hệ thống!");
        }
        if (thiSinh2025DAO.findBySoBaoDanh(thiSinh.getSoBaoDanh()) != null) {
            return BUSResult.error("Số báo danh đã tồn tại trong hệ thống!");
        }
        if (thiSinh2025DAO.insert(thiSinh)) {
            return BUSResult.success("Thêm thí sinh mới thành công!");
        }
        return BUSResult.error("Thêm thí sinh thất bại!");
    }

    @Transactional
    public BUSResult<ThiSinh2025> updateThiSinh(ThiSinh2025 thiSinh) {
        if (thiSinh == null || thiSinh.getId() == null || thiSinh.getId() <= 0) {
            return BUSResult.error("ID thí sinh không hợp lệ!");
        }
        ThiSinh2025 existing = thiSinh2025DAO.findById(thiSinh.getId());
        if (existing == null) {
            return BUSResult.error("Không tìm thấy thí sinh này trong hệ thống!");
        }
        existing.setCccd(thiSinh.getCccd());
        existing.setSoBaoDanh(thiSinh.getSoBaoDanh());
        existing.setHoTen(thiSinh.getHoTen());
        existing.setNgaySinh(thiSinh.getNgaySinh());
        existing.setDienThoai(thiSinh.getDienThoai());
        existing.setPassword(thiSinh.getPassword());
        existing.setGioiTinh(thiSinh.getGioiTinh());
        existing.setEmail(thiSinh.getEmail());
        existing.setNoiSinh(thiSinh.getNoiSinh());
        existing.setUpdatedAt(thiSinh.getUpdatedAt());
        existing.setDoiTuong(thiSinh.getDoiTuong());
        existing.setKhuVuc(thiSinh.getKhuVuc());

        if (thiSinh2025DAO.update(existing)) {
            return BUSResult.success("Cập nhật thí sinh thành công!");
        }
        return BUSResult.error("Cập nhật thí sinh thất bại!");
    }

    @Transactional
    public BUSResult<ThiSinh2025> deleteThiSinh(ThiSinh2025 thiSinh) {
        if (thiSinh == null || thiSinh.getId() == null || thiSinh.getId() <= 0) {
            return BUSResult.error("ID thí sinh không hợp lệ!");
        }
        ThiSinh2025 existing = thiSinh2025DAO.findById(thiSinh.getId());
        if (existing == null) {
            return BUSResult.error("Không tìm thấy thí sinh này trong hệ thống!");
        }
        if (thiSinh2025DAO.delete(existing)) {
            return BUSResult.success("Xóa thí sinh thành công!");
        }
        return BUSResult.error("Xóa thí sinh thất bại!");
    }
}
