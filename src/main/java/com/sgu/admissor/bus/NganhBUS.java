/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.bus;

import jakarta.inject.Inject;
import com.google.inject.persist.Transactional;
import com.sgu.admissor.dao.NganhDAO;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.Nganh;
import java.util.List;

/**
 *
 * @author Admin
 */
public class NganhBUS {

    private final NganhDAO nganhDAO;

    @Inject
    public NganhBUS(NganhDAO nganhDAO) {
        this.nganhDAO = nganhDAO;
    }

    public BUSResult<List<Nganh>> getAllNganh() {
        return BUSResult.successWithData("Lấy toàn bộ ngành thành công!", nganhDAO.findAll());
    }

    @Transactional
    public BUSResult<Nganh> getNganhById(Integer id) {
        if (id == null || id <= 0) {
            return BUSResult.error("ID ngành không hợp lệ!");
        }
        return BUSResult.successWithData("Lấy ngành thành công!", nganhDAO.findById(id));
    }

    public BUSResult<Nganh> getNganhByMaNganh(String maNganh) {
        if (maNganh == null || maNganh.trim().isEmpty()) {
            return BUSResult.error("Mã ngành không hợp lệ!");
        }
        return BUSResult.successWithData("Lấy ngành thành công!", nganhDAO.findByMaNganh(maNganh));
    }

    public BUSResult<List<Nganh>> getNganhByTenNganh(String tenNganh) {
        if (tenNganh == null || tenNganh.trim().isEmpty()) {
            return BUSResult.error("Tên ngành không hợp lệ!");
        }
        return BUSResult.successWithData("Lấy ngành thành công!", nganhDAO.findByTenNganh(tenNganh));
    }

    public BUSResult<List<Nganh>> getNganhTuyenThang() {
        return BUSResult.successWithData("Lấy ngành tuyển thẳng thành công!", nganhDAO.findByTuyenThang(true));
    }

    @Transactional
    public BUSResult<Nganh> addNganh(Nganh nganh) {
        if (nganh == null) {
            return BUSResult.error("Thông tin ngành không hợp lệ!");
        }
        if (nganh.getMaNganh() == null || nganh.getMaNganh().trim().isEmpty()) {
            return BUSResult.error("Mã ngành không được để trống!");
        }
        if (nganh.getTenNganh() == null || nganh.getTenNganh().trim().isEmpty()) {
            return BUSResult.error("Tên ngành không được để trống!");
        }
        if (nganhDAO.findByMaNganh(nganh.getMaNganh()) != null) {
            return BUSResult.error("Mã ngành đã tồn tại trong hệ thống!");
        }
        if (nganhDAO.insert(nganh)) {
            return BUSResult.success("Thêm ngành mới thành công!");
        }
        return BUSResult.error("Thêm ngành thất bại!");
    }
    
    @Transactional
    public BUSResult addListNganh(List<Nganh> listNganh) {
        if (listNganh == null || listNganh.size() == 0) {
            return BUSResult.error("Không có ngành nào để add");
        }
        if (!nganhDAO.insertBatch(listNganh)) {
            return BUSResult.error("Lỗi trong phương thức addListNganh");
        }
        return BUSResult.success("Thêm danh sách ngành thành công!");
    }

    @Transactional
    public BUSResult<Nganh> updateNganh(Nganh nganh) {
        if (nganh == null || nganh.getId() == null || nganh.getId() <= 0) {
            return BUSResult.error("ID ngành không hợp lệ!");
        }
        Nganh existing = nganhDAO.findById(nganh.getId());
        if (existing == null) {
            return BUSResult.error("Không tìm thấy ngành này trong hệ thống!");
        }
        existing.setMaNganh(nganh.getMaNganh());
        existing.setTenNganh(nganh.getTenNganh());
        existing.setToHopGoc(nganh.getToHopGoc());
        existing.setChiTieu(nganh.getChiTieu());
        existing.setDiemSan(nganh.getDiemSan());
        existing.setDiemTrungTuyen(nganh.getDiemTrungTuyen());
        existing.setTuyenThang(nganh.getTuyenThang());
        existing.setDgnl(nganh.getDgnl());
        existing.setThpt(nganh.getThpt());
        existing.setVsat(nganh.getVsat());
        existing.setSlXtt(nganh.getSlXtt());
        existing.setSlDgnl(nganh.getSlDgnl());
        existing.setSlVsat(nganh.getSlVsat());
        existing.setSlThpt(nganh.getSlThpt());

        if (nganhDAO.update(existing)) {
            return BUSResult.success("Cập nhật ngành thành công!");
        }
        return BUSResult.error("Cập nhật ngành thất bại!");
    }

    @Transactional
    public BUSResult<Nganh> deleteNganh(Nganh nganh) {
        if (nganh == null || nganh.getId() == null || nganh.getId() <= 0) {
            return BUSResult.error("ID ngành không hợp lệ!");
        }
        Nganh existing = nganhDAO.findById(nganh.getId());
        if (existing == null) {
            return BUSResult.error("Không tìm thấy ngành này trong hệ thống!");
        }
        if (nganhDAO.delete(existing)) {
            return BUSResult.success("Xóa ngành thành công!");
        }
        return BUSResult.error("Xóa ngành thất bại!");
    }
    
    @Transactional
    public int countAdvanced(String tieuChi, String giaTri, String maToHop) {
        try {
            return nganhDAO.countTotalAdvanced(tieuChi, giaTri, maToHop);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    @Transactional
    public BUSResult<List<Object[]>> searchAdvanced(String tieuChi, String giaTri, String maToHop, int page, int limit) {
        try {
            int offset = (page - 1) * limit;
            List<Object[]> list = nganhDAO.searchAdvancedWithCount(tieuChi, giaTri, maToHop, offset, limit);
            return BUSResult.successWithData("Truy xuất dữ liệu thành công!", list);
        } catch (Exception e) {
            e.printStackTrace();
            return BUSResult.error("Lỗi khi truy xuất dữ liệu Ngành!");
        }
    }
}
