/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.dao;

import com.sgu.admissor.entity.DiemThi;
import java.util.List;

/**
 *
 * @author Admin
 */
public class DiemThiDAO extends GenericDAO<DiemThi> {

    public DiemThiDAO() {
        super(DiemThi.class);
    }

    public List<DiemThi> findByCccd(String cccd) {
        String hql = "FROM DiemThi dt WHERE dt.thiSinh.cccd = :cccd";
        return emProvider.get().createQuery(hql, DiemThi.class)
                .setParameter("cccd", cccd)
                .getResultList();
    }

    public List<DiemThi> findByPhuongThuc(String phuongThuc) {
        String hql = "FROM DiemThi dt WHERE dt.phuongThuc = :phuongThuc";
        return emProvider.get().createQuery(hql, DiemThi.class)
                .setParameter("phuongThuc", phuongThuc)
                .getResultList();
    }

    public DiemThi findByCccdAndPhuongThuc(String cccd, String phuongThuc) {
        String hql = "FROM DiemThi dt WHERE dt.thiSinh.cccd = :cccd AND dt.phuongThuc = :phuongThuc";
        return emProvider.get().createQuery(hql, DiemThi.class)
                .setParameter("cccd", cccd)
                .setParameter("phuongThuc", phuongThuc)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }
}
