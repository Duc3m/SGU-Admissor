/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.bus;

import com.google.inject.Inject;
import com.sgu.admissor.dao.NganhToHopDAO;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.NganhToHop;
import java.util.List;

/**
 *
 * @author Admin
 */
public class NganhToHopBUS {
    private final NganhToHopDAO nganhToHopDAO;

    @Inject
    public NganhToHopBUS(NganhToHopDAO nganhToHopDAO) {
        this.nganhToHopDAO = nganhToHopDAO;
    }

    public List<NganhToHop> getAllNganhToHop() {
        return nganhToHopDAO.findAll();
    }

    public NganhToHop getNganhToHopById(Integer id) {
        if (id == null || id <= 0) {
            System.out.println("ID ngành-tổ hợp không hợp lệ!");
            return null;
        }
        return nganhToHopDAO.findById(id);
    }

    public List<NganhToHop> getNganhToHopByMaNganh(String maNganh) {
        if (maNganh == null || maNganh.trim().isEmpty()) {
            System.out.println("Mã ngành không hợp lệ!");
            return null;
        }
        return nganhToHopDAO.findByMaNganh(maNganh);
    }

    public List<NganhToHop> getNganhToHopByMaToHop(String maToHop) {
        if (maToHop == null || maToHop.trim().isEmpty()) {
            System.out.println("Mã tổ hợp không hợp lệ!");
            return null;
        }
        return nganhToHopDAO.findByMaToHop(maToHop);
    }

    public BUSResult<NganhToHop> addNganhToHop(NganhToHop newNganhToHop) {
        if (newNganhToHop == null) {
            return BUSResult.error("Thông tin ngành-tổ hợp không hợp lệ!");
        }
        if (newNganhToHop.getNganh() == null || newNganhToHop.getNganh().getMaNganh() == null
                || newNganhToHop.getNganh().getMaNganh().trim().isEmpty()) {
            return BUSResult.error("Mã ngành không được để trống!");
        }
        if (newNganhToHop.getToHop() == null || newNganhToHop.getToHop().getMaToHop() == null
                || newNganhToHop.getToHop().getMaToHop().trim().isEmpty()) {
            return BUSResult.error("Mã tổ hợp không được để trống!");
        }

        NganhToHop existing = nganhToHopDAO.findByMaNganhAndMaToHop(
                newNganhToHop.getNganh().getMaNganh(),
                newNganhToHop.getToHop().getMaToHop());
        if (existing != null) {
            return BUSResult.error("Liên kết ngành - tổ hợp này đã tồn tại!");
        }

        boolean isInserted = nganhToHopDAO.insert(newNganhToHop);
        if (isInserted) {
            return BUSResult.success("Thêm liên kết ngành - tổ hợp thành công!");
        } else {
            return BUSResult.error("Thêm liên kết ngành - tổ hợp thất bại!");
        }
    }

    public BUSResult<NganhToHop> updateNganhToHop(NganhToHop nganhToHop) {
        if (nganhToHop == null || nganhToHop.getId() == null || nganhToHop.getId() <= 0) {
            return BUSResult.error("ID ngành-tổ hợp không hợp lệ!");
        }

        NganhToHop existing = nganhToHopDAO.findById(nganhToHop.getId());
        if (existing == null) {
            return BUSResult.error("Không tìm thấy liên kết ngành - tổ hợp này trong hệ thống!");
        }

        if (nganhToHop.getHsMon1() != null) existing.setHsMon1(nganhToHop.getHsMon1());
        if (nganhToHop.getHsMon2() != null) existing.setHsMon2(nganhToHop.getHsMon2());
        if (nganhToHop.getHsMon3() != null) existing.setHsMon3(nganhToHop.getHsMon3());
        if (nganhToHop.getDoLech() != null) existing.setDoLech(nganhToHop.getDoLech());
        if (nganhToHop.getTbKey() != null) existing.setTbKey(nganhToHop.getTbKey());

        boolean isUpdated = nganhToHopDAO.update(existing);
        if (isUpdated) {
            return BUSResult.success("Cập nhật liên kết ngành - tổ hợp thành công!");
        } else {
            return BUSResult.error("Cập nhật liên kết ngành - tổ hợp thất bại!");
        }
    }

    public BUSResult<NganhToHop> deleteNganhToHop(NganhToHop nganhToHop) {
        if (nganhToHop == null || nganhToHop.getId() == null || nganhToHop.getId() <= 0) {
            return BUSResult.error("ID ngành-tổ hợp không hợp lệ!");
        }

        NganhToHop toDelete = nganhToHopDAO.findById(nganhToHop.getId());
        if (toDelete == null) {
            return BUSResult.error("Không tìm thấy liên kết ngành - tổ hợp này trong hệ thống!");
        }

        boolean isDeleted = nganhToHopDAO.delete(toDelete);
        if (isDeleted) {
            return BUSResult.success("Xóa liên kết ngành - tổ hợp thành công!");
        } else {
            return BUSResult.error("Xóa liên kết ngành - tổ hợp thất bại!");
        }
    }
}
