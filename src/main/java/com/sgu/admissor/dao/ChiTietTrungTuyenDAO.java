/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.dao;

import com.sgu.admissor.entity.NguyenVong;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author Duc3m
 */
public class ChiTietTrungTuyenDAO extends GenericDAO<NguyenVong> {

    public ChiTietTrungTuyenDAO() {
        super(NguyenVong.class);
    }

    public int countAdvanced(String cccd, String hoTen, String toHop, String maNganh,
                             BigDecimal diemMin, BigDecimal diemMax) {
        StringBuilder hql = new StringBuilder(
            "SELECT COUNT(nv.id) FROM NguyenVong nv " +
            "LEFT JOIN nv.thiSinh ts " +
            "LEFT JOIN nv.nganh n " +
            "WHERE nv.ketQua = :ketQua "
        );

        if (cccd != null && !cccd.trim().isEmpty()) {
            hql.append("AND ts.cccd LIKE :cccd ");
        }
        if (hoTen != null && !hoTen.trim().isEmpty()) {
            hql.append("AND ts.hoTen LIKE :hoTen ");
        }
        if (toHop != null && !toHop.equals("Tất cả")) {
            hql.append("AND nv.toHopMon = :toHop ");
        }
        if (maNganh != null && !maNganh.equals("Tất cả")) {
            hql.append("AND n.maNganh = :maNganh ");
        }
        if (diemMin != null) {
            hql.append("AND nv.diemXetTuyen IS NOT NULL AND nv.diemXetTuyen >= :diemMin ");
        }
        if (diemMax != null) {
            hql.append("AND nv.diemXetTuyen IS NOT NULL AND nv.diemXetTuyen <= :diemMax ");
        }

        var query = emProvider.get().createQuery(hql.toString(), Long.class);
        query.setParameter("ketQua", "PASSED");

        if (cccd != null && !cccd.trim().isEmpty()) {
            query.setParameter("cccd", "%" + cccd.trim() + "%");
        }
        if (hoTen != null && !hoTen.trim().isEmpty()) {
            query.setParameter("hoTen", "%" + hoTen.trim() + "%");
        }
        if (toHop != null && !toHop.equals("Tất cả")) {
            query.setParameter("toHop", toHop);
        }
        if (maNganh != null && !maNganh.equals("Tất cả")) {
            query.setParameter("maNganh", maNganh);
        }
        if (diemMin != null) {
            query.setParameter("diemMin", diemMin);
        }
        if (diemMax != null) {
            query.setParameter("diemMax", diemMax);
        }

        return query.getSingleResult().intValue();
    }

    public List<NguyenVong> searchAdvanced(String cccd, String hoTen, String toHop, String maNganh,
                                           BigDecimal diemMin, BigDecimal diemMax, int offset, int limit) {
        StringBuilder hql = new StringBuilder(
            "SELECT nv FROM NguyenVong nv " +
            "LEFT JOIN nv.thiSinh ts " +
            "LEFT JOIN nv.nganh n " +
            "WHERE nv.ketQua = :ketQua "
        );

        if (cccd != null && !cccd.trim().isEmpty()) {
            hql.append("AND ts.cccd LIKE :cccd ");
        }
        if (hoTen != null && !hoTen.trim().isEmpty()) {
            hql.append("AND ts.hoTen LIKE :hoTen ");
        }
        if (toHop != null && !toHop.equals("Tất cả")) {
            hql.append("AND nv.toHopMon = :toHop ");
        }
        if (maNganh != null && !maNganh.equals("Tất cả")) {
            hql.append("AND n.maNganh = :maNganh ");
        }
        if (diemMin != null) {
            hql.append("AND nv.diemXetTuyen IS NOT NULL AND nv.diemXetTuyen >= :diemMin ");
        }
        if (diemMax != null) {
            hql.append("AND nv.diemXetTuyen IS NOT NULL AND nv.diemXetTuyen <= :diemMax ");
        }

        hql.append("ORDER BY nv.diemXetTuyen DESC, nv.id DESC");

        var query = emProvider.get().createQuery(hql.toString(), NguyenVong.class);
        query.setParameter("ketQua", "PASSED");

        if (cccd != null && !cccd.trim().isEmpty()) {
            query.setParameter("cccd", "%" + cccd.trim() + "%");
        }
        if (hoTen != null && !hoTen.trim().isEmpty()) {
            query.setParameter("hoTen", "%" + hoTen.trim() + "%");
        }
        if (toHop != null && !toHop.equals("Tất cả")) {
            query.setParameter("toHop", toHop);
        }
        if (maNganh != null && !maNganh.equals("Tất cả")) {
            query.setParameter("maNganh", maNganh);
        }
        if (diemMin != null) {
            query.setParameter("diemMin", diemMin);
        }
        if (diemMax != null) {
            query.setParameter("diemMax", diemMax);
        }

        query.setFirstResult(offset);
        query.setMaxResults(limit);

        return query.getResultList();
    }
}
