/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.dao;

import com.sgu.admissor.entity.ThiSinh2025;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Admin
 */
public class ThiSinh2025DAO extends GenericDAO<ThiSinh2025> {

    public ThiSinh2025DAO() {
        super(ThiSinh2025.class);
    }

    public ThiSinh2025 findByCccd(String cccd) {
        String hql = "FROM ThiSinh2025 ts WHERE ts.cccd = :cccd";
        return emProvider.get().createQuery(hql, ThiSinh2025.class)
                .setParameter("cccd", cccd)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }
    
    public int countAdvanced(String tieuChi, String giaTri, String doiTuong, String khuVuc) {
        Map<String, Object> params = new HashMap<>();
        String whereClause = buildWhereClause(tieuChi, giaTri, doiTuong, khuVuc, params);
        
        return countByCondition(whereClause, params); 
    }
    
    public List<ThiSinh2025> searchAdvanced(String tieuChi, String giaTri, String doiTuong, String khuVuc, int page, int limit) {
        Map<String, Object> params = new HashMap<>();
        String whereClause = buildWhereClause(tieuChi, giaTri, doiTuong, khuVuc, params);
        
        return findByCondition(whereClause, params, page, limit);
    }
    
    private String buildWhereClause(String tieuChi, String giaTri, String doiTuong, String khuVuc, Map<String, Object> params) {
        StringBuilder sql = new StringBuilder("WHERE 1=1 "); // 1=1 để dễ dàng nối thêm chữ AND phía sau

        if (doiTuong != null && !doiTuong.contains("Tất cả")) {
            if (doiTuong.equals("Không")) {
                // Tìm những người không có đối tượng ưu tiên (NULL hoặc rỗng)
                sql.append(" AND (e.doiTuong IS NULL OR e.doiTuong = '')");
            } else {
                // Tìm đúng đối tượng 1, 2, 3, 4
                sql.append(" AND e.doiTuong = :doiTuong");
                params.put("doiTuong", doiTuong.trim());
            }
        }

        // Lọc theo Khu vực
        if (khuVuc != null && !khuVuc.contains("Tất cả")) {
            sql.append(" AND e.khuVuc = :khuVuc");
            params.put("khuVuc", khuVuc.trim());
        }

        // Lọc theo Ô tìm kiếm Text
        if (giaTri != null && !giaTri.trim().isEmpty()) {
            if ("CCCD".equals(tieuChi)) {
                sql.append(" AND e.cccd LIKE :giaTri");
                params.put("giaTri", "%" + giaTri.trim() + "%");
            } 
            else if ("Họ và Tên".equals(tieuChi)) {
                sql.append(" AND e.hoTen LIKE :giaTri");
                params.put("giaTri", "%" + giaTri.trim() + "%");
            } 
            else if ("ID Thí sinh".equals(tieuChi)) {
                sql.append(" AND e.id = :idVal");
                try {
                    params.put("idVal", Integer.parseInt(giaTri.trim()));
                } catch (NumberFormatException e) {
                    params.put("idVal", -1); // Nếu nhập chữ vào ô ID thì cho gán ID = -1 để không tìm ra ai
                }
            }
        }
        return sql.toString();
    }

    public ThiSinh2025 findBySoBaoDanh(String soBaoDanh) {
        String hql = "FROM ThiSinh2025 ts WHERE ts.soBaoDanh = :soBaoDanh";
        return emProvider.get().createQuery(hql, ThiSinh2025.class)
                .setParameter("soBaoDanh", soBaoDanh)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    public List<ThiSinh2025> findByHoTen(String hoTen) {
        String hql = "FROM ThiSinh2025 ts WHERE ts.hoTen LIKE :hoTen";
        return emProvider.get().createQuery(hql, ThiSinh2025.class)
                .setParameter("hoTen", "%" + hoTen + "%")
                .getResultList();
    }
}
