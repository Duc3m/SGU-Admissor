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
}
