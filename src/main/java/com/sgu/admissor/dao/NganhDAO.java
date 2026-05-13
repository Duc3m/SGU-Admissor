/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.dao;

import com.sgu.admissor.entity.Nganh;
import jakarta.persistence.Query;
import java.util.List;

/**
 *
 * @author Admin
 */
public class NganhDAO extends GenericDAO<Nganh> {

    public NganhDAO() {
        super(Nganh.class);
    }

    public Nganh findByMaNganh(String maNganh) {
        String hql = "FROM Nganh n WHERE n.maNganh = :maNganh";
        return emProvider.get().createQuery(hql, Nganh.class)
                .setParameter("maNganh", maNganh)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    public List<Nganh> findByTenNganh(String tenNganh) {
        String hql = "FROM Nganh n WHERE n.tenNganh LIKE :tenNganh";
        return emProvider.get().createQuery(hql, Nganh.class)
                .setParameter("tenNganh", "%" + tenNganh + "%")
                .getResultList();
    }

    public List<Nganh> findByTuyenThang(boolean tuyenThang) {
        String hql = "FROM Nganh n WHERE n.tuyenThang = :tuyenThang";
        return emProvider.get().createQuery(hql, Nganh.class)
                .setParameter("tuyenThang", tuyenThang)
                .getResultList();
    }
    
    // Trả về mảng Object[] chứa: [Nganh, Long (Số lượng thí sinh)]
    public List<Object[]> searchAdvancedWithCount(String tieuChi, String giaTri, String maToHop, int offset, int limit) {
        StringBuilder hql = new StringBuilder(
            "SELECT n, COUNT(DISTINCT nv.thiSinh) " +
            "FROM Nganh n " +
            "LEFT JOIN NguyenVong nv ON n.maNganh = nv.nganh.maNganh "
        );

        if (maToHop != null && !maToHop.equals("Tất cả")) {
            hql.append("INNER JOIN NganhToHop nth ON n.maNganh = nth.nganh.maNganh ");
        }

        hql.append("WHERE 1=1 ");

        if (giaTri != null && !giaTri.trim().isEmpty()) {
            if (tieuChi.equals("Mã ngành")) {
                hql.append("AND n.maNganh LIKE :giaTri ");
            } else if (tieuChi.equals("Tên ngành")) {
                hql.append("AND n.tenNganh LIKE :giaTri ");
            }
        }

        if (maToHop != null && !maToHop.equals("Tất cả")) {
            hql.append("AND nth.toHop.maToHop = :maToHop ");
        }

        hql.append("GROUP BY n.id");

        // Sử dụng emProvider được protected từ GenericDAO
        var query = emProvider.get().createQuery(hql.toString(), Object[].class);
        
        if (giaTri != null && !giaTri.trim().isEmpty()) {
            query.setParameter("giaTri", "%" + giaTri.trim() + "%");
        }
        if (maToHop != null && !maToHop.equals("Tất cả")) {
            query.setParameter("maToHop", maToHop);
        }

        query.setFirstResult(offset);
        query.setMaxResults(limit);

        return query.getResultList();
    }

    // Đếm tổng số bản ghi trả về (không áp dụng limit/offset) để phân trang
    public int countTotalAdvanced(String tieuChi, String giaTri, String maToHop) {
        StringBuilder hql = new StringBuilder("SELECT COUNT(DISTINCT n.id) FROM Nganh n ");
        
        if (maToHop != null && !maToHop.equals("Tất cả")) {
            hql.append("INNER JOIN NganhToHop nth ON n.maNganh = nth.nganh.maNganh ");
        }
        
        hql.append("WHERE 1=1 ");

        if (giaTri != null && !giaTri.trim().isEmpty()) {
             if (tieuChi.equals("Mã ngành")) {
                hql.append("AND n.maNganh LIKE :giaTri ");
            } else if (tieuChi.equals("Tên ngành")) {
                hql.append("AND n.tenNganh LIKE :giaTri ");
            }
        }
        
        if (maToHop != null && !maToHop.equals("Tất cả")) {
            hql.append("AND nth.toHop.maToHop = :maToHop ");
        }

        var query = emProvider.get().createQuery(hql.toString(), Long.class);
        
        if (giaTri != null && !giaTri.trim().isEmpty()) {
            query.setParameter("giaTri", "%" + giaTri.trim() + "%");
        }
        if (maToHop != null && !maToHop.equals("Tất cả")) {
            query.setParameter("maToHop", maToHop);
        }

        return ((Long) query.getSingleResult()).intValue();
    }
}
