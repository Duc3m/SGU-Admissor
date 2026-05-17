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
    
    // 1. Lấy danh sách CCCD
    public List<Object[]> searchGroupedCandidates(String cccd, String maToHop, String phuongThuc, int offset, int limit) {
        StringBuilder hql = new StringBuilder("SELECT DISTINCT dc.thiSinh.cccd, dc.thiSinh.hoTen, COUNT(dc.id) FROM DiemCong dc WHERE 1=1 ");

        if (cccd != null && !cccd.trim().isEmpty()) {
            hql.append("AND dc.thiSinh.cccd LIKE :cccd ");
        }
        if (maToHop != null && !maToHop.equals("Tất cả")) {
            hql.append("AND dc.toHop.maToHop = :maToHop ");
        }
        if (phuongThuc != null && !phuongThuc.equals("Tất cả")) {
            hql.append("AND dc.phuongThuc = :phuongThuc ");
        }

        // Gom nhóm theo CCCD và Tên
        hql.append("GROUP BY dc.thiSinh.cccd, dc.thiSinh.hoTen ");
        hql.append("ORDER BY dc.thiSinh.cccd ASC");

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

    // 2. Đếm số lượng Thí sinh (để phân trang)
    public int countDistinctCccd(String cccd, String maToHop, String phuongThuc) {
        StringBuilder hql = new StringBuilder("SELECT COUNT(DISTINCT dc.thiSinh.cccd) FROM DiemCong dc WHERE 1=1 ");
        
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

        // ... Set parameters y hệt như hàm trên ...
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
