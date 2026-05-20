/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.dao;

import com.sgu.admissor.entity.NganhToHop;
import java.util.List;

/**
 *
 * @author Admin
 */
public class NganhToHopDAO extends GenericDAO<NganhToHop> {

    public NganhToHopDAO() {
        super(NganhToHop.class);
    }

    public List<NganhToHop> findByMaNganh(String maNganh) {
        String hql = "FROM NganhToHop nt WHERE nt.nganh.maNganh = :maNganh";
        return emProvider.get().createQuery(hql, NganhToHop.class)
                .setParameter("maNganh", maNganh)
                .getResultList();
    }

    public List<NganhToHop> findByMaToHop(String maToHop) {
        String hql = "FROM NganhToHop nt WHERE nt.toHop.maToHop = :maToHop";
        return emProvider.get().createQuery(hql, NganhToHop.class)
                .setParameter("maToHop", maToHop)
                .getResultList();
    }

    public NganhToHop findByMaNganhAndMaToHop(String maNganh, String maToHop) {
        String hql = "FROM NganhToHop nt WHERE nt.nganh.maNganh = :maNganh AND nt.toHop.maToHop = :maToHop";
        return emProvider.get().createQuery(hql, NganhToHop.class)
                .setParameter("maNganh", maNganh)
                .setParameter("maToHop", maToHop)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }
    
    public int deleteByMaNganh(String maNganh) {
        String hql = "DELETE FROM NganhToHop nt WHERE nt.nganh.maNganh = :maNganh";
        return emProvider.get().createQuery(hql)
                .setParameter("maNganh", maNganh)
                .executeUpdate(); // Trả về số lượng dòng đã xóa dưới DB
    }
}
