/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.dao;

import com.sgu.admissor.entity.NganhToHop;
import com.sgu.admissor.util.HibernateUtil;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.query.Query;

/**
 *
 * @author Admin
 */
public class NganhToHopDAO extends GenericDAO<NganhToHop> {

    public NganhToHopDAO() {
        super(NganhToHop.class);
    }

    public List<NganhToHop> findByMaNganh(String maNganh) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM NganhToHop WHERE nganh.maNganh = :maNganhParam";
            Query<NganhToHop> query = session.createQuery(hql, NganhToHop.class);
            query.setParameter("maNganhParam", maNganh);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<NganhToHop> findByMaToHop(String maToHop) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM NganhToHop WHERE toHop.maToHop = :maToHopParam";
            Query<NganhToHop> query = session.createQuery(hql, NganhToHop.class);
            query.setParameter("maToHopParam", maToHop);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public NganhToHop findByMaNganhAndMaToHop(String maNganh, String maToHop) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM NganhToHop WHERE nganh.maNganh = :maNganhParam AND toHop.maToHop = :maToHopParam";
            Query<NganhToHop> query = session.createQuery(hql, NganhToHop.class);
            query.setParameter("maNganhParam", maNganh);
            query.setParameter("maToHopParam", maToHop);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
