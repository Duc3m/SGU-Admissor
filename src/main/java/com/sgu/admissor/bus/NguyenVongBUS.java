/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.bus;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.sgu.admissor.dao.NguyenVongDAO;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.NguyenVong;
import java.util.List;

/**
 *
 * @author Admin
 */
public class NguyenVongBUS {

    private final NguyenVongDAO nguyenVongDAO;

    @Inject
    public NguyenVongBUS(NguyenVongDAO nguyenVongDAO) {
        this.nguyenVongDAO = nguyenVongDAO;
    }

    public BUSResult<List<NguyenVong>> getAllNguyenVong() {
        return BUSResult.successWithData("Lấy toàn bộ nguyện vọng thành công!", nguyenVongDAO.findAll());
    }

    @Transactional
    public BUSResult<NguyenVong> getNguyenVongById(Integer id) {
        if (id == null || id <= 0) {
            return BUSResult.error("ID Nguyên vọng không hợp lệ!");
        }
        return BUSResult.successWithData("Lấy nguyện vọng thành công!", nguyenVongDAO.findById(id));
    }

    public BUSResult<List<NguyenVong>> getNguyenVongsByCccd(String cccd) {
        if (cccd == null || cccd.trim().isEmpty()) {
            return BUSResult.error("CCCD không hợp lệ!");
        }
        return BUSResult.successWithData("Lấy nguyện vọng thành công!", nguyenVongDAO.findByCccd(cccd));
    }
    
    @Transactional
    public int countAdvanced(String tieuChi, String giaTri, String phuongThuc, String toHop, String ketQua) {
        try {
            return nguyenVongDAO.countAdvanced(tieuChi, giaTri, phuongThuc, toHop, ketQua);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Transactional
    public BUSResult<List<NguyenVong>> searchAdvanced(String tieuChi, String giaTri, String phuongThuc, String toHop, String ketQua, int page, int limit) {
        try {
            int offset = (page - 1) * limit;
            List<NguyenVong> list = nguyenVongDAO.searchAdvanced(tieuChi, giaTri, phuongThuc, toHop, ketQua, offset, limit);
            return BUSResult.successWithData("Truy xuất nguyện vọng thành công!", list);
        } catch (Exception e) {
            e.printStackTrace();
            return BUSResult.error("Lỗi khi lấy danh sách nguyện vọng!");
        }
    }

    @Transactional
    public BUSResult<NguyenVong> addNguyenVong(NguyenVong nv) {
        if (nv == null || nv.getThiSinh() == null || nv.getThiSinh().getCccd() == null
                || nv.getThiSinh().getCccd().trim().isEmpty()) {
            return BUSResult.error("CCCD không hợp lệ!");
        }
        if (nv.getNganh() == null || nv.getNganh().getMaNganh() == null || nv.getNganh().getMaNganh().trim().isEmpty()) {
            return BUSResult.error("Mã ngành không hợp lệ!");
        }
        if (nv.getPhuongThuc() == null || nv.getPhuongThuc().trim().isEmpty()) {
            return BUSResult.error("Phương thức không hợp lệ!");
        }
        String nvKey = nv.getThiSinh().getCccd() + "_" + nv.getNganh().getMaNganh() + "_" + nv.getPhuongThuc();
        nv.setNvKey(nvKey);
        if (nguyenVongDAO.findByNvKey(nvKey) != null) {
            return BUSResult.error("Nguyện vọng này đã tồn tại (trùng CCCD, Ngành và Phương thức)!");
        }
        if (nguyenVongDAO.insert(nv)) {
            return BUSResult.success("Thêm nguyên vọng mới thành công!");
        }
        return BUSResult.error("Thêm nguyên vọng thất bại!");
    }
    
    @Transactional
    public BUSResult addListNguyenVong(List<NguyenVong> nvList){
        if (nvList == null || nvList.size() == 0) {
            return BUSResult.error("Không có nguyện vọng nào để add");
        }
        
        for(NguyenVong nv : nvList){
            String nvkey = nv.getThiSinh().getCccd() + "_" + nv.getNganh().getMaNganh() + "_" + nv.getPhuongThuc();
            nv.setNvKey(nvkey);
        }
        
        if (!nguyenVongDAO.insertBatch(nvList)) {
            return BUSResult.error("Lỗi trong phương thức addNguyenVong");
        }
        return BUSResult.success("Thêm danh sách nguyện vọng thành công!");
    }

    @Transactional
    public BUSResult<NguyenVong> updateNguyenVong(NguyenVong nv) {
        if (nv == null || nv.getId() == null || nv.getId() <= 0) {
            return BUSResult.error("ID nguyện vọng không hợp lê!");
        }
        NguyenVong existing = nguyenVongDAO.findById(nv.getId());
        if (existing == null) {
            return BUSResult.error("Không tìm thấy nguyện vọng này trong hệ thống!");
        }
        if (nv.getThiSinh() == null || nv.getThiSinh().getCccd() == null || nv.getThiSinh().getCccd().trim().isEmpty()) {
            return BUSResult.error("CCCD không hợp lệ!");
        }
        if (nv.getNganh() == null || nv.getNganh().getMaNganh() == null || nv.getNganh().getMaNganh().trim().isEmpty()) {
            return BUSResult.error("Mã ngành không hợp lệ!");
        }
        if (nv.getPhuongThuc() == null || nv.getPhuongThuc().trim().isEmpty()) {
            return BUSResult.error("Phương thức không hợp lệ!");
        }
        String nvKey = nv.getThiSinh().getCccd() + "_" + nv.getNganh().getMaNganh() + "_" + nv.getPhuongThuc();
        NguyenVong duplicate = nguyenVongDAO.findByNvKey(nvKey);
        if (duplicate != null && (duplicate.getId() == null || !duplicate.getId().equals(nv.getId()))) {
            return BUSResult.error("Lỗi cập nhật: Nguyện vọng đã tồn tại!");
        }
        existing.setThiSinh(nv.getThiSinh());
        existing.setNganh(nv.getNganh());
        existing.setThuTu(nv.getThuTu());
        existing.setDiemThxt(nv.getDiemThxt());
        existing.setDiemUtqd(nv.getDiemUtqd());
        existing.setDiemCong(nv.getDiemCong());
        existing.setDiemXetTuyen(nv.getDiemXetTuyen());
        existing.setKetQua(nv.getKetQua());
        existing.setPhuongThuc(nv.getPhuongThuc());
        existing.setToHopMon(nv.getToHopMon());
        existing.setNvKey(nvKey);

        if (nguyenVongDAO.update(existing)) {
            return BUSResult.success("Cập nhật nguyện vọng thành công!");
        }
        return BUSResult.error("Cập nhật nguyện vọng thất bại!");
    }

    @Transactional
    public BUSResult deleteNguyenVong(NguyenVong nv) {
        if (nv == null || nv.getId() == null || nv.getId() <= 0) {
            return BUSResult.error("ID nguyện vọng không hợp lê!");
        }
        NguyenVong existing = nguyenVongDAO.findById(nv.getId());
        if (existing == null) {
            return BUSResult.error("Không tìm thấy nguyện vọng này trong hệ thống!");
        }
        if (nguyenVongDAO.delete(existing)) {
            return BUSResult.success("Xóa nguyện vọng thành công!");
        }
        return BUSResult.error("Xóa nguyện vọng thất bại!");
    }
}
