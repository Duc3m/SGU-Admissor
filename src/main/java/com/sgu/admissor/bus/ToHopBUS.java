/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.bus;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.sgu.admissor.dao.ToHopDAO;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.ToHop;
import java.util.List;

/**
 *
 * @author Admin
 */
public class ToHopBUS {

    private final ToHopDAO toHopDAO;

    @Inject
    public ToHopBUS(ToHopDAO toHopDAO) {
        this.toHopDAO = toHopDAO;
    }

    public BUSResult<List<ToHop>> getAllToHop() {
        return BUSResult.successWithData("Lấy toàn bộ tổ hợp thành công!", toHopDAO.findAll());
    }

    public BUSResult<ToHop> getToHopById(Integer id) {
        if (id == null || id <= 0) {
            return BUSResult.error("ID tổ hợp không hợp lệ!");
        }
        return BUSResult.successWithData("Lấy tổ hợp thành công!", toHopDAO.findById(id));
    }

    public BUSResult<ToHop> getToHopByMaToHop(String maToHop) {
        if (maToHop == null || maToHop.trim().isEmpty()) {
            return BUSResult.error("Mã tổ hợp không hợp lệ!");
        }
        return BUSResult.successWithData("Lấy tổ hợp thành công!", toHopDAO.findByMaToHop(maToHop));
    }

    public BUSResult<List<ToHop>> getToHopByTenToHop(String tenToHop) {
        if (tenToHop == null || tenToHop.trim().isEmpty()) {
            return BUSResult.error("Tên tổ hợp không hợp lệ!");
        }
        return BUSResult.successWithData("Lấy tổ hợp thành công!", toHopDAO.findByTenToHop(tenToHop));
    }

    @Transactional
    public BUSResult<ToHop> addToHop(ToHop toHop) {
        if (toHop == null) {
            return BUSResult.error("Thông tin tổ hợp không hợp lệ!");
        }
        if (toHop.getMaToHop() == null || toHop.getMaToHop().trim().isEmpty()) {
            return BUSResult.error("Mã tổ hợp không được để trống!");
        }
        if (toHop.getMon1() == null || toHop.getMon2() == null || toHop.getMon3() == null
                || toHop.getMon1().trim().isEmpty() || toHop.getMon2().trim().isEmpty() || toHop.getMon3().trim().isEmpty()) {
            return BUSResult.error("Các môn trong tổ hợp không được để trống!");
        }
        if (toHopDAO.findByMaToHop(toHop.getMaToHop()) != null) {
            return BUSResult.error("Mã tổ hợp đã tồn tại trong hệ thống!");
        }
        if (toHopDAO.insert(toHop)) {
            return BUSResult.success("Thêm tổ hợp mới thành công!");
        }
        return BUSResult.error("Thêm tổ hợp thất bại!");
    }
    
    @Transactional
    public BUSResult addListToHop(List<ToHop> listToHop) {
        if (listToHop == null || listToHop.size() == 0) {
            return BUSResult.error("Không có tổ hợp nào để add");
        }
        if (!toHopDAO.insertBatch(listToHop)) {
            return BUSResult.error("Lỗi trong phương thức addListToHop");
        }
        return BUSResult.success("Thêm danh sách tổ hợp thành công!");
    }

    @Transactional
    public BUSResult<ToHop> updateToHop(ToHop toHop) {
        if (toHop == null || toHop.getId() == null || toHop.getId() <= 0) {
            return BUSResult.error("ID tổ hợp không hợp lệ!");
        }
        ToHop existing = toHopDAO.findById(toHop.getId());
        if (existing == null) {
            return BUSResult.error("Không tìm thấy tổ hợp này trong hệ thống!");
        }
        existing.setMaToHop(toHop.getMaToHop());
        existing.setMon1(toHop.getMon1());
        existing.setMon2(toHop.getMon2());
        existing.setMon3(toHop.getMon3());
        existing.setTenToHop(toHop.getTenToHop());

        if (toHopDAO.update(existing)) {
            return BUSResult.success("Cập nhật tổ hợp thành công!");
        }
        return BUSResult.error("Cập nhật tổ hợp thất bại!");
    }

    @Transactional
    public BUSResult<ToHop> deleteToHop(ToHop toHop) {
        if (toHop == null || toHop.getId() == null || toHop.getId() <= 0) {
            return BUSResult.error("ID tổ hợp không hợp lệ!");
        }
        ToHop existing = toHopDAO.findById(toHop.getId());
        if (existing == null) {
            return BUSResult.error("Không tìm thấy tổ hợp này trong hệ thống!");
        }
        if (toHopDAO.delete(existing)) {
            return BUSResult.success("Xóa tổ hợp thành công!");
        }
        return BUSResult.error("Xóa tổ hợp thất bại!");
    }
}
