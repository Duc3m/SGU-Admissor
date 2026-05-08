/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.bus;

import com.google.inject.Inject;
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

    public BUSResult<ToHop> addToHop(ToHop newToHop) {
        if (newToHop == null) {
            return BUSResult.error("Thông tin tổ hợp không hợp lệ!");
        }
        if (newToHop.getMaToHop() == null || newToHop.getMaToHop().trim().isEmpty()) {
            return BUSResult.error("Mã tổ hợp không được để trống!");
        }
        if (newToHop.getMon1() == null || newToHop.getMon1().trim().isEmpty()
                || newToHop.getMon2() == null || newToHop.getMon2().trim().isEmpty()
                || newToHop.getMon3() == null || newToHop.getMon3().trim().isEmpty()) {
            return BUSResult.error("Các môn trong tổ hợp không được để trống!");
        }

        if (toHopDAO.findByMaToHop(newToHop.getMaToHop()) != null) {
            return BUSResult.error("Mã tổ hợp đã tồn tại trong hệ thống!");
        }

        boolean isInserted = toHopDAO.insert(newToHop);
        if (isInserted) {
            return BUSResult.success("Thêm tổ hợp mới thành công!");
        } else {
            return BUSResult.error("Thêm tổ hợp thất bại!");
        }
    }

    public BUSResult<ToHop> updateToHop(ToHop toHop) {
        if (toHop == null || toHop.getId() == null || toHop.getId() <= 0) {
            return BUSResult.error("ID tổ hợp không hợp lệ!");
        }

        ToHop existing = toHopDAO.findById(toHop.getId());
        if (existing == null) {
            return BUSResult.error("Không tìm thấy tổ hợp này trong hệ thống!");
        }

        if (toHop.getTenToHop() != null) existing.setTenToHop(toHop.getTenToHop());
        if (toHop.getMon1() != null && !toHop.getMon1().trim().isEmpty()) existing.setMon1(toHop.getMon1());
        if (toHop.getMon2() != null && !toHop.getMon2().trim().isEmpty()) existing.setMon2(toHop.getMon2());
        if (toHop.getMon3() != null && !toHop.getMon3().trim().isEmpty()) existing.setMon3(toHop.getMon3());

        boolean isUpdated = toHopDAO.update(existing);
        if (isUpdated) {
            return BUSResult.success("Cập nhật tổ hợp thành công!");
        } else {
            return BUSResult.error("Cập nhật tổ hợp thất bại!");
        }
    }

    public BUSResult<ToHop> deleteToHop(ToHop toHop) {
        if (toHop == null || toHop.getId() == null || toHop.getId() <= 0) {
            return BUSResult.error("ID tổ hợp không hợp lệ!");
        }

        ToHop toDelete = toHopDAO.findById(toHop.getId());
        if (toDelete == null) {
            return BUSResult.error("Không tìm thấy tổ hợp này trong hệ thống!");
        }

        boolean isDeleted = toHopDAO.delete(toDelete);
        if (isDeleted) {
            return BUSResult.success("Xóa tổ hợp thành công!");
        } else {
            return BUSResult.error("Xóa tổ hợp thất bại!");
        }
    }
}
