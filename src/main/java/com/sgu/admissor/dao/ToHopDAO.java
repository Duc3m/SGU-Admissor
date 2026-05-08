/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.dao;

import com.sgu.admissor.entity.ToHop;
import java.util.List;

/**
 *
 * @author Admin
 */
public class ToHopDAO extends GenericDAO<ToHop> {

    public ToHopDAO() {
        super(ToHop.class);
    }

    public ToHop findByMaToHop(String maToHop) {
        String hql = "FROM ToHop t WHERE t.maToHop = :maToHop";
        return emProvider.get().createQuery(hql, ToHop.class)
                .setParameter("maToHop", maToHop)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    public List<ToHop> findByTenToHop(String tenToHop) {
        String hql = "FROM ToHop t WHERE t.tenToHop LIKE :tenToHop";
        return emProvider.get().createQuery(hql, ToHop.class)
                .setParameter("tenToHop", "%" + tenToHop + "%")
                .getResultList();
    }
}
