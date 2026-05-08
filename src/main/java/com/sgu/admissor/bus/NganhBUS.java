/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.bus;

import com.google.inject.Inject;
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

    public BUSResult<Nganh> addNganh(Nganh newNganh) {
        if (newNganh == null) {
            return BUSResult.error("Thông tin ngành không hợp lệ!");
        }
        if (newNganh.getMaNganh() == null || newNganh.getMaNganh().trim().isEmpty()) {
            return BUSResult.error("Mã ngành không được để trống!");
        }
        if (newNganh.getTenNganh() == null || newNganh.getTenNganh().trim().isEmpty()) {
            return BUSResult.error("Tên ngành không được để trống!");
        }

        if (nganhDAO.findByMaNganh(newNganh.getMaNganh()) != null) {
            return BUSResult.error("Mã ngành đã tồn tại trong hệ thống!");
        }

        boolean isInserted = nganhDAO.insert(newNganh);
        if (isInserted) {
            return BUSResult.success("Thêm ngành mới thành công!");
        } else {
            return BUSResult.error("Thêm ngành thất bại!");
        }
    }

    public BUSResult<Nganh> updateNganh(Nganh nganh) {
        if (nganh == null || nganh.getId() == null || nganh.getId() <= 0) {
            return BUSResult.error("ID ngành không hợp lệ!");
        }

        Nganh existing = nganhDAO.findById(nganh.getId());
        if (existing == null) {
            return BUSResult.error("Không tìm thấy ngành này trong hệ thống!");
        }

        if (nganh.getTenNganh() != null && !nganh.getTenNganh().trim().isEmpty()) {
            existing.setTenNganh(nganh.getTenNganh());
        }
        if (nganh.getChiTieu() != null) existing.setChiTieu(nganh.getChiTieu());
        if (nganh.getDiemSan() != null) existing.setDiemSan(nganh.getDiemSan());
        if (nganh.getDiemTrungTuyen() != null) existing.setDiemTrungTuyen(nganh.getDiemTrungTuyen());
        if (nganh.getTuyenThang() != null) existing.setTuyenThang(nganh.getTuyenThang());
        if (nganh.getDgnl() != null) existing.setDgnl(nganh.getDgnl());
        if (nganh.getThpt() != null) existing.setThpt(nganh.getThpt());
        if (nganh.getVsat() != null) existing.setVsat(nganh.getVsat());
        if (nganh.getSlXtt() != null) existing.setSlXtt(nganh.getSlXtt());
        if (nganh.getSlDgnl() != null) existing.setSlDgnl(nganh.getSlDgnl());
        if (nganh.getSlVsat() != null) existing.setSlVsat(nganh.getSlVsat());
        if (nganh.getSlThpt() != null) existing.setSlThpt(nganh.getSlThpt());

        boolean isUpdated = nganhDAO.update(existing);
        if (isUpdated) {
            return BUSResult.success("Cập nhật ngành thành công!");
        } else {
            return BUSResult.error("Cập nhật ngành thất bại!");
        }
    }

    public BUSResult<Nganh> deleteNganh(Nganh nganh) {
        if (nganh == null || nganh.getId() == null || nganh.getId() <= 0) {
            return BUSResult.error("ID ngành không hợp lệ!");
        }

        Nganh toDelete = nganhDAO.findById(nganh.getId());
        if (toDelete == null) {
            return BUSResult.error("Không tìm thấy ngành này trong hệ thống!");
        }

        boolean isDeleted = nganhDAO.delete(toDelete);
        if (isDeleted) {
            return BUSResult.success("Xóa ngành thành công!");
        } else {
            return BUSResult.error("Xóa ngành thất bại!");
        }
    }
}
