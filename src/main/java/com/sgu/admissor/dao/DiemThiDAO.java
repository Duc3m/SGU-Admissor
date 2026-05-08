/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.dao;

import com.sgu.admissor.entity.DiemThi;
import com.sgu.admissor.util.HibernateUtil;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.query.Query;

/**
 *
 * @author Admin
 */
public class DiemThiDAO extends GenericDAO<DiemThi> {

    public DiemThiDAO() {
        super(DiemThi.class);
    }

    public List<DiemThi> findByCccd(String cccd) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM DiemThi WHERE thiSinh.cccd = :cccdParam";
            Query<DiemThi> query = session.createQuery(hql, DiemThi.class);
            query.setParameter("cccdParam", cccd);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<DiemThi> findByPhuongThuc(String phuongThuc) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM DiemThi WHERE phuongThuc = :phuongThucParam";
            Query<DiemThi> query = session.createQuery(hql, DiemThi.class);
            query.setParameter("phuongThucParam", phuongThuc);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public DiemThi findByCccdAndPhuongThuc(String cccd, String phuongThuc) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM DiemThi WHERE thiSinh.cccd = :cccdParam AND phuongThuc = :phuongThucParam";
            Query<DiemThi> query = session.createQuery(hql, DiemThi.class);
            query.setParameter("cccdParam", cccd);
            query.setParameter("phuongThucParam", phuongThuc);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
