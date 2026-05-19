/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.bus;

import com.google.inject.persist.Transactional;
import com.sgu.admissor.dao.ChiTietTrungTuyenDAO;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.NguyenVong;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author Duc3m
 */
public class ChiTietTrungTuyenBUS {
    private final ChiTietTrungTuyenDAO chiTietTrungTuyenDAO;

    @Inject
    public ChiTietTrungTuyenBUS(ChiTietTrungTuyenDAO chiTietTrungTuyenDAO) {
        this.chiTietTrungTuyenDAO = chiTietTrungTuyenDAO;
    }

    @Transactional
    public int countAdvanced(String cccd, String hoTen, String toHop, String maNganh,
                             BigDecimal diemMin, BigDecimal diemMax) {
        return chiTietTrungTuyenDAO.countAdvanced(cccd, hoTen, toHop, maNganh, diemMin, diemMax);
    }

    @Transactional
    public BUSResult<List<NguyenVong>> searchAdvanced(String cccd, String hoTen, String toHop, String maNganh,
                                                     BigDecimal diemMin, BigDecimal diemMax,
                                                     int page, int limit) {
        int offset = (page - 1) * limit;
        List<NguyenVong> data = chiTietTrungTuyenDAO.searchAdvanced(cccd, hoTen, toHop, maNganh, diemMin, diemMax, offset, limit);
        return BUSResult.successWithData("Lọc thí sinh trúng tuyển thành công!", data);
    }
}
