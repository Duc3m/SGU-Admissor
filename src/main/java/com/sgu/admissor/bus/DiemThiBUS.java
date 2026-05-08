/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.bus;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
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
        return BUSResult.successWithData("Lấy điểm thi thành công!", diemThiDAO.findByCccdAndPhuongThuc(cccd, phuongThuc));
    }

    @Transactional
    public BUSResult<DiemThi> addDiemThi(DiemThi diemThi) {
        if (diemThi == null) {
            return BUSResult.error("Thông tin điểm thi không hợp lệ!");
        }
        if (diemThi.getThiSinh() == null || diemThi.getThiSinh().getCccd() == null
                || diemThi.getThiSinh().getCccd().trim().isEmpty()) {
            return BUSResult.error("Thông tin thí sinh không hợp lệ!");
        }
        if (diemThi.getPhuongThuc() == null || diemThi.getPhuongThuc().trim().isEmpty()) {
            return BUSResult.error("Phương thức thi không được để trống!");
        }
        if (diemThiDAO.findByCccdAndPhuongThuc(diemThi.getThiSinh().getCccd(), diemThi.getPhuongThuc()) != null) {
            return BUSResult.error("Điểm thi theo phương thức này đã tồn tại cho thí sinh!");
        }
        if (diemThiDAO.insert(diemThi)) {
            return BUSResult.success("Thêm điểm thi thành công!");
        }
        return BUSResult.error("Thêm điểm thi thất bại!");
    }

    @Transactional
    public BUSResult<DiemThi> updateDiemThi(DiemThi diemThi) {
        if (diemThi == null || diemThi.getId() == null || diemThi.getId() <= 0) {
            return BUSResult.error("ID điểm thi không hợp lệ!");
        }
        DiemThi existing = diemThiDAO.findById(diemThi.getId());
        if (existing == null) {
            return BUSResult.error("Không tìm thấy điểm thi này trong hệ thống!");
        }
        existing.setThiSinh(diemThi.getThiSinh());
        existing.setPhuongThuc(diemThi.getPhuongThuc());
        existing.setTo(diemThi.getTo());
        existing.setLi(diemThi.getLi());
        existing.setHo(diemThi.getHo());
        existing.setSi(diemThi.getSi());
        existing.setSu(diemThi.getSu());
        existing.setDi(diemThi.getDi());
        existing.setVa(diemThi.getVa());
        existing.setN1Thi(diemThi.getN1Thi());
        existing.setN1Cc(diemThi.getN1Cc());
        existing.setCncn(diemThi.getCncn());
        existing.setCnnn(diemThi.getCnnn());
        existing.setTi(diemThi.getTi());
        existing.setKtpl(diemThi.getKtpl());
        existing.setNl1(diemThi.getNl1());
        existing.setNk1(diemThi.getNk1());
        existing.setNk2(diemThi.getNk2());

        if (diemThiDAO.update(existing)) {
            return BUSResult.success("Cập nhật điểm thi thành công!");
        }
        return BUSResult.error("Cập nhật điểm thi thất bại!");
    }

    @Transactional
    public BUSResult<DiemThi> deleteDiemThi(DiemThi diemThi) {
        if (diemThi == null || diemThi.getId() == null || diemThi.getId() <= 0) {
            return BUSResult.error("ID điểm thi không hợp lệ!");
        }
        DiemThi existing = diemThiDAO.findById(diemThi.getId());
        if (existing == null) {
            return BUSResult.error("Không tìm thấy điểm thi này trong hệ thống!");
        }
        if (diemThiDAO.delete(existing)) {
            return BUSResult.success("Xóa điểm thi thành công!");
        }
        return BUSResult.error("Xóa điểm thi thất bại!");
    }
}
