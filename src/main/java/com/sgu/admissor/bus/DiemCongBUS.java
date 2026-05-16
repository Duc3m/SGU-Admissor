/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.bus;

import jakarta.inject.Inject;
import com.google.inject.persist.Transactional;
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
    
    @Transactional
    public int countAdvanced(String cccd, String maToHop, String phuongThuc) {
        try {
            return diemCongDAO.countAdvanced(cccd, maToHop, phuongThuc);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Transactional
    public BUSResult<List<Object[]>> searchAdvanced(String cccd, String maToHop, String phuongThuc, int page, int limit) {
        try {
            int offset = (page - 1) * limit;
            List<Object[]> list = diemCongDAO.searchAdvanced(cccd, maToHop, phuongThuc, offset, limit);
            return BUSResult.successWithData("Truy xuất điểm cộng thành công!", list);
        } catch (Exception e) {
            e.printStackTrace();
            return BUSResult.error("Lỗi khi lấy danh sách điểm cộng!");
        }
    }

    @Transactional
    public BUSResult<DiemCong> addDiemCong(DiemCong diemCong) {
        if (diemCong == null) {
            return BUSResult.error("Thông tin điểm cộng không hợp lệ!");
        }
        if (diemCong.getThiSinh() == null || diemCong.getThiSinh().getCccd() == null
                || diemCong.getThiSinh().getCccd().trim().isEmpty()) {
            return BUSResult.error("Thông tin thí sinh không hợp lệ!");
        }
        if (diemCong.getNganh() == null || diemCong.getNganh().getMaNganh() == null
                || diemCong.getNganh().getMaNganh().trim().isEmpty()) {
            return BUSResult.error("Mã ngành không được để trống!");
        }
        if (diemCong.getDcKey() != null && !diemCong.getDcKey().trim().isEmpty()) {
            if (diemCongDAO.findByDcKey(diemCong.getDcKey()) != null) {
                return BUSResult.error("Điểm cộng này đã tồn tại (trùng dc_key)!");
            }
        }
        if (diemCongDAO.insert(diemCong)) {
            return BUSResult.success("Thêm điểm cộng thành công!");
        }
        return BUSResult.error("Thêm điểm cộng thất bại!");
    }

    @Transactional
    public BUSResult<DiemCong> updateDiemCong(DiemCong diemCong) {
        if (diemCong == null || diemCong.getId() == null || diemCong.getId() <= 0) {
            return BUSResult.error("ID điểm cộng không hợp lệ!");
        }
        DiemCong existing = diemCongDAO.findById(diemCong.getId());
        if (existing == null) {
            return BUSResult.error("Không tìm thấy điểm cộng này trong hệ thống!");
        }
        existing.setThiSinh(diemCong.getThiSinh());
        existing.setNganh(diemCong.getNganh());
        existing.setToHop(diemCong.getToHop());
        existing.setPhuongThuc(diemCong.getPhuongThuc());
        existing.setDiemCc(diemCong.getDiemCc());
        existing.setDiemUtxt(diemCong.getDiemUtxt());
        existing.setDiemTong(diemCong.getDiemTong());
        existing.setGhiChu(diemCong.getGhiChu());
        existing.setDcKey(diemCong.getDcKey());

        if (diemCongDAO.update(existing)) {
            return BUSResult.success("Cập nhật điểm cộng thành công!");
        }
        return BUSResult.error("Cập nhật điểm cộng thất bại!");
    }

    @Transactional
    public BUSResult addListDiemCong(List<DiemCong> listDiemCong) {
        if (listDiemCong == null || listDiemCong.isEmpty()) {
            return BUSResult.error("Không có điểm cộng nào để add");
        }
        if (!diemCongDAO.insertBatch(listDiemCong)) {
            return BUSResult.error("Lỗi phương thức addListDiemCong");
        }
        return BUSResult.success("Thêm danh sách điểm cộng thành công!");
    }

    @Transactional
    public BUSResult updateBatchDiemCong(List<DiemCong> listDiemCong) {
        if (listDiemCong == null || listDiemCong.isEmpty()) {
            return BUSResult.error("Không có điểm cộng nào để update");
        }
        for (DiemCong dc : listDiemCong) {
            diemCongDAO.update(dc);
        }
        return BUSResult.success("Cập nhật danh sách điểm cộng thành công!");
    }

    @Transactional
    public BUSResult<DiemCong> deleteDiemCong(DiemCong diemCong) {
        if (diemCong == null || diemCong.getId() == null || diemCong.getId() <= 0) {
            return BUSResult.error("ID điểm cộng không hợp lệ!");
        }
        DiemCong existing = diemCongDAO.findById(diemCong.getId());
        if (existing == null) {
            return BUSResult.error("Không tìm thấy điểm cộng này trong hệ thống!");
        }
        if (diemCongDAO.delete(existing)) {
            return BUSResult.success("Xóa điểm cộng thành công!");
        }
        return BUSResult.error("Xóa điểm cộng thất bại!");
    }
}
