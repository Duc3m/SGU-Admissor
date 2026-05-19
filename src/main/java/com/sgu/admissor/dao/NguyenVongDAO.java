/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.dao;

import com.sgu.admissor.entity.NguyenVong;
import java.util.List;

/**
 *
 * @author Admin
 */
public class NguyenVongDAO extends GenericDAO<NguyenVong>{
    
    public NguyenVongDAO() {
        super(NguyenVong.class);
    }
    
    
    public List<NguyenVong> findByCccd(String cccd){
        String hql = "FROM NguyenVong nv WHERE nv.thiSinh.cccd = :cccd";
        return emProvider.get().createQuery(hql, NguyenVong.class)
                .setParameter("cccd", cccd)
                .getResultList();
    }
    
    public NguyenVong findByNvKey(String nvKey) {
        String hql = "FROM NguyenVong nv WHERE nv.nvKey = :nvKey";
        return emProvider.get().createQuery(hql, NguyenVong.class)
                .setParameter("nvKey", nvKey)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }
    
    public List<NguyenVong> searchAdvanced(String tieuChi, String giaTri, String phuongThuc, String toHop, String ketQua, int offset, int limit) {
        StringBuilder hql = new StringBuilder(
            "SELECT nv FROM NguyenVong nv " +
            "LEFT JOIN nv.nganh n " +
            "LEFT JOIN nv.thiSinh ts " +
            "WHERE 1=1 "
        );

        if (giaTri != null && !giaTri.trim().isEmpty()) {
            if ("CCCD".equals(tieuChi)) {
                hql.append("AND ts.cccd LIKE :giaTri ");
            } else if ("Mã ngành".equals(tieuChi)) {
                hql.append("AND n.maNganh LIKE :giaTri ");
            } else if ("Tên ngành".equals(tieuChi)) {
                hql.append("AND n.tenNganh LIKE :giaTri ");
            }
        }

        if (phuongThuc != null && !phuongThuc.equals("Tất cả")) {
            hql.append("AND nv.phuongThuc = :phuongThuc ");
        }

        if (toHop != null && !toHop.equals("Tất cả")) {
            hql.append("AND nv.toHopMon = :toHop ");
        }

        if (ketQua != null && !ketQua.equals("Tất cả")) {
            if ("Chưa xét".equals(ketQua)) {
                hql.append("AND (nv.ketQua IS NULL OR nv.ketQua = '') ");
            } else {
                hql.append("AND nv.ketQua = :ketQua ");
            }
        }

        // Sắp xếp ID giảm dần để nguyện vọng mới nhất lên đầu
        hql.append("ORDER BY nv.id DESC");

        var query = emProvider.get().createQuery(hql.toString(), NguyenVong.class);
        
        if (giaTri != null && !giaTri.trim().isEmpty()) {
            query.setParameter("giaTri", "%" + giaTri.trim() + "%");
        }
        if (phuongThuc != null && !phuongThuc.equals("Tất cả")) {
            query.setParameter("phuongThuc", phuongThuc);
        }
        if (toHop != null && !toHop.equals("Tất cả")) {
            query.setParameter("toHop", toHop);
        }
        if (ketQua != null && !ketQua.equals("Tất cả") && !ketQua.equals("Chưa xét")) {
            query.setParameter("ketQua", ketQua);
        }

        query.setFirstResult(offset);
        query.setMaxResults(limit);

        return query.getResultList();
    }

    public List<NguyenVong> findByMaNganhSorted(String maNganh) {
        String hql = "FROM NguyenVong nv WHERE nv.nganh.maNganh = :maNganh "
                   + "ORDER BY nv.diemXetTuyen DESC, nv.thuTu ASC";
        return emProvider.get().createQuery(hql, NguyenVong.class)
                .setParameter("maNganh", maNganh)
                .getResultList();
    }

    public void updateBatch(List<NguyenVong> list, int batchSize) {
        var em = emProvider.get();
        for (int i = 0; i < list.size(); i++) {
            em.merge(list.get(i));
            if ((i + 1) % batchSize == 0) {
                em.flush();
                em.clear();
            }
        }
        if (list.size() % batchSize != 0) {
            em.flush();
            em.clear();
        }
    }

    public int countAdvanced(String tieuChi, String giaTri, String phuongThuc, String toHop, String ketQua) {
        StringBuilder hql = new StringBuilder(
            "SELECT COUNT(nv.id) FROM NguyenVong nv " +
            "LEFT JOIN nv.nganh n " +
            "LEFT JOIN nv.thiSinh ts " +
            "WHERE 1=1 "
        );

        if (giaTri != null && !giaTri.trim().isEmpty()) {
            if ("CCCD".equals(tieuChi)) {
                hql.append("AND ts.cccd LIKE :giaTri ");
            } else if ("Mã ngành".equals(tieuChi)) {
                hql.append("AND n.maNganh LIKE :giaTri ");
            } else if ("Tên ngành".equals(tieuChi)) {
                hql.append("AND n.tenNganh LIKE :giaTri ");
            }
        }

        if (phuongThuc != null && !phuongThuc.equals("Tất cả")) {
            hql.append("AND nv.phuongThuc = :phuongThuc ");
        }

        if (toHop != null && !toHop.equals("Tất cả")) {
            hql.append("AND nv.toHopMon = :toHop ");
        }

        if (ketQua != null && !ketQua.equals("Tất cả")) {
            if ("Chưa xét".equals(ketQua)) {
                hql.append("AND (nv.ketQua IS NULL OR nv.ketQua = '') ");
            } else {
                hql.append("AND nv.ketQua = :ketQua ");
            }
        }

        var query = emProvider.get().createQuery(hql.toString(), Long.class);
        
        if (giaTri != null && !giaTri.trim().isEmpty()) {
            query.setParameter("giaTri", "%" + giaTri.trim() + "%");
        }
        if (phuongThuc != null && !phuongThuc.equals("Tất cả")) {
            query.setParameter("phuongThuc", phuongThuc);
        }
        if (toHop != null && !toHop.equals("Tất cả")) {
            query.setParameter("toHop", toHop);
        }
        if (ketQua != null && !ketQua.equals("Tất cả") && !ketQua.equals("Chưa xét")) {
            query.setParameter("ketQua", ketQua);
        }

        return ((Long) query.getSingleResult()).intValue();
    }

    public List<Object[]> countPassedByNganhAndPhuongThuc() {
        String hql = "SELECT nv.nganh.maNganh, nv.phuongThuc, COUNT(nv.id) "
                   + "FROM NguyenVong nv "
                   + "WHERE nv.ketQua = :ketQua "
                   + "GROUP BY nv.nganh.maNganh, nv.phuongThuc";
        return emProvider.get().createQuery(hql, Object[].class)
                .setParameter("ketQua", "PASSED")
                .getResultList();
    }
}
