/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.dao;

import com.sgu.admissor.entity.Nganh;
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
}
