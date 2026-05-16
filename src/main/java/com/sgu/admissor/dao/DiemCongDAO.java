/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.dao;

import com.sgu.admissor.entity.DiemCong;
import java.util.List;

/**
 *
 * @author Admin
 */
public class DiemCongDAO extends GenericDAO<DiemCong> {

    public DiemCongDAO() {
        super(DiemCong.class);
    }

    public List<DiemCong> findByCccd(String cccd) {
        String hql = "FROM DiemCong dc WHERE dc.thiSinh.cccd = :cccd";
        return emProvider.get().createQuery(hql, DiemCong.class)
                .setParameter("cccd", cccd)
                .getResultList();
    }

    public DiemCong findByDcKey(String dcKey) {
        String hql = "FROM DiemCong dc WHERE dc.dcKey = :dcKey";
        return emProvider.get().createQuery(hql, DiemCong.class)
                .setParameter("dcKey", dcKey)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    public List<DiemCong> findByMaNganh(String maNganh) {
        String hql = "FROM DiemCong dc WHERE dc.nganh.maNganh = :maNganh";
        return emProvider.get().createQuery(hql, DiemCong.class)
                .setParameter("maNganh", maNganh)
                .getResultList();
    }
    
    // 1. Hàm tìm kiếm phân trang có JOIN lấy thứ tự nguyện vọng
    public List<Object[]> searchAdvanced(String cccd, String maToHop, String phuongThuc , int offset, int limit) {
        StringBuilder hql = new StringBuilder(
            "SELECT dc, nv.thuTu FROM DiemCong dc " +
            "LEFT JOIN NguyenVong nv ON dc.thiSinh.cccd = nv.thiSinh.cccd " +
            "AND dc.nganh.maNganh = nv.nganh.maNganh AND dc.phuongThuc = nv.phuongThuc " +
            "WHERE 1=1 "
        );

        if (cccd != null && !cccd.trim().isEmpty()) {
            hql.append("AND dc.thiSinh.cccd LIKE :cccd ");
        }
        if (maToHop != null && !maToHop.equals("Tất cả")) {
            hql.append("AND dc.toHop.maToHop = :maToHop ");
        }
        
        if (phuongThuc != null && !phuongThuc.equals("Tất cả")) {
            hql.append("AND dc.phuongThuc = :phuongThuc ");
        }

        hql.append("ORDER BY dc.id DESC");

        var query = emProvider.get().createQuery(hql.toString(), Object[].class);

        if (cccd != null && !cccd.trim().isEmpty()) {
            query.setParameter("cccd", "%" + cccd.trim() + "%");
        }
        if (maToHop != null && !maToHop.equals("Tất cả")) {
            query.setParameter("maToHop", maToHop);
        }
        
        if (phuongThuc != null && !phuongThuc.equals("Tất cả")) {
            query.setParameter("phuongThuc", phuongThuc);
        }

        query.setFirstResult(offset);
        query.setMaxResults(limit);

        return query.getResultList();
    }

    // 2. Hàm đếm tổng số bản ghi
    public int countAdvanced(String cccd, String maToHop, String phuongThuc) {
        StringBuilder hql = new StringBuilder("SELECT COUNT(dc.id) FROM DiemCong dc WHERE 1=1 ");
        
        if (cccd != null && !cccd.trim().isEmpty()) {
            hql.append("AND dc.thiSinh.cccd LIKE :cccd ");
        }
        if (maToHop != null && !maToHop.equals("Tất cả")) {
            hql.append("AND dc.toHop.maToHop = :maToHop ");
        }
        
        if (phuongThuc != null && !phuongThuc.equals("Tất cả")) {
            hql.append("AND dc.phuongThuc = :phuongThuc ");
        }

        var query = emProvider.get().createQuery(hql.toString(), Long.class);

        if (cccd != null && !cccd.trim().isEmpty()) {
            query.setParameter("cccd", "%" + cccd.trim() + "%");
        }
        if (maToHop != null && !maToHop.equals("Tất cả")) {
            query.setParameter("maToHop", maToHop);
        }
        
        if (phuongThuc != null && !phuongThuc.equals("Tất cả")) {
            query.setParameter("phuongThuc", phuongThuc);
        }

        return ((Long) query.getSingleResult()).intValue();
    }
}
