/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.bus;

import com.google.inject.Inject;
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

    public List<ThiSinh2025> getAllThiSinh() {
        return thiSinh2025DAO.findAll();
    }

    public ThiSinh2025 getThiSinhById(Integer id) {
        if (id == null || id <= 0) {
            System.out.println("ID thí sinh không hợp lệ!");
            return null;
        }
        return thiSinh2025DAO.findById(id);
    }

    public ThiSinh2025 getThiSinhByCccd(String cccd) {
        if (cccd == null || cccd.trim().isEmpty()) {
            System.out.println("CCCD không hợp lệ!");
            return null;
        }
        return thiSinh2025DAO.findByCccd(cccd);
    }

    public ThiSinh2025 getThiSinhBySoBaoDanh(String soBaoDanh) {
        if (soBaoDanh == null || soBaoDanh.trim().isEmpty()) {
            System.out.println("Số báo danh không hợp lệ!");
            return null;
        }
        return thiSinh2025DAO.findBySoBaoDanh(soBaoDanh);
    }

    public List<ThiSinh2025> getThiSinhByHoTen(String hoTen) {
        if (hoTen == null || hoTen.trim().isEmpty()) {
            System.out.println("Họ tên không hợp lệ!");
            return null;
        }
        return thiSinh2025DAO.findByHoTen(hoTen);
    }

    public BUSResult<ThiSinh2025> addThiSinh(ThiSinh2025 newThiSinh) {
        if (newThiSinh == null) {
            return BUSResult.error("Thông tin thí sinh không hợp lệ!");
        }
        if (newThiSinh.getCccd() == null || newThiSinh.getCccd().trim().isEmpty()) {
            return BUSResult.error("CCCD không được để trống!");
        }
        if (newThiSinh.getSoBaoDanh() == null || newThiSinh.getSoBaoDanh().trim().isEmpty()) {
            return BUSResult.error("Số báo danh không được để trống!");
        }
        if (newThiSinh.getHoTen() == null || newThiSinh.getHoTen().trim().isEmpty()) {
            return BUSResult.error("Họ tên không được để trống!");
        }

        if (thiSinh2025DAO.findByCccd(newThiSinh.getCccd()) != null) {
            return BUSResult.error("CCCD đã tồn tại trong hệ thống!");
        }
        if (thiSinh2025DAO.findBySoBaoDanh(newThiSinh.getSoBaoDanh()) != null) {
            return BUSResult.error("Số báo danh đã tồn tại trong hệ thống!");
        }

        boolean isInserted = thiSinh2025DAO.insert(newThiSinh);
        if (isInserted) {
            return BUSResult.success("Thêm thí sinh mới thành công!");
        } else {
            return BUSResult.error("Thêm thí sinh thất bại!");
        }
    }

    public BUSResult<ThiSinh2025> updateThiSinh(ThiSinh2025 thiSinh) {
        if (thiSinh == null || thiSinh.getId() == null || thiSinh.getId() <= 0) {
            return BUSResult.error("ID thí sinh không hợp lệ!");
        }

        ThiSinh2025 existing = thiSinh2025DAO.findById(thiSinh.getId());
        if (existing == null) {
            return BUSResult.error("Không tìm thấy thí sinh này trong hệ thống!");
        }

        if (thiSinh.getHoTen() != null && !thiSinh.getHoTen().trim().isEmpty()) {
            existing.setHoTen(thiSinh.getHoTen());
        }
        if (thiSinh.getDienThoai() != null) existing.setDienThoai(thiSinh.getDienThoai());
        if (thiSinh.getEmail() != null) existing.setEmail(thiSinh.getEmail());
        if (thiSinh.getGioiTinh() != null) existing.setGioiTinh(thiSinh.getGioiTinh());
        if (thiSinh.getNoiSinh() != null) existing.setNoiSinh(thiSinh.getNoiSinh());
        if (thiSinh.getNgaySinh() != null) existing.setNgaySinh(thiSinh.getNgaySinh());
        if (thiSinh.getDoiTuong() != null) existing.setDoiTuong(thiSinh.getDoiTuong());
        if (thiSinh.getKhuVuc() != null) existing.setKhuVuc(thiSinh.getKhuVuc());

        boolean isUpdated = thiSinh2025DAO.update(existing);
        if (isUpdated) {
            return BUSResult.success("Cập nhật thí sinh thành công!");
        } else {
            return BUSResult.error("Cập nhật thí sinh thất bại!");
        }
    }

    public BUSResult<ThiSinh2025> deleteThiSinh(ThiSinh2025 thiSinh) {
        if (thiSinh == null || thiSinh.getId() == null || thiSinh.getId() <= 0) {
            return BUSResult.error("ID thí sinh không hợp lệ!");
        }

        ThiSinh2025 toDelete = thiSinh2025DAO.findById(thiSinh.getId());
        if (toDelete == null) {
            return BUSResult.error("Không tìm thấy thí sinh này trong hệ thống!");
        }

        boolean isDeleted = thiSinh2025DAO.delete(toDelete);
        if (isDeleted) {
            return BUSResult.success("Xóa thí sinh thành công!");
        } else {
            return BUSResult.error("Xóa thí sinh thất bại!");
        }
    }
}
