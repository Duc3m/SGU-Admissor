/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.bus;

import jakarta.inject.Inject;
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
    
    @Transactional
    public void refresh(ThiSinh2025 thiSinh) {
        thiSinh2025DAO.refresh(thiSinh);
    }

    @Transactional
    public BUSResult<List<ThiSinh2025>> getAllThiSinh() {
        return BUSResult.successWithData("Lấy toàn bộ thí sinh thành công!", thiSinh2025DAO.findAll());
    }
    
    @Transactional
    public BUSResult<Integer> getCount() {
        int count = thiSinh2025DAO.countAll();
        return BUSResult.successWithData("Lấy số lượng toàn bộ thí sinh thành công!", count);
    }

    @Transactional
    public BUSResult<List<ThiSinh2025>> getThiSinhByPage(int page, int limit) {
        return BUSResult.successWithData("Lấy thí sinh theo trang thành công!", thiSinh2025DAO.getByPage(page, limit));
    }
    
    @Transactional
    public int countAdvanced(String tieuChi, String giaTri, String doiTuong, String khuVuc) {
        return thiSinh2025DAO.countAdvanced(tieuChi, giaTri, doiTuong, khuVuc);
    }

    @Transactional
    public BUSResult<List<ThiSinh2025>> searchAdvanced(String tieuChi, String giaTri, String doiTuong, String khuVuc, int page, int limit) {
        List<ThiSinh2025> data = thiSinh2025DAO.searchAdvanced(tieuChi, giaTri, doiTuong, khuVuc, page, limit);
        return BUSResult.successWithData("Lọc thí sinh thành công!", data);
    }
    
    @Transactional
    public BUSResult<ThiSinh2025> getThiSinhById(Integer id) {
        if (id == null || id <= 0) {
            return BUSResult.error("ID thí sinh không hợp lệ!");
        }
        return BUSResult.successWithData("Lấy thí sinh thành công!", thiSinh2025DAO.findById(id));
    }

    @Transactional
    public BUSResult<ThiSinh2025> getThiSinhByCccd(String cccd) {
        if (cccd == null || cccd.trim().isEmpty()) {
            return BUSResult.error("CCCD không hợp lệ!");
        }
        return BUSResult.successWithData("Lấy thí sinh thành công!", thiSinh2025DAO.findByCccd(cccd));
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
    public BUSResult addListThiSinh(List<ThiSinh2025> listThiSinh) {
        if (listThiSinh == null || listThiSinh.size() == 0) {
            return BUSResult.error("Không có thí sinh nào để add");
        }
        if (!thiSinh2025DAO.insertBatch(listThiSinh)) {
            return BUSResult.error("Lỗi trong phương thức addListThiSinh");
        }
        return BUSResult.success("Thêm danh sách thí sinh thành công!");
    }

    @Transactional
    public BUSResult<ThiSinh2025> updateThiSinh(ThiSinh2025 thiSinh) {
        if (thiSinh == null || thiSinh.getId() == null || thiSinh.getId() <= 0) {
            return BUSResult.error("ID thí sinh không hợp lệ!");
        }
        if (thiSinh.getHoTen() == null || thiSinh.getHoTen().trim().isEmpty()) {
            return BUSResult.error("Họ tên không được để trống!");
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
