/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.bus;

import com.google.inject.Inject;
import com.sgu.admissor.dao.DiemThiDAO;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.DiemThi;
import java.util.List;

/**
 *
 * @author Admin
 */
public class DiemThiBUS {
    private final DiemThiDAO diemThiDAO;

    @Inject
    public DiemThiBUS(DiemThiDAO diemThiDAO) {
        this.diemThiDAO = diemThiDAO;
    }

    public BUSResult<List<DiemThi>> getAllDiemThi() {
        return BUSResult.successWithData("Lấy toàn bộ điểm thi thành công!", diemThiDAO.findAll());
    }

    public BUSResult<DiemThi> getDiemThiById(Integer id) {
        if (id == null || id <= 0) {
            return BUSResult.error("ID điểm thi không hợp lệ!");
        }
        return BUSResult.successWithData("Lấy điểm thi thành công!", diemThiDAO.findById(id));
    }

    public BUSResult<List<DiemThi>> getDiemThiByCccd(String cccd) {
        if (cccd == null || cccd.trim().isEmpty()) {
            return BUSResult.error("CCCD không hợp lệ!");
        }
        return BUSResult.successWithData("Lấy điểm thi thành công!", diemThiDAO.findByCccd(cccd));
    }

    public BUSResult<List<DiemThi>> getDiemThiByPhuongThuc(String phuongThuc) {
        if (phuongThuc == null || phuongThuc.trim().isEmpty()) {
            return BUSResult.error("Phương thức không hợp lệ!");
        }
        return BUSResult.successWithData("Lấy điểm thi thành công!", diemThiDAO.findByPhuongThuc(phuongThuc));
    }

    public BUSResult<DiemThi> getDiemThiByCccdAndPhuongThuc(String cccd, String phuongThuc) {
        if (cccd == null || cccd.trim().isEmpty()) {
            return BUSResult.error("CCCD không hợp lệ!");
        }
        if (phuongThuc == null || phuongThuc.trim().isEmpty()) {
            return BUSResult.error("Phương thức không hợp lệ!");
        }
        return BUSResult.successWithData(
                "Lấy điểm thi thành công!",
                diemThiDAO.findByCccdAndPhuongThuc(cccd, phuongThuc));
    }

    public BUSResult<DiemThi> addDiemThi(DiemThi newDiemThi) {
        if (newDiemThi == null) {
            return BUSResult.error("Thông tin điểm thi không hợp lệ!");
        }
        if (newDiemThi.getThiSinh() == null || newDiemThi.getThiSinh().getCccd() == null
                || newDiemThi.getThiSinh().getCccd().trim().isEmpty()) {
            return BUSResult.error("Thông tin thí sinh không hợp lệ!");
        }
        if (newDiemThi.getPhuongThuc() == null || newDiemThi.getPhuongThuc().trim().isEmpty()) {
            return BUSResult.error("Phương thức thi không được để trống!");
        }

        DiemThi existing = diemThiDAO.findByCccdAndPhuongThuc(
                newDiemThi.getThiSinh().getCccd(), newDiemThi.getPhuongThuc());
        if (existing != null) {
            return BUSResult.error("Điểm thi theo phương thức này đã tồn tại cho thí sinh!");
        }

        boolean isInserted = diemThiDAO.insert(newDiemThi);
        if (isInserted) {
            return BUSResult.success("Thêm điểm thi thành công!");
        } else {
            return BUSResult.error("Thêm điểm thi thất bại!");
        }
    }

    public BUSResult<DiemThi> updateDiemThi(DiemThi diemThi) {
        if (diemThi == null || diemThi.getId() == null || diemThi.getId() <= 0) {
            return BUSResult.error("ID điểm thi không hợp lệ!");
        }

        DiemThi existing = diemThiDAO.findById(diemThi.getId());
        if (existing == null) {
            return BUSResult.error("Không tìm thấy điểm thi này trong hệ thống!");
        }

        // Cập nhật tất cả các điểm (cho phép null - không đổi)
        if (diemThi.getTo() != null) existing.setTo(diemThi.getTo());
        if (diemThi.getLi() != null) existing.setLi(diemThi.getLi());
        if (diemThi.getHo() != null) existing.setHo(diemThi.getHo());
        if (diemThi.getSi() != null) existing.setSi(diemThi.getSi());
        if (diemThi.getSu() != null) existing.setSu(diemThi.getSu());
        if (diemThi.getDi() != null) existing.setDi(diemThi.getDi());
        if (diemThi.getVa() != null) existing.setVa(diemThi.getVa());
        if (diemThi.getN1Thi() != null) existing.setN1Thi(diemThi.getN1Thi());
        if (diemThi.getN1Cc() != null) existing.setN1Cc(diemThi.getN1Cc());
        if (diemThi.getCncn() != null) existing.setCncn(diemThi.getCncn());
        if (diemThi.getCnnn() != null) existing.setCnnn(diemThi.getCnnn());
        if (diemThi.getTi() != null) existing.setTi(diemThi.getTi());
        if (diemThi.getKtpl() != null) existing.setKtpl(diemThi.getKtpl());
        if (diemThi.getNl1() != null) existing.setNl1(diemThi.getNl1());
        if (diemThi.getNk1() != null) existing.setNk1(diemThi.getNk1());
        if (diemThi.getNk2() != null) existing.setNk2(diemThi.getNk2());

        boolean isUpdated = diemThiDAO.update(existing);
        if (isUpdated) {
            return BUSResult.success("Cập nhật điểm thi thành công!");
        } else {
            return BUSResult.error("Cập nhật điểm thi thất bại!");
        }
    }

    public BUSResult<DiemThi> deleteDiemThi(DiemThi diemThi) {
        if (diemThi == null || diemThi.getId() == null || diemThi.getId() <= 0) {
            return BUSResult.error("ID điểm thi không hợp lệ!");
        }

        DiemThi toDelete = diemThiDAO.findById(diemThi.getId());
        if (toDelete == null) {
            return BUSResult.error("Không tìm thấy điểm thi này trong hệ thống!");
        }

        boolean isDeleted = diemThiDAO.delete(toDelete);
        if (isDeleted) {
            return BUSResult.success("Xóa điểm thi thành công!");
        } else {
            return BUSResult.error("Xóa điểm thi thất bại!");
        }
    }
}
