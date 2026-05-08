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
}
