/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.bus;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
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

    public BUSResult<List<NganhToHop>> getAllNganhToHop() {
        return BUSResult.successWithData("Lấy toàn bộ ngành - tổ hợp thành công!", nganhToHopDAO.findAll());
    }

    public BUSResult<NganhToHop> getNganhToHopById(Integer id) {
        if (id == null || id <= 0) {
            return BUSResult.error("ID ngành-tổ hợp không hợp lệ!");
        }
        return BUSResult.successWithData("Lấy ngành-tổ hợp thành công!", nganhToHopDAO.findById(id));
    }

    public BUSResult<List<NganhToHop>> getNganhToHopByMaNganh(String maNganh) {
        if (maNganh == null || maNganh.trim().isEmpty()) {
            return BUSResult.error("Mã ngành không hợp lệ!");
        }
        return BUSResult.successWithData("Lấy ngành-tổ hợp thành công!", nganhToHopDAO.findByMaNganh(maNganh));
    }

    public BUSResult<List<NganhToHop>> getNganhToHopByMaToHop(String maToHop) {
        if (maToHop == null || maToHop.trim().isEmpty()) {
            return BUSResult.error("Mã tổ hợp không hợp lệ!");
        }
        return BUSResult.successWithData("Lấy ngành-tổ hợp thành công!", nganhToHopDAO.findByMaToHop(maToHop));
    }

    @Transactional
    public BUSResult<NganhToHop> addNganhToHop(NganhToHop nganhToHop) {
        if (nganhToHop == null) {
            return BUSResult.error("Thông tin ngành-tổ hợp không hợp lệ!");
        }
        if (nganhToHop.getNganh() == null || nganhToHop.getNganh().getMaNganh() == null
                || nganhToHop.getNganh().getMaNganh().trim().isEmpty()) {
            return BUSResult.error("Mã ngành không được để trống!");
        }
        if (nganhToHop.getToHop() == null || nganhToHop.getToHop().getMaToHop() == null
                || nganhToHop.getToHop().getMaToHop().trim().isEmpty()) {
            return BUSResult.error("Mã tổ hợp không được để trống!");
        }
        String maNganh = nganhToHop.getNganh().getMaNganh();
        String maToHop = nganhToHop.getToHop().getMaToHop();
        if (nganhToHopDAO.findByMaNganhAndMaToHop(maNganh, maToHop) != null) {
            return BUSResult.error("Liên kết ngành - tổ hợp này đã tồn tại!");
        }
        if (nganhToHopDAO.insert(nganhToHop)) {
            return BUSResult.success("Thêm liên kết ngành - tổ hợp thành công!");
        }
        return BUSResult.error("Thêm liên kết ngành - tổ hợp thất bại!");
    }

    @Transactional
    public BUSResult<NganhToHop> updateNganhToHop(NganhToHop nganhToHop) {
        if (nganhToHop == null || nganhToHop.getId() == null || nganhToHop.getId() <= 0) {
            return BUSResult.error("ID ngành-tổ hợp không hợp lệ!");
        }
        NganhToHop existing = nganhToHopDAO.findById(nganhToHop.getId());
        if (existing == null) {
            return BUSResult.error("Không tìm thấy liên kết ngành - tổ hợp này trong hệ thống!");
        }
        existing.setNganh(nganhToHop.getNganh());
        existing.setToHop(nganhToHop.getToHop());
        existing.setHsMon1(nganhToHop.getHsMon1());
        existing.setHsMon2(nganhToHop.getHsMon2());
        existing.setHsMon3(nganhToHop.getHsMon3());
        existing.setTbKey(nganhToHop.getTbKey());
        existing.setN1(nganhToHop.getN1());
        existing.setTo(nganhToHop.getTo());
        existing.setLi(nganhToHop.getLi());
        existing.setHo(nganhToHop.getHo());
        existing.setSi(nganhToHop.getSi());
        existing.setVa(nganhToHop.getVa());
        existing.setSu(nganhToHop.getSu());
        existing.setDi(nganhToHop.getDi());
        existing.setTi(nganhToHop.getTi());
        existing.setKhac(nganhToHop.getKhac());
        existing.setKtpl(nganhToHop.getKtpl());
        existing.setDoLech(nganhToHop.getDoLech());

        if (nganhToHopDAO.update(existing)) {
            return BUSResult.success("Cập nhật liên kết ngành - tổ hợp thành công!");
        }
        return BUSResult.error("Cập nhật liên kết ngành - tổ hợp thất bại!");
    }

    @Transactional
    public BUSResult<NganhToHop> deleteNganhToHop(NganhToHop nganhToHop) {
        if (nganhToHop == null || nganhToHop.getId() == null || nganhToHop.getId() <= 0) {
            return BUSResult.error("ID ngành-tổ hợp không hợp lệ!");
        }
        NganhToHop existing = nganhToHopDAO.findById(nganhToHop.getId());
        if (existing == null) {
            return BUSResult.error("Không tìm thấy liên kết ngành - tổ hợp này trong hệ thống!");
        }
        if (nganhToHopDAO.delete(existing)) {
            return BUSResult.success("Xóa liên kết ngành - tổ hợp thành công!");
        }
        return BUSResult.error("Xóa liên kết ngành - tổ hợp thất bại!");
    }
}
