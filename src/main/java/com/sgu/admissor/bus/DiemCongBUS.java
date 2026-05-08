/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.bus;

import com.google.inject.Inject;
import com.sgu.admissor.dao.DiemCongDAO;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.DiemCong;
import java.util.List;

/**
 *
 * @author Admin
 */
public class DiemCongBUS {
    private final DiemCongDAO diemCongDAO;

    @Inject
    public DiemCongBUS(DiemCongDAO diemCongDAO) {
        this.diemCongDAO = diemCongDAO;
    }

    public BUSResult<List<DiemCong>> getAllDiemCong() {
        return BUSResult.successWithData("Lấy toàn bộ điểm cộng thành công!", diemCongDAO.findAll());
    }

    public BUSResult<DiemCong> getDiemCongById(Integer id) {
        if (id == null || id <= 0) {
            return BUSResult.error("ID điểm cộng không hợp lệ!");
        }
        return BUSResult.successWithData("Lấy điểm cộng thành công!", diemCongDAO.findById(id));
    }

    public BUSResult<List<DiemCong>> getDiemCongByCccd(String cccd) {
        if (cccd == null || cccd.trim().isEmpty()) {
            return BUSResult.error("CCCD không hợp lệ!");
        }
        return BUSResult.successWithData("Lấy điểm cộng thành công!", diemCongDAO.findByCccd(cccd));
    }

    public BUSResult<DiemCong> getDiemCongByDcKey(String dcKey) {
        if (dcKey == null || dcKey.trim().isEmpty()) {
            return BUSResult.error("DC key không hợp lệ!");
        }
        return BUSResult.successWithData("Lấy điểm cộng thành công!", diemCongDAO.findByDcKey(dcKey));
    }

    public BUSResult<List<DiemCong>> getDiemCongByMaNganh(String maNganh) {
        if (maNganh == null || maNganh.trim().isEmpty()) {
            return BUSResult.error("Mã ngành không hợp lệ!");
        }
        return BUSResult.successWithData("Lấy điểm cộng thành công!", diemCongDAO.findByMaNganh(maNganh));
    }

    public BUSResult<DiemCong> addDiemCong(DiemCong newDiemCong) {
        if (newDiemCong == null) {
            return BUSResult.error("Thông tin điểm cộng không hợp lệ!");
        }
        if (newDiemCong.getThiSinh() == null || newDiemCong.getThiSinh().getCccd() == null
                || newDiemCong.getThiSinh().getCccd().trim().isEmpty()) {
            return BUSResult.error("Thông tin thí sinh không hợp lệ!");
        }
        if (newDiemCong.getNganh() == null || newDiemCong.getNganh().getMaNganh() == null
                || newDiemCong.getNganh().getMaNganh().trim().isEmpty()) {
            return BUSResult.error("Mã ngành không được để trống!");
        }
        if (newDiemCong.getDcKey() != null && !newDiemCong.getDcKey().trim().isEmpty()
                && diemCongDAO.findByDcKey(newDiemCong.getDcKey()) != null) {
            return BUSResult.error("Điểm cộng này đã tồn tại (trùng dc_key)!");
        }

        boolean isInserted = diemCongDAO.insert(newDiemCong);
        if (isInserted) {
            return BUSResult.success("Thêm điểm cộng thành công!");
        } else {
            return BUSResult.error("Thêm điểm cộng thất bại!");
        }
    }

    public BUSResult<DiemCong> updateDiemCong(DiemCong diemCong) {
        if (diemCong == null || diemCong.getId() == null || diemCong.getId() <= 0) {
            return BUSResult.error("ID điểm cộng không hợp lệ!");
        }

        DiemCong existing = diemCongDAO.findById(diemCong.getId());
        if (existing == null) {
            return BUSResult.error("Không tìm thấy điểm cộng này trong hệ thống!");
        }

        if (diemCong.getDiemCc() != null) existing.setDiemCc(diemCong.getDiemCc());
        if (diemCong.getDiemUtxt() != null) existing.setDiemUtxt(diemCong.getDiemUtxt());
        if (diemCong.getDiemTong() != null) existing.setDiemTong(diemCong.getDiemTong());
        if (diemCong.getGhiChu() != null) existing.setGhiChu(diemCong.getGhiChu());
        if (diemCong.getPhuongThuc() != null) existing.setPhuongThuc(diemCong.getPhuongThuc());

        boolean isUpdated = diemCongDAO.update(existing);
        if (isUpdated) {
            return BUSResult.success("Cập nhật điểm cộng thành công!");
        } else {
            return BUSResult.error("Cập nhật điểm cộng thất bại!");
        }
    }

    public BUSResult<DiemCong> deleteDiemCong(DiemCong diemCong) {
        if (diemCong == null || diemCong.getId() == null || diemCong.getId() <= 0) {
            return BUSResult.error("ID điểm cộng không hợp lệ!");
        }

        DiemCong toDelete = diemCongDAO.findById(diemCong.getId());
        if (toDelete == null) {
            return BUSResult.error("Không tìm thấy điểm cộng này trong hệ thống!");
        }

        boolean isDeleted = diemCongDAO.delete(toDelete);
        if (isDeleted) {
            return BUSResult.success("Xóa điểm cộng thành công!");
        } else {
            return BUSResult.error("Xóa điểm cộng thất bại!");
        }
    }
}
