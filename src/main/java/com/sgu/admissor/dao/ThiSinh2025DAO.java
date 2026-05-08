/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.dao;

import com.sgu.admissor.entity.ThiSinh2025;
import java.util.List;

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
